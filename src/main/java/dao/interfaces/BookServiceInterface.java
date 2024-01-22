package dao.interfaces;

import dao.Page;
import models.BookSearchValues;
import org.primefaces.model.SortOrder;

// методы для работы с книгами
public interface BookServiceInterface<T> extends CommonServiceInterface<T>{
    // поиск книг с постраничным выводом
    Page<T> find(BookSearchValues bookSearchValues, int startFrom, int pageSize, String sortColumn, SortOrder sortOrder);

    byte[] getContent(Long id); // получить контент (PDF) книги

    void updateViewCount(long viewCount, long bookId); // обновить кол-во просмотров
}
