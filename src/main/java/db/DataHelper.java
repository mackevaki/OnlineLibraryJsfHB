package db;

import beans.Pager;
import entity.Author;
import entity.Book;
import entity.Genre;
import entity.HibernateUtil;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class DataHelper {

    private SessionFactory sessionFactory = null;
    private static DataHelper dataHelper;
    private Pager currentPager;

    private SavedCriteria<Book> currentCriteria;
    
    private DataHelper() {
        sessionFactory = HibernateUtil.getSessionFactory();
    }

    public static DataHelper getInstance() {
        if (dataHelper == null) {
            dataHelper = new DataHelper();
        }
        return dataHelper;
    }

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }
   
    public void runCurrentCriteria() {
        Session session = getSession();
        if (!session.getTransaction().isActive()) {
            session.beginTransaction();
        }
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> from = criteriaQuery.from(Book.class);
        CriteriaQuery<Book> select;
                
        select = currentCriteria.getCriteriaQuery(from, criteriaQuery, criteriaBuilder).orderBy(criteriaBuilder.asc(from.get("name")));
        
        TypedQuery<Book> typedQuery = session.createQuery(select);
        typedQuery.setFirstResult(currentPager.getFrom());
        typedQuery.setMaxResults(currentPager.getTo());
        
        List<Book> list = typedQuery.getResultList();
        
        currentPager.setList(list);
        
        if (session.getTransaction().isActive()) {
            session.getTransaction().commit();
        }
    }
    
    public void getAllBooks(Pager pager) {
        currentPager = pager;
        
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        
        CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
        countQuery.select(criteriaBuilder.count(countQuery.from(Book.class)));
        Long count = session.createQuery(countQuery).getSingleResult();
        
        currentPager.setTotalBooksCount(count);

        currentCriteria = (root, query, cb) -> {return query.select(root);};
      
        runCurrentCriteria();
        if (session.getTransaction().isActive()) {
            session.getTransaction().commit();
        }
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

    public void getBooksByGenre(Long genreId, Pager pager) {
        currentPager = pager;        
        
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        currentCriteria = (root, query, cb) -> {return query.select(root).where(cb.equal(root.get("genre").get("id"), genreId));};
        
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();

        CriteriaQuery q = criteriaBuilder.createQuery();
        Root<Book> root = q.from(Book.class);
        
        q.select(criteriaBuilder.count(root));
        q.where(criteriaBuilder.equal(root.get("genre").get("id"), genreId));
        CriteriaQuery<Long> qcount = q;
        Long count = session.createQuery(qcount).getSingleResult();
        
        currentPager.setTotalBooksCount(count);

        runCurrentCriteria();
        if (session.getTransaction().isActive()) {
            session.getTransaction().commit();
        }
    }

    public void getBooksByLetter(Character letter, Pager pager) {
        currentPager = pager;
        
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        currentCriteria = (root, query, cb) -> {return query.select(root).where(cb.like(cb.lower(root.get("name")), letter.toString().toLowerCase() + "%"));};
        
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
       
        CriteriaQuery q = criteriaBuilder.createQuery();
        Root<Book> root = q.from(Book.class);
        
        q.select(criteriaBuilder.count(root));
        q.where(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), letter.toString().toLowerCase() + "%"));
        CriteriaQuery<Long> qcount = q;
        Long count = session.createQuery(qcount).getSingleResult();
        
        currentPager.setTotalBooksCount(count);

        runCurrentCriteria();
        if (session.getTransaction().isActive()) {
            session.getTransaction().commit();
        }
    }

    public void getBooksByAuthor(String authorName, Pager pager) {
        currentPager = pager;
        
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        currentCriteria = (root, query, cb) -> {return query.select(root).where(cb.like(cb.lower(root.get("author").get("fio")), "%" + authorName.toLowerCase() + "%"));};
        
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        
        CriteriaQuery q = criteriaBuilder.createQuery();
        Root<Book> root = q.from(Book.class);
        
        q.select(criteriaBuilder.count(root));
        q.where(criteriaBuilder.like(criteriaBuilder.lower(root.get("author").get("fio")), "%" + authorName.toLowerCase() + "%"));
        CriteriaQuery<Long> qcount = q;
        Long count = session.createQuery(qcount).getSingleResult();
        
        currentPager.setTotalBooksCount(count);
        
        runCurrentCriteria();
        if (session.getTransaction().isActive()) {
            session.getTransaction().commit();
        }
    }

    public void getBooksByName(String bookName, Pager pager) {
        currentPager = pager;
        
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        currentCriteria = (root, query, cb) -> {return query.select(root).where(cb.like(cb.lower(root.get("name")), "%" + bookName.toLowerCase() + "%"));};
        
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        
        CriteriaQuery q = criteriaBuilder.createQuery();
        Root<Book> root = q.from(Book.class);
        
        q.select(criteriaBuilder.count(root));
        q.where(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + bookName.toLowerCase() + "%"));
        CriteriaQuery<Long> qcount = q;
        Long count = session.createQuery(qcount).getSingleResult();
        
        currentPager.setTotalBooksCount(count);
        
        runCurrentCriteria();
        if (session.getTransaction().isActive()) {
            session.getTransaction().commit();
        }
    }

    public void update() {
        Session session = sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        
//        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
//        CriteriaUpdate<Book> criteriaUpdate = criteriaBuilder.createCriteriaUpdate(Book.class);
//        Root<Book> root = criteriaUpdate.from(Book.class);
        
        for(Object object : currentPager.getList()) {
            Book book = (Book) object;
            if(book.isEdit()) {
                session.merge(book);
            }
        }
        
        tx.commit();
        session.close();
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

    public void setCurrentPager(Pager currentPager) {
        this.currentPager = currentPager;
    }
}