package models;

import beans.Pager;
import dao.Page;
import db.BookService;
import entity.Book;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

// модель для постраничного вывода списка значений
// можно применять не только к книгам, но и к любым типам данных
@Named
@SessionScoped
public class BookListDataModel extends LazyDataModel<Book>  implements Serializable {
    private List<Book> bookList;
    private final BookService bookService; //= DataHelper.getInstance();
    private Pager pager = Pager.getInstance();
    private BookSearchValues bookSearchValues; // параметры поиска

    @Inject
    public BookListDataModel(BookService bookService, BookSearchValues bookSearchValues) {
        this.bookService = bookService;
        this.bookSearchValues = bookSearchValues;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return 0;//(int) bookList.stream().count();
    }

    @Override
    public String getRowKey(Book book) {
        return book.getId().toString();
    }

    @Override
    public Book getRowData(String rowKey) {
        for (Book book : bookList) {
            if (book.getId().intValue() == Long.valueOf(rowKey).intValue()) {
                return book;
            }
        }
        return null;
    }   
    
    @Override
    public List<Book> load(int first, int pageSize, Map<String, SortMeta> sortBy, Map<String, FilterMeta> filterBy) {
/*        pager.setFrom(first);
        pager.setTo(pageSize);
        
        bookService.populateList();

        this.setRowCount(pager.getTotalBooksCount());
        
        bookList = pager.getList(); // alternative pagination implementation technique (uses Pager) */
        Page<Book> page = bookService.find(bookSearchValues, first, pageSize, "name", SortOrder.ASCENDING);
        this.setRowCount(page.getTotalCount());

        bookList = page.getList();

        return bookList;
    }
}
