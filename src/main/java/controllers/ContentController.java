package controllers;

import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;

@Named("contentController")
@SessionScoped
@Getter @Setter
public class ContentController implements Serializable {
    private byte[] uploadedContent;
    private boolean showContent;

/*
    private BookController bookController;

    @Inject
    public ContentController(BookController bookController) {
        this.bookController = bookController;
    }
*/

    public void handleFileUpload(FileUploadEvent event) {
        uploadedContent = event.getFile().getContent();
        if (uploadedContent != null && uploadedContent.length > 0){
            showContent = true;
        }
//        bookController.getSelectedBook().setContent(uploadedContent);
    }

    public ActionListener saveListener() {
        return (ActionEvent event) -> {
            if (uploadedContent != null) {
                // bookController.getSelectedBook().setContent(uploadedContent);
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


