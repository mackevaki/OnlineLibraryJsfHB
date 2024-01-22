package controllers;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;

@Named(value = "loginController")
@RequestScoped
public class LoginController {
    public LoginController() {
    }

    public String login() {
        return "books";
    }

    public String exit() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return "exit";
    }
}
