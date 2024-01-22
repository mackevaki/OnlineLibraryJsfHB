package dao.interfaces;

import java.util.List;

public interface CommonServiceInterface<T> {
    void add(T item);

    void delete(T item);

    void update(T item);

    List<T> findAll(Class<T> c);

    T find(Class<T> c, Long id);
}
