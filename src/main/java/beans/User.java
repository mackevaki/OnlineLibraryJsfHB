package beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@SessionScoped
@Getter @Setter
// users data and trying to log in
public class User implements Serializable {
    private String username;
    private String password;
    
    public User() {
    }

    public String login() {
        try {
            Thread.sleep(1000); // imitation of loading
            
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

            if (request.getUserPrincipal() == null || (request.getUserPrincipal() != null && !request.getUserPrincipal().getName().equals(username))) {
                request.logout(); // if user already has active session (has authorized) - log out
                request.login(username, password); // authentication/authorization request
            }

            return "/pages/books.xhtml?faces-redirect=true"; // if user has logged in successfully, go to the page with books
        } catch (ServletException ex) {
            // if something went wrong
            ResourceBundle bundle = ResourceBundle.getBundle("nls.messages", FacesContext.getCurrentInstance().getViewRoot().getLocale());
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            FacesContext context = FacesContext.getCurrentInstance();
            FacesMessage message = new FacesMessage(bundle.getString("login_error"));
            message.setSeverity(FacesMessage.SEVERITY_ERROR);
            context.addMessage("login_form", message);
        } catch (InterruptedException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            Thread.currentThread().interrupt();
        }
        
        return "index"; // return to the main page if an error during authorization has occurred
    }
    
    public String logout() {
        String indexRedirect = "/index.xhtml?faces-redirect=true";

        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            request.logout(); // application exit (all roles will be reset)
        } catch (ServletException e) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, e);
        }

        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();

        return indexRedirect;
    }
        
    public String goHome(){
        return "/index.xhtml?faces-redirect=true";
    }
    
     public String goBooks(){
        return "/pages/books.xhtml?faces-redirect=true";
    }    

}
