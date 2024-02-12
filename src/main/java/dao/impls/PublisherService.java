package dao.impls;

import dao.interfaces.DirServiceInterface;
import entity.Author;
import entity.Author_;
import entity.Publisher;
import entity.Publisher_;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.io.Serializable;
import java.util.List;

@Named("publisherService")
@SessionScoped
public class PublisherService extends CommonService<Publisher> implements DirServiceInterface<Publisher>, Serializable {
    @Override
    public List<Publisher> findByName(String str) {
        try (Session session = getSession()) {
            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Publisher> cq = cb.createQuery(Publisher.class);
            Root<Publisher> from = cq.from(Publisher.class);

            cq.select(from).where(cb.like(from.get(Publisher_.NAME), str+"%"));

            Query<Publisher> query = session.createQuery(cq);
            return query.getResultList();
        }
    }
}
