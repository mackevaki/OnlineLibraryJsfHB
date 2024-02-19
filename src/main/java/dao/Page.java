package dao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
// Container for storing the query result with pagination
public class Page<T> implements Serializable {
    private List<T> list; // found objects
    private int totalCount; // how many objects were found
}
