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
import org.omnifaces.cdi.Eager;

@Named("bookListController")
@SessionScoped
//@Eager
public class BookListController implements Serializable {
    private Long selectedAuthorId; // текущий автор книги из списка при редактировании книги
    private long selectedGenreId; // выбранный жанр
    private char selectedLetter; // выбранная буква алфавита
    private SearchType selectedSearchType = SearchType.TITLE;// хранит выбранный тип поиска
    private String searchString; // хранит поисковую строку
    private List<Book> currentBookList; // текущий список книг для отображения

    private final Pager<Book> pager = new Pager<>();
    
    private boolean editModeView; // отображение режима редактирования
    
    private transient int row = -1; 
    
    public int getRow() {
        row += 1;
        return row;
    }
    
    public BookListController() {
        fillBooksAll();
    }
    
    private void submitValues(Character selectedLetter, int selectedPageNumber, int selectedGenreId) {
        this.selectedLetter = selectedLetter;
        pager.setSelectedPageNumber(selectedPageNumber);
        this.selectedGenreId = selectedGenreId;
    }

    private void fillBooksAll() {
        DataHelper.getInstance().getAllBooks(pager);
    }

    public String fillBooksByGenre() {
        row = -1;
        
        immitateLoading();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        
        submitValues(' ', 1, Integer.valueOf(params.get("genre_id")));
        
        DataHelper.getInstance().getBooksByGenre(selectedGenreId, pager);
        
        return "books";
    }
    
    public String fillBooksByLetter() {
        row = -1;
        
        immitateLoading();
        
        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        selectedLetter = params.get("letter").charAt(0);
        
        submitValues(selectedLetter, 1, -1);
 
        DataHelper.getInstance().getBooksByLetter(selectedLetter, pager);
        
        return "books";
    }
    
    public String fillBooksBySearch() {
        row = -1;
        
        immitateLoading();
        
        submitValues(' ', 1, -1);
        
        if (searchString.trim().length() == 0) {
            fillBooksAll();
            return "books";
        }
        
        if (selectedSearchType == SearchType.AUTHOR) {
            DataHelper.getInstance().getBooksByAuthor(searchString, pager);
        } else if (selectedSearchType == SearchType.TITLE) {
            DataHelper.getInstance().getBooksByName(searchString, pager);
        }
        
        return "books";
    }
//</editor-fold>

//<editor-fold defaultstate="collapsed" desc="edition mode">
    
    public String updateBooks() {
        immitateLoading();

        cancelEdit();
        return "books";
    }
    
    public void showEdit() {
        editModeView = true;
    }
        
    public void cancelEdit() {
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
    private DataHelper dh = DataHelper.getInstance();
    
    public void selectPage() {
        row = -1;
        cancelEdit();

        Map<String, String> params = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        pager.setSelectedPageNumber(Integer.parseInt(params.get("page_number")));

        dh.setCurrentPager(pager);
        dh.runCurrentCriteria();
    }
    
    public void booksOnPageChanged(ValueChangeEvent e) {
        row = -1;
        cancelEdit();
        pager.setBooksOnPage(Integer.parseInt(e.getNewValue().toString()));
        pager.setSelectedPageNumber(1);

        dh.setCurrentPager(pager);
        dh.runCurrentCriteria();//        DataHelper.getInstance().runCurrentCriteria();
    }
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
    
    public List<Book> getCurrentBookList() {
        return currentBookList;
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
    
    
    
//</editor-fold>
    
    private void immitateLoading() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(BookListController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
