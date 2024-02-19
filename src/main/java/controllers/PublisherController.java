package controllers;

import dao.impls.PublisherService;
import entity.Publisher;
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

import java.io.Serializable;
import java.util.*;

@Named("publisherController")
@SessionScoped
@Getter @Setter
public class PublisherController implements Serializable, Converter<Publisher> {
    private List<Publisher> publisherList;
    private Map<Long, Publisher> publisherMap;
    private List<SelectItem> selectItems = new ArrayList<>();

    private FacesContext facesContext;
    private PublisherService publisherService;

    @Inject
    public PublisherController(FacesContext facesContext, PublisherService publisherService) {
        this.facesContext = facesContext;
        this.publisherService = publisherService;
    }

    @PostConstruct
    public void init() {
        publisherList = publisherService.findAll(Publisher.class);
        publisherMap = new HashMap<>();
        
        publisherList.sort(Comparator.comparing(Publisher::toString));
        
        for (Publisher publisher : publisherList) {
            publisherMap.put(publisher.getId(), publisher);
            selectItems.add(new SelectItem(publisher, publisher.getName()));
        }
    }
    
    @Override
    public Publisher getAsObject(FacesContext context, UIComponent component, String value) {
        return publisherMap.get(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Publisher value) {
        return value.getId().toString();
    }

    public List<Publisher> find(String str) {
        return publisherService.findByName(str);
    }
}
