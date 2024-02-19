package beans;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
public class Pager<T> implements Serializable {
    private int totalBooksCount;
    private List<T> list;
    private int from;
    private int to;

    public Pager() {}
   }
