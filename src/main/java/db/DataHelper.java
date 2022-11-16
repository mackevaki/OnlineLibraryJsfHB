package db;

import entity.Author;
import entity.Book;
import entity.Genre;
import entity.HibernateUtil;
import jakarta.persistence.Query;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class DataHelper {

    private SessionFactory sessionFactory = null;
    private static DataHelper dataHelper;

    private DataHelper() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public static DataHelper getInstance() {
        return dataHelper == null ? new DataHelper() : dataHelper;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
   
    public List<Book> getAllBooks() {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        
        Root<Book> root = cq.from(Book.class);
        cq.select(root);
        
        Query query = session.createQuery(cq);
        List<Book> list = query.getResultList();
        
        tx.commit();
        
        return list;
    }

    public List<Genre> getAllGenres() {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Genre> cq = cb.createQuery(Genre.class);
        
        Root<Genre> root = cq.from(Genre.class);
        cq.select(root);
        
        Query query = session.createQuery(cq);
        List<Genre> list = query.getResultList();
        
        tx.commit();
        
        return list;
    }

    public List<Author> getAllAuthors() { 
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Author> cq = cb.createQuery(Author.class);
        
        Root<Author> root = cq.from(Author.class);
        cq.select(root);
        
        Query query = session.createQuery(cq);
        List<Author> list = query.getResultList();
        
        tx.commit();
        
        return list;
    }

    public List<Book> getBooksByGenre(Long genreId) {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> root = cq.from(Book.class);
        
        cq.select(root).where(cb.equal(root.get("genre").get("id"), genreId)).orderBy(cb.asc(root.get("name")));
        
        Query query = session.createQuery(cq);
        List<Book> list = query.getResultList();
                
        tx.commit();
        
        return list;
    }

    public List<Book> getBooksByLetter(Character letter) {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> root = cq.from(Book.class);
        
        cq.select(root).where(cb.like(cb.lower(root.get("name")), letter.toString().toLowerCase() + "%")).orderBy(cb.asc(root.get("name")));
        
        Query query = session.createQuery(cq);
        List<Book> list = query.getResultList();
                
        tx.commit();
        
        return list; 
    }

    public List<Book> getBooksByAuthor(String authorName) {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> root = cq.from(Book.class);
        
        cq.select(root).where(cb.like(cb.lower(root.get("author").get("fio")), "%" + authorName.toLowerCase() + "%")).orderBy(cb.asc(root.get("name")));
        
        Query query = session.createQuery(cq);
        List<Book> list = query.getResultList();
                
        tx.commit();
        
        return list; 
    }

    public List<Book> getBooksByName(String bookName) {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Book> cq = cb.createQuery(Book.class);
        Root<Book> root = cq.from(Book.class);
        
        cq.select(root).where(cb.like(cb.lower(root.get("name")), "%" + bookName.toLowerCase() + "%")).orderBy(cb.asc(root.get("name")));
        
        Query query = session.createQuery(cq);
        List<Book> list = query.getResultList();
                
        tx.commit();
        
        return list; 
    }

    public byte[] getContent(Long id) {
        try (Session session = sessionFactory.openSession();) {
            return (byte[]) session.get(Book.class, id).getContent();
        }
    }

    public byte[] getImage(Long id) {
        try (Session session = sessionFactory.openSession();) {
            return (byte[]) session.get(Book.class, id).getImage();
        }
    }

    public Author getAuthor(long id) {
        return (Author) getSession().get(Author.class, id);
    }

}