package controllers;

import dao.impls.AuthorService;
import entity.Author;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;
    
@Named("authorController")
@ApplicationScoped
@Getter @Setter
public class AuthorController implements Serializable, Converter<Author> {
    private List<Author> authorList;
    private List<SelectItem> selectItems = new ArrayList<>();
    private Map<Long, Author> authorMap;

    private FacesContext facesContext;
    private AuthorService authorService;

    @Inject
    public AuthorController(FacesContext facesContext, AuthorService authorService) {
        this.facesContext = facesContext;
        this.authorService = authorService;
    }

    @PostConstruct
    public void init() {
        authorMap = new HashMap<>();        
        authorList = authorService.findAll(Author.class);//DataHelper.getInstance().getAllAuthors();

        authorList.sort(Comparator.comparing(Author::toString));
        
        for (Author author : authorList) {
            authorMap.put(author.getId(), author);
            selectItems.add(new SelectItem(author, author.getFio()));
        }
    }

    @Override
    public Author getAsObject(FacesContext context, UIComponent component, String value) {
        return authorMap.get(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Author value) {
        return value.getId().toString();
    }

    public List<Author> find(String fio) {
        return authorService.findByName(fio);
    }

}
