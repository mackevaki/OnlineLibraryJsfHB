package controllers;

import dao.impls.GenreService;
import entity.Genre;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.cdi.Eager;

import java.io.Serializable;
import java.util.*;

@Named("genreController")
@SessionScoped
@Eager
@Getter @Setter
public class GenreController implements Serializable, Converter<Genre> {
    private List<Genre> genreList; 
    private List<SelectItem> selectItems = new ArrayList<>();
    private Map<Long, Genre> genreMap;

    private GenreService genreService;
    private FacesContext facesContext;

    @Inject
    public GenreController(GenreService genreService, FacesContext facesContext) {
        this.genreService = genreService;
        this.facesContext = facesContext;
    }

    @PostConstruct
    public void init() {
        genreMap = new HashMap<>();
        genreList = genreService.findAll(Genre.class);
        
        genreList.sort(Comparator.comparing(Genre::toString));

        for (Genre genre : genreList) {
            genreMap.put(genre.getId(), genre);
            selectItems.add(new SelectItem(genre, genre.getName()));
        }        
    }

    @Override
    public Genre getAsObject(FacesContext context, UIComponent component, String value) {
        return genreMap.get(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Genre value) {
        return value.getId().toString();
    }

    public List<Genre> find(String name) {
        return genreService.findByName(name);
    }
}
