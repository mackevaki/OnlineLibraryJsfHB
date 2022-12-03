package controllers;

import beans.Pager;
import db.DataHelper;
import entity.Book;
import enums.SearchType;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.faces.event.ValueChangeEvent;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.BookListDataModel;
import org.omnifaces.cdi.Eager;
import org.primefaces.model.LazyDataModel;

@Named("bookListController")
@SessionScoped
//@Eager
public class BookListController implements Serializable {
    private Long selectedAuthorId; // текущий автор книги из списка при редактировании книги
    private long selectedGenreId; // выбранный жанр
    private char selectedLetter; // выбранная буква алфавита
    private SearchType selectedSearchType = SearchType.TITLE;// хранит выбранный тип поиска
    private String searchString; // хранит поисковую строку
//    private List<Book> currentBookList; // текущий список книг для отображения

    private final Pager<Book> pager = Pager.getInstance();
    private DataHelper dataHelper = DataHelper.getInstance();
    private LazyDataModel<Book> bookListModel;
    
    private boolean editModeView; // отображение режима редактирования
    
//    private transient int row = -1; 
    
//    public int getRow() {
//        row += 1;
//        return row;
//    }
    
    public BookListController() {
//        fillBooksAll();
        bookListModel = new BookListDataModel();                       
    }
//    
//    private void submitValues(Character selectedLetter, int selectedPageNumber, int selectedGenreId) {
//        this.selectedLetter = selectedLetter;
//        pager.setSelectedPageNumber(selectedPageNumber);
//        this.selectedGenreId = selectedGenreId;
//    }
    private void submitValues(Character selectedLetter, long selectedGenreId) {
        this.selectedLetter = selectedLetter;
        this.selectedGenreId = selectedGenreId;
    }
    
    private void fillBooksAll() {
        //DataHelper.getInstance().getAllBooks(pager);
        dataHelper.getAllBooks();
    }

    public String fillBooksByGenre() {
//        row = -1;
        
//        immitateLoading();
        
        cancelEdit();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
        submitValues(' ', Integer.valueOf(params.get("genre_id")));
        
        dataHelper.getBooksByGenre(selectedGenreId);
        
        return "books";
    }
    
    public String fillBooksByLetter() {
//        row = -1;
        
//        immitateLoading();
        
        cancelEdit();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedLetter = params.get("letter").charAt(0);
        
        submitValues(selectedLetter, -1);
 
        dataHelper.getBooksByLetter(selectedLetter);
        
        return "books";
    }
    
    public String fillBooksBySearch() {
//        row = -1;
//        
//        immitateLoading();
        
        cancelEdit();
                
        submitValues(' ', -1);
        
        if (searchString.trim().length() == 0) {
            fillBooksAll();
            return "books";
        }
        
        if (selectedSearchType == SearchType.AUTHOR) {
            dataHelper.getBooksByAuthor(searchString);
        } else if (selectedSearchType == SearchType.TITLE) {
            dataHelper.getBooksByName(searchString);
        }
        
        return "books";
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="edition mode">
    
    public String updateBooks() {
//        immitateLoading();
//        
//        row = -1;
        
        dataHelper.update();
        
        cancelEdit();
        
//        dataHelper.refreshList();
        dataHelper.populateList();

        return "books";
    }
    
    public void showEdit() {
//        row = -1;
        editModeView = true;
    }
        
    public void cancelEdit() {
//        row = -1;   
        editModeView = false;
        for (Book b : pager.getList()) {
            b.setEdit(false);
        }
    }
//</editor-fold>


    public Character[] getRussianLetters() {
        Character[] letters = new Character[]{'А', 'Б', 'В', 'Г', 'Д', 'Е', 'Ё', 'Ж', 'З', 'И', 'Й', 'К', 'Л', 'М', 'Н', 'О', 'П', 'Р', 'С', 'Т', 'У', 'Ф', 'Х', 'Ц', 'Ч', 'Ш', 'Щ', 'Ъ', 'Ы', 'Ь', 'Э', 'Ю', 'Я'};
        return letters;
    }
//<editor-fold defaultstate="collapsed" desc="pager">
    
//    public void selectPage() {
//        row = -1;
//        cancelEdit();
//
//        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
//        pager.setSelectedPageNumber(Integer.parseInt(params.get("page_number")));
//
//        DataHelper.getInstance().setCurrentPager(pager);
//        DataHelper.getInstance().refreshList();
//    }
//    
//    public void booksOnPageChanged(ValueChangeEvent e) {
//        row = -1;
//        cancelEdit();
//        pager.setBooksOnPage(Integer.parseInt(e.getNewValue().toString()));
//        pager.setSelectedPageNumber(1);
//
//        DataHelper.getInstance().setCurrentPager(pager);
//        DataHelper.getInstance().refreshList();
//    }
//</editor-fold>
   
    public void searchTypeChanged(ValueChangeEvent e) {
        selectedSearchType = (SearchType) e.getNewValue();
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
    
    public SearchType getSelectedSearchType() {
        return selectedSearchType;
    }
    
    public void setSelectedSearchType(SearchType selectedSearchType) {
        this.selectedSearchType = selectedSearchType;
    }
    
//    public List<Book> getCurrentBookList() {
//        return currentBookList;
//    }
//   
    
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
    
    
    
//</editor-fold>
    
    private void immitateLoading() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
