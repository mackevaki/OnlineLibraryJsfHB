package controllers;

import db.DataHelper;
import entity.Genre;
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
import org.omnifaces.cdi.Eager;

@Named("genreController")
@ApplicationScoped
@Eager
public class GenreController implements Serializable, Converter {
    private List<Genre> genreList; 
    private List<SelectItem> selectItems = new ArrayList<>();
    private Map<Long, Genre> genreMap;
    
    public GenreController() {
        genreMap = new HashMap<Long, Genre>();
        genreList = DataHelper.getInstance().getAllGenres();
        
        Collections.sort(genreList, Comparator.comparing(Genre::toString));
        
//        genreList.add(0, createEmptyGenre());

        for (Genre genre : genreList) {
            genreMap.put(genre.getId(), genre);
            selectItems.add(new SelectItem(genre, genre.getName()));
        }        
    }
    
    public List<Genre> getGenreList() {
        return genreList;
    }
    
    public List<SelectItem> getSelectItems() {
        return selectItems;
    }

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return genreMap.get(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((Genre) value).getId().toString();
    }
    
//    private Genre createEmptyGenre() {
//        Genre genre = new Genre();
//        genre.setId(-1L);
//        genre.setName("");
//        return genre;
//    }
}
