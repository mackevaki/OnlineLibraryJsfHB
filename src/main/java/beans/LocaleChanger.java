package beans;

import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.Locale;
import org.omnifaces.cdi.Eager;

@Named(value = "localeChanger")
@SessionScoped
@Eager
public class LocaleChanger implements Serializable {
    private Locale currentLocale = new Locale("ru"); // set russian language as default
    
    public LocaleChanger() {
    }

    public Locale getCurrentLocale() {
        return currentLocale;
    }
    
    public void changeLocale(String localeCode) {
        currentLocale = new Locale(localeCode);
    }
}
