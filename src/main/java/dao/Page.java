package dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
// Контейнер для хранения результата запроса с постраничностью
public class Page<T> {
    private List<T> list; // найденные объекты
    private int totalCount; // сколько всего найдено объектов
}
