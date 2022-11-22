package controllers;

import db.DataHelper;
import entity.Genre;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.omnifaces.cdi.Eager;

@Named
@ApplicationScoped
//@Eager
public class GenreController implements Serializable {
    private List<Genre> genreList; //= new ArrayList<>();
    
    public GenreController() {
        fillAllGenres();
    }
    
    private List<Genre> fillAllGenres() {
        genreList = new ArrayList<>();
//        try (Connection conn = Database.getConnection();
//            Statement stmt = conn.createStatement();
//            ResultSet res = stmt.executeQuery("select * from library.genre order by name");) {
//            while (res.next()) {
//                Genre genre = new Genre();
//                genre.setName(res.getString("name"));
//                genre.setId(res.getLong("id"));
//                genreList.add(genre);
//            }
//        } catch (SQLException ex) {
//            Logger.getLogger(GenreController.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
        genreList = DataHelper.getInstance().getAllGenres();

        return genreList;
    }
    
    public List<Genre> getGenreList() {
//        if (!genreList.isEmpty()) {
//            return genreList;
//        } else {
//            return fillAllGenres();
//        }
        return genreList;
    }
}
