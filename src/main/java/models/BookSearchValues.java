package models;

import enums.SearchType;
import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Named("bookSearchValues")
@RequestScoped
@Getter @Setter
// параметры поиска книг (незаполненные параметры не будут учитываться при поиске)
public class BookSearchValues implements Serializable {
    private long genreId;
    private Character letter;
    private String searchText;
    private SearchType searchType = SearchType.TITLE; // default search field is the book's title
}
