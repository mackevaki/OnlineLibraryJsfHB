package controllers;

import db.DataHelper;
import entity.Author;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
    
@Named("authorController")
@ApplicationScoped
public class AuthorController implements Serializable, Converter {
    private List<Author> authorList; 
    private List<SelectItem> selectItems = new ArrayList<>();
    private Map<Long, Author> authorMap;

    public AuthorController() {
        authorMap = new HashMap<>();        
        authorList = DataHelper.getInstance().getAllAuthors();

        Collections.sort(authorList, Comparator.comparing(Author::toString));
        
        for (Author author : authorList) {
            authorMap.put(author.getId(), author);
            selectItems.add(new SelectItem(author, author.getFio()));
        }
    }
        
    public List<Author> getAuthorList(){
        return authorList;
    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return authorMap.get(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((Author)value).getId().toString();
    }
}
