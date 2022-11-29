package controllers;

import db.DataHelper;
import entity.Publisher;
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

@Named("publisherController")
@ApplicationScoped
public class PublisherController implements Serializable, Converter {
    private List<Publisher> publisherList;
    private Map<Long, Publisher> publisherMap;
    private List<SelectItem> selectItems = new ArrayList<>();

    public PublisherController() {
        publisherList = DataHelper.getInstance().getAllPublishers();
        publisherMap = new HashMap<>();
        
        Collections.sort(publisherList, Comparator.comparing(Publisher::toString));
        
        for (Publisher publisher : publisherList) {
            publisherMap.put(publisher.getId(), publisher);
            selectItems.add(new SelectItem(publisher, publisher.getName()));
        }
    }

    public List<Publisher> getPublisherList() {
        return publisherList;
    }

    public List<SelectItem> getSelectItems() {
        return selectItems;
    }
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        return publisherMap.get(Long.valueOf(value));
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        return ((Publisher)value).getId().toString();
    }
}
