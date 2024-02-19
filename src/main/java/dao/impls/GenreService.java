package dao.impls;

import dao.interfaces.DirServiceInterface;
import entity.Genre;
import entity.Genre_;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.util.List;

@Named("genreService")
@SessionScoped
public class GenreService extends CommonService<Genre> implements DirServiceInterface<Genre>, Serializable {
    @Override
    public List<Genre> findByName(String str) {
        try (Session session = getSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Genre> cq = cb.createQuery(Genre.class);
            Root<Genre> from = cq.from(Genre.class);

            cq.select(from).where(cb.like(from.get(Genre_.NAME), str+"%"));

            Query<Genre> query = session.createQuery(cq);
            return query.getResultList();
        }
    }
}
