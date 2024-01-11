package controllers;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;

@Named
@RequestScoped
public class RequestController {
    private int bookIndex = -1;

    public int getBookIndex() {
        return bookIndex;
    }

    public int getIncrementBookIndex() {
        return ++bookIndex;
    }
}
