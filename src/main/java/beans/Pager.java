package beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class Pager<T> implements Serializable {
    private int totalBooksCount; // общее кол-во книг (не на текущей странице, а всего), для постраничности
    private int selectedPageNumber = 1; // выбранный номер страницы в постраничной навигации
    private List<T> list;
    private int from;
    private int to;

    private static Pager pager;

    private Pager() {}

    public static Pager getInstance() {
        if (pager == null) {
            pager = new Pager();
        }
        return pager;
    }
   }
