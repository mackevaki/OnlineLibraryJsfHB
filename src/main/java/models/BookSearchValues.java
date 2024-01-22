package models;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Named
@SessionScoped
@Getter @Setter
// параметры поиска книг (незаполненные параметры не будут учитываться при поиске)
public class BookSearchValues implements Serializable {
    private Long genreId;
    private Character letter;
    private String searchText;
}
