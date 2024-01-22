package dao.impls;

import dao.interfaces.CommonServiceInterface;
import entity.HibernateUtil;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import lombok.Getter;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

@Getter
public abstract class CommonService<T> implements CommonServiceInterface<T> {
    private SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

    protected Session getSession() {
        if (sessionFactory == null) {
            sessionFactory = HibernateUtil.getSessionFactory();
        }

        return sessionFactory.openSession();
    }

    @Override
    public void add(T item) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            session.persist(item);

            tx.commit();
        }
    }

    @Override
    public void delete(T item) {
        try(Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            session.remove(item);

            tx.commit();
        }
    }

    @Override
    public void update(T item) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            session.merge(item);

            tx.commit();
        }
    }

    @Override
    public List<T> findAll(Class<T> c) {
        CriteriaBuilder cb = sessionFactory.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(c);
        Root<T> root = cq.from(c);

        cq.select(root);

        try (Session session = sessionFactory.openSession()) {
            Query<T> query = session.createQuery(cq);
            return query.getResultList();
        }
    }

    @Override
    public T find(Class<T> c, Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.get(c, id);
        }
    }
}
