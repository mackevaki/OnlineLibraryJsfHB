package controllers;

import java.util.ResourceBundle;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

public class SessionUtils {

    public static SessionUtils sessionObjects;

    public static SessionUtils getInstance() {
        if (sessionObjects == null) {
            sessionObjects = new SessionUtils();
        }

        return sessionObjects;
    }

    private SessionUtils() {
    }
    
    private ResourceBundle bundle = ResourceBundle.getBundle("nls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());

    public boolean isNullOrEmpty(Object obj) {
        return obj == null || obj.toString().equals("");
    }

    public void failValidation(String message_key) {
        FacesContext.getCurrentInstance().validationFailed();
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, bundle.getString(message_key), bundle.getString("error")));
    }

    public String getBundleMessage(String message_key) {
        return bundle.getString(message_key);
    }

    public void showMessage(String message_key) {
        FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(bundle.getString(message_key)));

    }

    public String getUserName() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        return facesContext.getExternalContext().getUserPrincipal().getName();
    }
}