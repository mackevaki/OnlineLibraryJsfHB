package models;

import beans.Pager;
import db.DataHelper;
import entity.Book;
import java.util.List;
import java.util.Map;
import org.primefaces.model.FilterMeta;
import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortMeta;

public class BookListDataModel extends LazyDataModel<Book> {
    private List<Book> bookList;
    private DataHelper dataHelper = DataHelper.getInstance();
    private Pager pager = Pager.getInstance();
    
    public BookListDataModel() {}

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
        pager.setFrom(first);
        pager.setTo(pageSize);
        
        dataHelper.populateList();
        
        this.setRowCount(pager.getTotalBooksCount());
        
        bookList = pager.getList();
        
        return bookList;
    }
}
