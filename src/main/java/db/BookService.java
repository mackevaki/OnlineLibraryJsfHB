package db;

import beans.Pager;
import dao.Page;
import dao.impls.CommonService;
import dao.interfaces.BookServiceInterface;
import entity.*;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import models.BookSearchValues;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.primefaces.model.SortOrder;

import java.io.Serializable;
import java.util.List;

@Named("bookService")
@SessionScoped
public class BookService extends CommonService<Book> implements BookServiceInterface<Book>, Serializable {
    private Pager<Book> pager = Pager.getInstance();

    private SavedCriteria<Book> currentCriteria;
    private SavedCriteria<Book> countCriteria;    
    
    public BookService() {
        currentCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot);
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root));
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
                
        Selection<Book>[] selection = new Selection[]{from.get(Book_.ID).alias(Book_.ID),
                from.get(Book_.NAME).alias(Book_.NAME),
                from.get(Book_.IMAGE).alias(Book_.IMAGE),
                from.get(Book_.GENRE).alias(Book_.GENRE),
                from.get(Book_.PAGE_COUNT).alias(Book_.PAGE_COUNT),
                from.get(Book_.ISBN).alias(Book_.ISBN),
                from.get(Book_.PUBLISHER).alias(Book_.PUBLISHER),
                from.get(Book_.AUTHOR).alias(Book_.AUTHOR),
                from.get(Book_.PUBLISH_DATE).alias(Book_.PUBLISH_DATE),
                from.get(Book_.DESCR).alias(Book_.DESCR),
                from.get(Book_.AVG_RATING).alias(Book_.AVG_RATING),
                from.get(Book_.TOTAL_VOTE_COUNT).alias(Book_.TOTAL_VOTE_COUNT),
                from.get(Book_.TOTAL_RATING).alias(Book_.TOTAL_RATING),
                from.get(Book_.VIEW_COUNT).alias(Book_.VIEW_COUNT)
        };
        
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
    
    public void findAll() {
        currentCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot);
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root));
        
        runCountCriteria();
        runCurrentCriteria();
    }

    public void getBooksByGenre(Long genreId) {               
        currentCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.equal(root.get("genre").get("id"), genreId));
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root)).where(cb.equal(root.get("genre").get("id"), genreId));

        runCountCriteria();
        runCurrentCriteria();
    }

    public void getBooksByLetter(Character letter) {
        currentCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.like(cb.lower(root.get("name")), letter.toString().toLowerCase() + "%"));
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root)).where(cb.like(cb.lower(root.get("name")), letter.toString().toLowerCase() + "%"));

        runCountCriteria();
        runCurrentCriteria();
    }

    public void getBooksByAuthor(String authorName) {       
        currentCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.like(cb.lower(root.get("author").get("fio")), "%" + authorName.toLowerCase() + "%"));
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root)).where(cb.like(cb.lower(root.get("author").get("fio")), "%" + authorName.toLowerCase() + "%"));

        runCountCriteria();
        runCurrentCriteria();
    }

    public void getBooksByName(String bookName) {        
        currentCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.like(cb.lower(root.get("name")), "%" + bookName.toLowerCase() + "%"));
        countCriteria = (selectionRoot, queryRoot, query, cb) -> query.select(cb.count(queryRoot)).where(cb.like(cb.lower(queryRoot.get("name")), "%" + bookName.toLowerCase() + "%"));

        runCountCriteria();
        runCurrentCriteria();
    }

    public boolean isIsbnExists(String isbn, Long id) {
        try (Session session = getSessionFactory().openSession()) {
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

    @Override
    public void update(Book book) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            
            if (book.getContent() == null) {
                book.setContent(getContent(book.getId()));
            } 
            
            session.merge(book);
            
            tx.commit();
        }
    }

    @Override
    public Page<Book> find(BookSearchValues bookSearchValues, int startFrom, int pageSize, String sortColumn, SortOrder sortOrder) {
        return null;
    }

    @Override
    public byte[] getContent(Long id) {
        try (Session session = getSessionFactory().openSession();) {
            return session.get(Book.class, id).getContent();
        }
    }

    @Override
    public void updateViewCount(long viewCount, long bookId) {
        // обновляем таблицу book согласно новым данным рейтинга
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Book> criteriaUpdate = builder.createCriteriaUpdate(Book.class);
            Root<Book> root = criteriaUpdate.from(Book.class);

            criteriaUpdate.set(root.get(Book_.VIEW_COUNT), viewCount)
                    .where(builder.equal(root.get(Book_.ID), bookId));

            session.createMutationQuery(criteriaUpdate).executeUpdate();

            tx.commit();
        }
    }

    public byte[] getImage(Long id) {
        try (Session session = getSessionFactory().openSession();) {
            return session.get(Book.class, id).getImage();
        }
    }

    public void populateList() {
        runCountCriteria();
        runCurrentCriteria();
    }

    public void updateRating(Book book) {
        try (Session session = getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();

            CriteriaBuilder builder = session.getCriteriaBuilder();
            CriteriaUpdate<Book> criteriaUpdate = builder.createCriteriaUpdate(Book.class);
            Root<Book> root = criteriaUpdate.from(Book.class);

            criteriaUpdate.set(root.get(Book_.TOTAL_VOTE_COUNT), book.getTotalVoteCount())
                    .set(root.get(Book_.AVG_RATING), book.getAvgRating())
                    .set(root.get(Book_.TOTAL_RATING), book.getTotalRating())
                    .where(builder.equal(root.get(Book_.ID), book.getId()));

            session.createMutationQuery(criteriaUpdate).executeUpdate();

            tx.commit();
        }
    }

    private void updateBookRate(Book book) {
        NativeQuery<Book> query = getSession().createNativeQuery("update Book b set b.total_vote_count=:totalVoteCount, b.total_rating=:totalRating, b.avg_rating=:avgRating where b.id =:id", Book.class);

        query.setParameter("id", book.getId());
        query.setParameter("totalVoteCount", book.getTotalVoteCount());
        query.setParameter("totalRating", book.getTotalRating());
        query.setParameter("avgRating", book.getAvgRating());

        query.executeUpdate();
    }
}