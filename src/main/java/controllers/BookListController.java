package controllers;

import beans.Pager;
import db.DataHelper;
import entity.Book;
import enums.SearchType;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Map;
import java.util.ResourceBundle;
import models.BookListDataModel;
import org.omnifaces.cdi.Eager;
import org.primefaces.PrimeFaces;
import org.primefaces.model.LazyDataModel;

@Named("bookListController")
@SessionScoped
@Eager
public class BookListController implements Serializable {
    private Book selectedBook;
    private Long selectedAuthorId; // текущий автор книги из списка при редактировании книги
    
    private long selectedGenreId; // выбранный жанр
    private char selectedLetter; // выбранная буква алфавита
    private SearchType searchType = SearchType.TITLE;// хранит выбранный тип поиска
    private String searchString; // хранит поисковую строку

    private final Pager<Book> pager = Pager.getInstance();
    private DataHelper dataHelper = DataHelper.getInstance();
    private LazyDataModel<Book> bookListModel;
    
    private boolean editModeView; // отображение режима редактирования
    
    public BookListController() {
        bookListModel = new BookListDataModel();                       
    }

    private void submitValues(Character selectedLetter, long selectedGenreId) {
        this.selectedLetter = selectedLetter;
        this.selectedGenreId = selectedGenreId;
    }
    
    private void fillBooksAll() {
        dataHelper.getAllBooks();
    }

    public String fillBooksByGenre() {
        cancelEdit();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
        submitValues(' ', Integer.valueOf(params.get("genre_id")));
        
        dataHelper.getBooksByGenre(selectedGenreId);
        
        return "books";
    }
    
    public String fillBooksByLetter() {
        cancelEdit();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedLetter = params.get("letter").charAt(0);
        
        submitValues(selectedLetter, -1);
 
        dataHelper.getBooksByLetter(selectedLetter);
        
        return "books";
    }
    
    public String fillBooksBySearch() {       
        cancelEdit();
                
        submitValues(' ', -1);
        
        if (searchString.trim().length() == 0) {
            fillBooksAll();
            return "books";
        }
        
        if (searchType == SearchType.AUTHOR) {
            dataHelper.getBooksByAuthor(searchString);
        } else if (searchType == SearchType.TITLE) {
            dataHelper.getBooksByName(searchString);
        }
        
        return "books";
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="edition mode">
    
    public String updateBook() {  
        dataHelper.updateBook(selectedBook);
        cancelEdit();
        dataHelper.populateList();
        
        PrimeFaces.current().executeScript("PF('dlgEditBook').hide()");

        ResourceBundle bundle = ResourceBundle.getBundle("nls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("updated")));

        return "books";
    }
    public void deleteBook() {
        dataHelper.deleteBook(selectedBook);
        dataHelper.populateList();

        ResourceBundle bundle = ResourceBundle.getBundle("nls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString("deleted")));
    }    
    public void showEdit() {
        editModeView = true;
        PrimeFaces.current().executeScript("PF('dlgEditBook').show()");
    }
        
    public void cancelEdit() {
        editModeView = false;
        PrimeFaces.current().executeScript("PF('dlgEditBook').hide()");
    }
//</editor-fold>

    public Character[] getRussianLetters() {
        Character[] letters = new Character[]{'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'};
        return letters;
    }
   
    public void searchTypeChanged(ValueChangeEvent e) {
        searchType = (SearchType) e.getNewValue();
    }
    
    public void searchStringChanged(ValueChangeEvent e) {
        searchString = e.getNewValue().toString();
    }
    
//<editor-fold defaultstate="collapsed" desc="getters and setters">
    public boolean isEditModeView() {
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
            
//</editor-fold>
}
