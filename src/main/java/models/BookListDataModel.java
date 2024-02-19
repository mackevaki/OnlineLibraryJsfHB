package models;

import dao.Page;
import db.BookService;
import entity.Book;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;
import org.primefaces.model.SortOrder;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

// model for page-by-page display of a list of values
@Named
@SessionScoped
public class BookListDataModel extends LazyDataModel<Book>  implements Serializable {
    private List<Book> bookList;
    private final BookService bookService;
    private BookSearchValues bookSearchValues; // search attributes

    @Inject
    public BookListDataModel(BookService bookService, BookSearchValues bookSearchValues) {
        this.bookService = bookService;
        this.bookSearchValues = bookSearchValues;
    }

    @Override
    public int count(Map<String, FilterMeta> filterBy) {
        return 0;
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
        Page<Book> page = bookService.find(bookSearchValues, first, pageSize, "name", SortOrder.ASCENDING);
        this.setRowCount(page.getTotalCount());

        bookList = page.getList();

        return bookList;
    }
}
