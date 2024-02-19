package dao.interfaces;

import java.util.List;

// methods for reference tables (author, genre, publisher)
public interface DirServiceInterface<T> extends CommonServiceInterface<T> {
    List<T> findByName(String str); // searches for occurrences of text in the title
}
