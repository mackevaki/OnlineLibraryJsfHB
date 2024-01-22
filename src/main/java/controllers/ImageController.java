package controllers;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.event.ActionEvent;
import jakarta.faces.event.ActionListener;
import jakarta.inject.Inject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;


@Named(value = "imageController")
@SessionScoped
@Getter @Setter
public class ImageController implements Serializable {
/*    private final int IMAGE_MAX_SIZE = 204800;
    private byte[] uploadedImage;
    
    @Inject
    private BookListController bookListController;
    
    public ImageController() {
    }

    public StreamedContent getDefaultImage() {
        return getStreamedContent(bookListController.getSelectedBook().getImage());
    }

    public StreamedContent getUploadedImage() {
        return getStreamedContent(uploadedImage);
    }

    public void handleFileUpload(FileUploadEvent event) {
        uploadedImage = event.getFile().getContent();
//        bookListController.getSelectedBook().setImage(uploadedImage);
    }
    
    private DefaultStreamedContent getStreamedContent(byte[] image) {
        if (image == null) {
            return null;
        }

        try(InputStream inputStream = new ByteArrayInputStream(image);) {
            return DefaultStreamedContent.builder().contentType("image/png").stream(() -> inputStream).build();
        } catch (IOException ex) {
            Logger.getLogger(ImageController.class.getName()).log(Level.SEVERE, null, ex);
        } 
        return null;
    }

    public ActionListener saveListener() {
        return (ActionEvent event) -> {
            if (uploadedImage != null) {
                bookListController.getSelectedBook().setImage(uploadedImage);
            }
            clear();
        };
    }

    public ActionListener clearListener() {
        return (ActionEvent event) -> clear();
    }

    public void clear() {
        uploadedImage = null;
    }

    public int getImageMaxSize() {
        return IMAGE_MAX_SIZE;
    }*/
}
