package db;

import beans.Pager;
import entity.Author;
import entity.Book;
import entity.Genre;
import entity.HibernateUtil;
import entity.Publisher;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;
import java.io.Serializable;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.MutationQuery;

public class DataHelper {

    private SessionFactory sessionFactory = null;
    private static DataHelper dataHelper;
    private Pager pager = Pager.getInstance();

    private SavedCriteria<Book> currentCriteria;
    private SavedCriteria<Book> countCriteria;    
    
    private DataHelper() {
        sessionFactory = HibernateUtil.getSessionFactory();
        currentCriteria = (selectionRoot, root, query, cb) -> {return query.select(selectionRoot);};
        countCriteria = (selectionRoot, root, query, cb) -> {return query.select(cb.count(root));};
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
                
        Selection[] selection = {from.get("id").alias("id"), from.get("name").alias("name"), from.get("image").alias("image"), from.get("genre").alias("genre"), from.get("pageCount").alias("pageCount"), from.get("isbn").alias("isbn"), from.get("publisher").alias("publisher"), from.get("author").alias("author"), from.get("publishDate").alias("publishDate"), from.get("descr").alias("descr")};
        
        select = currentCriteria.getCriteriaQuery(criteriaBuilder.construct(Book.class, selection), from, criteriaQuery, criteriaBuilder).orderBy(criteriaBuilder.asc(from.get("name")));
        
        TypedQuery<Book> typedQuery = session.createQuery(select);
        typedQuery.setFirstResult(pager.getFrom());
        typedQuery.setMaxResults(pager.getTo());
        
        List<Book> list = typedQuery.getResultList();
        
        pager.setList(list);
        
        if (session.getTransaction().isActive()) {
            session.getTransaction().commit();
        }
    }
    
    public void runCountCriteria() {
        Session session = getSession();
        if (!session.getTransaction().isActive()) {
            session.beginTransaction();
        }
        
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery query = criteriaBuilder.createQuery();
        Root<Book> from = query.from(Book.class);
        
        CriteriaQuery<Long> countQuery = countCriteria.getCriteriaQuery(from, from, query, criteriaBuilder);
        Long count = session.createQuery(countQuery).getSingleResult();
        
        pager.setTotalBooksCount(Integer.parseInt(count.toString()));
        
        if (session.getTransaction().isActive()) {
            session.getTransaction().commit();
        }
    }
    
    public void getAllBooks() {       
        currentCriteria = (selectionRoot, root, query, cb) -> {return query.select(selectionRoot);};
        countCriteria = (selectionRoot, root, query, cb) -> {return query.select(cb.count(root));};
        
        runCountCriteria();
        runCurrentCriteria();
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
    
    public List<Publisher> getAllPublishers() {
        Session session = getSession();
        Transaction tx = session.beginTransaction();
        
        CriteriaBuilder cb = session.getCriteriaBuilder();
        CriteriaQuery<Publisher> cq = cb.createQuery(Publisher.class);
        Root<Publisher> root = cq.from(Publisher.class);
        
        cq.select(root);
        
        Query query  = session.createQuery(cq);
        List<Publisher> list = query.getResultList();
        
        tx.commit();
        
        return list;
    }

    public void getBooksByGenre(Long genreId) {               
        currentCriteria = (selectionRoot, root, query, cb) -> {return query.select(selectionRoot).where(cb.equal(root.get("genre").get("id"), genreId));};
        countCriteria = (selectionRoot, root, query, cb) -> {return query.select(cb.count(root)).where(cb.equal(root.get("genre").get("id"), genreId));};

        runCountCriteria();
        runCurrentCriteria();
    }

    public void getBooksByLetter(Character letter) {
        currentCriteria = (selectionRoot, root, query, cb) -> {return query.select(selectionRoot).where(cb.like(cb.lower(root.get("name")), letter.toString().toLowerCase() + "%"));};
        countCriteria = (selectionRoot, root, query, cb) -> {return query.select(cb.count(root)).where(cb.like(cb.lower(root.get("name")), letter.toString().toLowerCase() + "%"));};

        runCountCriteria();
        runCurrentCriteria();
    }

    public void getBooksByAuthor(String authorName) {       
        currentCriteria = (selectionRoot, root, query, cb) -> {return query.select(selectionRoot).where(cb.like(cb.lower(root.get("author").get("fio")), "%" + authorName.toLowerCase() + "%"));};
        countCriteria = (selectionRoot, root, query, cb) -> {return query.select(cb.count(root)).where(cb.like(cb.lower(root.get("author").get("fio")), "%" + authorName.toLowerCase() + "%"));};

        runCountCriteria();
        runCurrentCriteria();
    }

    public void getBooksByName(String bookName) {        
        currentCriteria = (selectionRoot, root, query, cb) -> {return query.select(selectionRoot).where(cb.like(cb.lower(root.get("name")), "%" + bookName.toLowerCase() + "%"));};
        countCriteria = (selectionRoot, queryRoot, query, cb) -> {return query.select(cb.count(queryRoot)).where(cb.like(cb.lower(queryRoot.get("name")), "%" + bookName.toLowerCase() + "%"));};

        runCountCriteria();
        runCurrentCriteria();
    }

    public boolean isIsbnExists(String isbn, Long id) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();

            CriteriaBuilder cb = session.getCriteriaBuilder();
            CriteriaQuery<Long> cq = cb.createQuery(Long.class);
            Root<Book> root = cq.from(Book.class);

            if (id == null || id == 0) {
                cq.select(cb.count(root)).where(cb.equal(root.get("isbn"), isbn));
            } else {
                cq.select(cb.count(root)).where(cb.and(cb.equal(root.get("isbn"), isbn), cb.not(cb.equal(root.get("id"), id))));
            }

            Long result = session.createQuery(cq).getSingleResult();
            
            tx.commit();

            return result >= 1;
        }
    }
    
    public void updateBook(Book book) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            if (book.getContent() == null) {
                book.setContent(getContent(book.getId()));
            } 
            
            session.merge(book);
            
            tx.commit();
        }
    }
    
    public void addBook(Book book) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            session.persist(book);
            
            tx.commit();
        }
    }    
    
    public void deleteBook(Book book) {
        try (Session session = sessionFactory.openSession()) {
            Transaction tx = session.beginTransaction();
            
            session.remove(book);
            
            tx.commit();
        }
//        MutationQuery query = getSession().createMutationQuery("delete from Book where id = :id");
//        query.setParameter("id", book.getId());
//        int result = query.executeUpdate();
    }    
    
    public byte[] getContent(Long id) {
        try (Session session = sessionFactory.openSession();) {
            byte[] content = session.get(Book.class, id).getContent();
            return content;
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

    public void populateList() {
        runCountCriteria();
        runCurrentCriteria();
    }
}