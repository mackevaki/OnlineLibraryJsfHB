package dao.interfaces;

import java.util.List;

// методы для таблиц-справочников (автор, жанр, издатель)
public interface DirServiceInterface<T> extends CommonServiceInterface<T> {
    List<T> findByName(String str); // ищет вхождение текста в названии
}
