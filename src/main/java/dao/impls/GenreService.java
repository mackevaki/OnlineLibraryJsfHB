package dao.impls;

import dao.interfaces.DirServiceInterface;
import entity.Genre;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import org.hibernate.query.NativeQuery;

import java.io.Serializable;
import java.util.List;

@Named("genreService")
@SessionScoped
public class GenreService extends CommonService<Genre> implements DirServiceInterface<Genre>, Serializable {
    @Override
    public List<Genre> findByName(String str) {
        NativeQuery<Genre> query = getSession().createNativeQuery("select * from Genre g where g.name like :str", Genre.class);

        query.setParameter("str", str + "%");

        return query.getResultList();
    }
}
