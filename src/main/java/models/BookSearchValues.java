package models;

import enums.SearchType;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
@Named("bookSearchValues")
@RequestScoped
@Getter @Setter
// book search attributes (empty attributes will not be taken into account when searching)
public class BookSearchValues implements Serializable {
    private long genreId;
    private Character letter;
    private String searchText;
    private SearchType searchType = SearchType.TITLE; // default search field is the book's title
}
