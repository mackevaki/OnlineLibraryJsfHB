package controllers;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

import org.primefaces.event.FileUploadEvent;

@Named(value = "contentController")
@SessionScoped
public class ContentController implements Serializable {
    private byte[] uploadedContent;
    private boolean showContent;
    
    @Inject
    private BookListController bookListController;

    public BookListController getBookListController() {
        return bookListController;
    }

    public void setBookListController(BookListController bookListController) {
        this.bookListController = bookListController;
    }

    public void handleFileUpload(FileUploadEvent event) {
        uploadedContent = event.getFile().getContent();
        if (uploadedContent != null && uploadedContent.length > 0){
            showContent = true;
        }
//        bookListController.getSelectedBook().setContent(uploadedContent);
    }

    public ActionListener saveListener() {
        return (ActionEvent event) -> {
            if (uploadedContent != null) {
                bookListController.getSelectedBook().setContent(uploadedContent);
            }
            clear();
        };
    }

    public ActionListener clearListener() {
        return (ActionEvent event) -> {
            clear();
        };
    }

    public void clear() {
        uploadedContent = null;
        showContent = false;
    }

    public boolean isShowContent() {
        return showContent;
    }

    public byte[] getUploadedContent() {
        return uploadedContent;
    }
}


