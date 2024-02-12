package dao.impls;

import dao.interfaces.DirServiceInterface;
import entity.Author;
import entity.Author_;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.util.List;

@Named("authorService")
@SessionScoped
public class AuthorService extends CommonService<Author> implements DirServiceInterface<Author>, Serializable {
    @Override
    public List<Author> findByName(String str) {
        try (Session session = getSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Author> cq = cb.createQuery(Author.class);
            Root<Author> from = cq.from(Author.class);

            cq.select(from).where(cb.like(from.get(Author_.FIO), str+"%"));

            Query<Author> query = session.createQuery(cq);
            return query.getResultList();
        }
    }
}
