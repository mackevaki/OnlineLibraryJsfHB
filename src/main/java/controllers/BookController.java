package controllers;

import beans.Pager;
import beans.User;
import dao.impls.VoteService;
import db.BookService;
import entity.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import models.BookSearchValues;
import org.omnifaces.cdi.Eager;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RateEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.shaded.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;

@Named("bookListController")
@SessionScoped
@Eager
@Getter @Setter
public class BookController implements Serializable {
    private Book selectedBook; // selected book for editing/adding or other actions

    private final Pager<Book> pager = new Pager<>();

    private FacesContext facesContext; // access to the JSF container context
    private LazyDataModel<Book> bookLazyDataModel; // page-by-page access to books
    private BookSearchValues bookSearchValues; // data for searching books
    private ResourceBundle bundle; // translations

    // database access services
    private VoteService voteService;
    private final BookService bookService;

    private byte[] uploadedImage; // a new cover uploaded by the user will be saved here (when editing or adding a book)
    private byte[] uploadedContent; // PDF content uploaded by the user will be saved here (when editing or adding a book)
    private String uploadedContentName; // the filename for display on the page will be saved here

    private User user; // current user after login
    private long selectedGenreId; // selected genre

    @Inject
    public BookController(BookService bookService, FacesContext facesContext, LazyDataModel<Book> bookLazyDataModel, BookSearchValues bookSearchValues, User user, VoteService voteService) {
        this.bookService = bookService;
        this.voteService = voteService;
        this.facesContext = facesContext;
        this.bookLazyDataModel = bookLazyDataModel;
        this.bookSearchValues = bookSearchValues;
        this.user = user;
    }

    @PostConstruct
    public void init() {
        bundle = ResourceBundle.getBundle("nls.messages", facesContext.getViewRoot().getLocale());       
    }

    // called when voting for a book
    public void rate(RateEvent rateEvent) {
        ExternalContext externalContext = facesContext.getExternalContext();

        Map<String, String> params = externalContext.getRequestParameterMap();
        int bookId = Integer.parseInt(params.get("bookId"));

        // look for the rated book in the lazyDataModel collection by id
        Book book = null;
        for(Book b : bookLazyDataModel.getWrappedData()) {
            if(b.getId() == bookId) {
                book = b;
            }
        }

        int votedRating = Integer.parseInt(rateEvent.getRating().toString()); // vote score given by the user

        assert book != null;

        long voteCount = book.getTotalVoteCount() + 1; // increase votes count
        long rating = book.getTotalRating() + votedRating;
        int avgRating = calcAverageRating(rating, voteCount); // get average rating

        // fill the book's rating fields by calculated values
        book.setTotalVoteCount(voteCount);
        book.setAvgRating(avgRating);
        book.setTotalRating(rating);

        // create vote with user credentials, rating and rated book
        Vote vote = new Vote();
        vote.setBook(book);
        vote.setUsername(user.getUsername());
        vote.setValue(votedRating);

        // persist the vote to DB
        voteService.add(vote);

        // update the book's rate in DB
        bookService.updateRating(book);

        facesContext.addMessage(null, new FacesMessage(bundle.getString("thanks_rating")));
        PrimeFaces.current().ajax().update("growlMessage");

        PrimeFaces.current().ajax().update("booksForm:booksList"); // update books list
    }

    private int calcAverageRating(long totalRating, long totalVoteCount) {
        if (totalRating == 0 || totalVoteCount == 0) {
            return 0;
        }

        return (int) (totalRating / totalVoteCount);
    }

    // Russian alphabet for display on the page
    public Character[] getRussianLetters() {
        return new Character[]{'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'};
    }

    // click a genre and display books of this genre
    public void selectGenre() {
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        long genreId = Long.parseLong(params.get("genre_id"));

        bookSearchValues.setGenreId(genreId);

        updateBookList();
    }

    // click a letter and display books starting with that letter
    public void selectLetter() {
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        char letter = params.get("letter").charAt(0);

        if (bookSearchValues.getLetter() != null && bookSearchValues.getLetter() == letter) { // 2nd click on already selected letter
            bookSearchValues.setLetter(null);
        } else { // select new letter
            bookSearchValues.setLetter(letter);
        }

        updateBookList();
    }

    // delete selected book
    public void delete() {
        bookService.delete(selectedBook);

        facesContext.addMessage(null, new FacesMessage(bundle.getString("deleted")));

        updateBookList();
    }

    // save new book or update existing book
    public void save() {
        if (!validateFields()) {
            return;
        }

        if (uploadedImage != null) {
            selectedBook.setImage(uploadedImage);
        }

        if (uploadedContent != null) {
            selectedBook.setContent(uploadedContent);
        }

        if (selectedBook.getId() == null || selectedBook.getId() == 0) {
            bookService.add(selectedBook);
        }else{
            bookService.update(selectedBook);
        }

        PrimeFaces.current().executeScript("PF('dlgEditBook').hide()");

        facesContext.addMessage(null, new FacesMessage(bundle.getString("updated")));

        PrimeFaces.current().ajax().update("booksForm:booksList");
    }

    private byte[] loadDefaultIcon() {
        InputStream stream = facesContext.getExternalContext().getResourceAsStream("/resources/images/no-cover.jpg");
        try {
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private byte[] loadDefaultPDF() {
        InputStream stream = facesContext.getExternalContext().getResourceAsStream("/resources/default-content.pdf");
        try {
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // clear the loaded content from the variable after closing the dialog
    public void onCloseDialog(ActionEvent event) {
        uploadedContent = null;
        uploadedImage = null;
        uploadedContentName = null;

        PrimeFaces.current().executeScript("PF('dlgEditBook').hide()");
    }

    public void uploadImage(FileUploadEvent event) {
        if (event.getFile() != null) {
            uploadedImage = event.getFile().getContent();
        }
    }

    public void uploadContent(FileUploadEvent event) {
        if (event.getFile() != null) {
            uploadedContent = event.getFile().getContent();

            // uploaded pdf name to display to the user after downloading
            uploadedContentName = event.getFile().getFileName();
            if (uploadedContentName.length() > 20) {
                uploadedContentName = uploadedContentName.substring(20).concat("...");
            }
        }
    }

    // update the list of books and highlight the selected genre, letter
    public void updateBookList() {
        // update the booksList page area to cause new data to be received from the BookLazyDataModel
        // when searching for books, BookLazyDataModel takes into account all search data (genre, selected letters etc.) - and will receive already filtered data
        PrimeFaces.current().ajax().update("booksForm:booksList");

        // update the remaining areas that need to be updated after the search to re-calculate the values of the variables
        PrimeFaces.current().ajax().update("lettersForm");
        PrimeFaces.current().ajax().update("genresForm");
    }

    public void confirmDeleteBook() {
        PrimeFaces.current().executeScript("PF('dlgDeleteBook').show()");
    }

    public void showEditDialog() {
        PrimeFaces.current().executeScript("PF('dlgEditBook').show()");
    }

    public void showAddDialog() {
        selectedBook = new Book();
        uploadedImage = loadDefaultIcon();
        uploadedContent = loadDefaultPDF();

//        PrimeFaces.current().ajax().update("booksForm:booksList");
        PrimeFaces.current().executeScript("PF('dlgEditBook').show()");
    }

    // checking book fields for emptiness
    private boolean validateFields() {
        if (isNullOrEmpty(selectedBook.getAuthor())
                || isNullOrEmpty(selectedBook.getDescr())
                || isNullOrEmpty(selectedBook.getGenre())
                || isNullOrEmpty(selectedBook.getIsbn())
                || isNullOrEmpty(selectedBook.getName())
                || isNullOrEmpty(selectedBook.getPageCount())
                || isNullOrEmpty(selectedBook.getPublishDate())
                || isNullOrEmpty(selectedBook.getPublisher())) {
            failValidation(bundle.getString("error_fill_all_fields"));
            return false;
        }

        if (bookService.isIsbnExists(selectedBook.getIsbn(), selectedBook.getId())){
            failValidation(bundle.getString("error_isbn_exist"));
            return false;
        }

        return true;
    }


    private boolean isNullOrEmpty(Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Genre) {
            return ((Genre) obj).getName().trim().isEmpty();
        }
        if (obj instanceof Publisher) {
            return ((Publisher) obj).getName().trim().isEmpty();
        }
        if (obj instanceof Author) {
            return ((Author) obj).getFio().trim().isEmpty();
        }

        return false;
    }

    private void failValidation(String message) {
        facesContext.validationFailed();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, bundle.getString("error")));
    }
}
