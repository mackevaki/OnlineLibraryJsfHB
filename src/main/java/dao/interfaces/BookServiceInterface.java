package dao.interfaces;

import dao.Page;
import models.BookSearchValues;
import org.primefaces.model.SortOrder;

// methods for working with books
public interface BookServiceInterface<T> extends CommonServiceInterface<T>{
    // search for books with page output
    Page<T> find(BookSearchValues bookSearchValues, int startFrom, int pageSize, String sortColumn, SortOrder sortOrder);

    byte[] getContent(Long id); // get book's content (PDF)

    void updateViewCount(long viewCount, long bookId); // update number of views
}
