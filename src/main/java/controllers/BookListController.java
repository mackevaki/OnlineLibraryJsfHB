package controllers;

import beans.Pager;
import beans.User;
import dao.impls.VoteService;
import db.BookService;
import entity.*;
import enums.SearchType;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;

import lombok.Getter;
import lombok.Setter;
import models.BookSearchValues;
import org.omnifaces.cdi.Eager;
import org.primefaces.PrimeFaces;
import org.primefaces.event.CloseEvent;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RateEvent;
import org.primefaces.model.LazyDataModel;
import org.primefaces.shaded.commons.io.IOUtils;

@Named("bookListController")
@SessionScoped
@Eager
@Getter @Setter
public class BookListController implements Serializable {
    public static final String BOOKS_PAGE = "books";
    private ResourceBundle bundle;
    
    private Book selectedBook;
    private long selectedAuthorId; // текущий автор книги из списка при редактировании книги
    private long selectedGenreId; // выбранный жанр
    private char selectedLetter; // выбранная буква алфавита
    
    private SearchType searchType = SearchType.TITLE;// хранит выбранный тип поиска
    private String searchString; // хранит поисковую строку

    private final Pager<Book> pager = Pager.getInstance();
    private final BookService bookService;

    private boolean editModeView; // отображение режима редактирования
    private boolean addModeView; // отображение режима добавления
    
    private User user; // текущий пользователь после логина
    private FacesContext facesContext; // доступ к контейнеру JSF (контексту)
    private LazyDataModel<Book> bookLazyDataModel; // постраничный доступ к книгам
    private BookSearchValues bookSearchValues; // данные для поика книг
   // сервисы доступа к БД
    private VoteService voteService;


    private byte[] uploadedImage; // сюда будет сохраняться загруженная пользователем новая обложка (при редактировании или при добавлении книги)
    private byte[] uploadedContent; // сюда будет сохраняться загруженный пользователем PDF контент (при редактировании или при добавлении книги)
    private String uploadedContentName; // сюда будет сохраняться имя файла для отображения на странице
    
    @Inject
    public BookListController(BookService bookService, FacesContext facesContext, LazyDataModel<Book> bookLazyDataModel, BookSearchValues bookSearchValues, User user, VoteService voteService) {
        this.bookService = bookService;
        this.facesContext = facesContext;
        this.bookLazyDataModel = bookLazyDataModel;
        this.bookSearchValues = bookSearchValues;
        this.user = user;
        this.voteService = voteService;
    }

    @PostConstruct
    public void init() {
        bundle = ResourceBundle.getBundle("nls.messages", facesContext.getViewRoot().getLocale());       
    }
    
    private void submitValues(Character selectedLetter, long selectedGenreId) {
        this.selectedLetter = selectedLetter;
        this.selectedGenreId = selectedGenreId;
    }
    
    private void fillBooksAll() {
        bookService.findAll();
    }

    public String fillBooksByGenre() {
        cancelEdit();
        
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();

        int genreId = Integer.parseInt(params.get("genre_id"));
        submitValues(' ', genreId);

        if (genreId == 0) {
            bookService.findAll();
        } else {
            bookService.getBooksByGenre(selectedGenreId);
        }

        updateBookList();     // обновить список книг и выделить выбранные жанр, букву

        return BOOKS_PAGE;
    }

    // обновить список книг и выделить выбранные жанр, букву
    public void updateBookList() {
        // Обновляем область страницы booksList, которая автоматом вызывает получение новых данных из BookLazyDataModel.
        // При поиске книг BookLazyDataModel учитывает все поисковые данные (жанр, выбранная буквы и пр.) - и получит уже отфильтрованные данные
        PrimeFaces.current().ajax().update("booksForm:booksList");


        // обновляем остальные области, которые нужно обновить после поиска (области заново считают значения переменных)
        PrimeFaces.current().ajax().update("lettersForm");
        PrimeFaces.current().ajax().update("genresForm");
    }

    public String fillBooksByLetter() {
        cancelEdit();
        
        Map<String, String> params = facesContext.getExternalContext().getRequestParameterMap();
        selectedLetter = params.get("letter").charAt(0);
        
        submitValues(selectedLetter, -1);
 
        bookService.getBooksByLetter(selectedLetter);
        
        return BOOKS_PAGE;
    }
    
    public String fillBooksBySearch() {       
        cancelEdit();
                
        submitValues(' ', -1);
        
        if (searchString.trim().isEmpty()) {
            fillBooksAll();
            return BOOKS_PAGE;
        }
        
        if (searchType == SearchType.AUTHOR) {
            bookService.getBooksByAuthor(searchString);
        } else if (searchType == SearchType.TITLE) {
            bookService.getBooksByName(searchString);
        }
        
        return BOOKS_PAGE;
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="edition mode">
    
    public ActionListener saveListener() {
        return (ActionEvent event) -> {
            if (!validateFields()) {
                return;
            }           
            
            if (editModeView) {
                bookService.update(selectedBook);
            } else if(addModeView) {
                bookService.add(selectedBook);
            }

            cancelEdit();
            bookService.populateList();

            PrimeFaces.current().executeScript("PF('dlgEditBook').hide()");

            facesContext.addMessage(null, new FacesMessage(bundle.getString("updated")));
        };
    }
    public void deleteBook() {
        bookService.delete(selectedBook);
        bookService.populateList();

        facesContext.addMessage(null, new FacesMessage(bundle.getString("deleted")));
    }    
    public void showEdit() {
        editModeView = true;
        PrimeFaces.current().executeScript("PF('dlgEditBook').show()");
    }
    
    public void switchAddMode() {
        addModeView = true;
        selectedBook = new Book();

        PrimeFaces.current().executeScript("PF('dlgEditBook').show()");
    }
     
    public void cancelEdit() {
        editModeView = false;
        addModeView = false;
        PrimeFaces.current().executeScript("PF('dlgEditBook').hide()");
    }
//</editor-fold>

    public Character[] getRussianLetters() {
        return new Character[]{'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'};
    }
   
    public void searchTypeChanged(ValueChangeEvent e) {
        searchType = (SearchType) e.getNewValue();
    }
    
    public void searchStringChanged(ValueChangeEvent e) {
        searchString = e.getNewValue().toString();
    }
    
//<editor-fold defaultstate="collapsed" desc="getters and setters">
/*    public boolean isEditModeView() {
        return editModeView;
    }
    
    public String getSearchString() {
        return searchString;
    }
    
    public void setSearchString(String searchString) {
        this.searchString = searchString;
    }
    
    public SearchType getSearchType() {
        return searchType;
    }
    
    public void setSearchType(SearchType searchType) {
        this.searchType = searchType;
    }
        
    public long getSelectedGenreId() {
        return selectedGenreId;
    }
    
    public void setSelectedGenreId(long selectedGenreId) {
        this.selectedGenreId = selectedGenreId;
    }
    
    public char getSelectedLetter() {
        return selectedLetter;
    }
    
    public void setSelectedLetter(char selectedLetter) {
        this.selectedLetter = selectedLetter;
    }

    public Long getSelectedAuthorId() {
        return selectedAuthorId;
    }

    public Pager<Book> getPager() {
        return pager;
    }
    
    public LazyDataModel<Book> getBookListModel() {
        return bookListModel;
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public void setSelectedBook(Book selectedBook) {
        this.selectedBook = selectedBook;
    }
            
    public boolean isAddModeView() {
        return addModeView;
    }*/
//</editor-fold>
    
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

        if (addModeView) {
            if (selectedBook.getContent() == null) {
                failValidation(bundle.getString("error_load_pdf"));
                return false;
            }

            if (selectedBook.getImage() == null) {
                failValidation(bundle.getString("error_load_image"));
                return false;
            }
        }

        return true;
    }

    public void rate(RateEvent rateEvent) {
        ExternalContext externalContext = facesContext.getExternalContext();

        Map<String, String> params = externalContext.getRequestParameterMap();
        int bookId = Integer.parseInt(params.get("bookId"));

        Book book = null;
        for(Book b : bookLazyDataModel.getWrappedData()) {
            if(b.getId() == bookId) {
                book = b;
            }
        }

        String username = externalContext.getUserPrincipal().getName();
        int votedRating = Integer.parseInt(rateEvent.getRating().toString());
        long voteCount = book.getTotalVoteCount() + 1;
        long rating = book.getTotalRating() + votedRating;
        int avgRating = calcAverageRating(rating, voteCount);

        book.setTotalVoteCount(voteCount);
        book.setAvgRating(avgRating);
        book.setTotalRating(rating);

        Vote vote = new Vote();
        vote.setBook(book);
        vote.setUsername(username);
        vote.setValue(votedRating);

        voteService.add(vote);
        bookService.updateRating(book);

        PrimeFaces.current().ajax().update("booksForm:booksList"); // обновляем список книг
    }

    private int calcAverageRating(long totalRating, long totalVoteCount) {
        if (totalRating == 0 || totalVoteCount == 0) {
            return 0;
        }

        int avgRating = Long.valueOf(totalRating / totalVoteCount).intValue();

        return avgRating;
    }

    private boolean isNullOrEmpty(Object obj) {
//        if (obj instanceof Genre) {
//            return ((Genre) obj).getName().trim().equals("");
//        }
//        if (obj instanceof Publisher) {
//            return ((Publisher) obj).getName().trim().equals("");
//        }
//        if (obj instanceof Author) {
//            return ((Author) obj).getFio().trim().equals("");
//        }        
        return obj == null || obj.toString().trim().equals("");
    }

    private void failValidation(String message) {
        facesContext.validationFailed();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, bundle.getString("error")));
    }

    // сохранение/обновление книги
    public void save() {
        // если было выбрано новое изображение
        if (uploadedImage != null) {
            selectedBook.setImage(uploadedImage);
        }

        // если был выбран новый PDF контент
        if (uploadedContent != null) {
            selectedBook.setContent(uploadedContent); // content нужно сохранять отдельно, т.к. изначально это поле не заполняется в book
        }

        if (selectedBook.getId() == null || selectedBook.getId() == 0) {
            bookService.add(selectedBook); // сохраняем/обновляем все данные (кроме контента)
        }else{
            bookService.update(selectedBook); // сохраняем/обновляем все данные (кроме контента)
        }

        // обновить области на странице
        PrimeFaces.current().executeScript("PF('dlgEditBook').hide()"); // скрыть диалоговое окно
        PrimeFaces.current().ajax().update("booksForm:booksList"); // обновить список книг
    }

    // загрузить картинку для обложки по-умолчанию
    private byte[] loadDefaultIcon() {
        InputStream stream = facesContext.getExternalContext().getResourceAsStream("/resources/images/no-cover.jpg");
        try {
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // загрузить PDF для книги по-умолчанию
    private byte[] loadDefaultPDF() {
        InputStream stream = facesContext.getExternalContext().getResourceAsStream("/resources/default-content.pdf");
        try {
            return IOUtils.toByteArray(stream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    // при закрытии диалогового окна - очищать загруженный контент из переменной
    public void onCloseDialog(CloseEvent event) {
        // обнулить все загруженные значения
        uploadedContent = null;
        uploadedImage = null;
        uploadedContentName = null;
    }


    // при загрузке обложки - она будет сохраняться в переменную uploadedImage
    public void uploadImage(FileUploadEvent event) {
        if (event.getFile() != null) {
            uploadedImage = event.getFile().getContent();
        }
    }

    // при загрузке PDF контента - он будет сохраняться в переменную uploadedContent
    public void uploadContent(FileUploadEvent event) {
        if (event.getFile() != null) {
            uploadedContent = event.getFile().getContent();

            // название книги для отображения пользователю после загрузки
            uploadedContentName = event.getFile().getFileName();
            if (uploadedContentName.length() > 20) {
                uploadedContentName = uploadedContentName.substring(20).concat("...");
            }
        }
    }
}
