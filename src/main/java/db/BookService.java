package db;

import beans.Pager;
import dao.Page;
import dao.impls.CommonService;
import dao.interfaces.BookServiceInterface;
import entity.*;
import enums.SearchType;
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
    private Pager<Book> pager = new Pager<>(); // for alternative implementation
    private final Page<Book> page = new Page<>(null, 0);
    private SavedCriteria<Book> currentDataCriteria;
    private SavedCriteria<Book> countCriteria;    
    
    public BookService() {
        currentDataCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot);
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root));
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
    public byte[] getContent(Long id) {
        try (Session session = getSessionFactory().openSession();) {
            return session.get(Book.class, id).getContent();
        }
    }

    @Override
    public void updateViewCount(long viewCount, long bookId) {
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

    public void runCurrentCriteria(SortOrder sortOrder, String sortColumn, int startFrom, int pageSize) {
        Session session = getSession();
        if (!session.getTransaction().isActive()) {
            session.beginTransaction();
        }

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> from = criteriaQuery.from(Book.class);
        CriteriaQuery<Book> select;

        // specify which fields to return from the table (we exclude content so as not to load the database. Content is obtained only upon request when the book is opened)
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

        select = currentDataCriteria.getCriteriaQuery(criteriaBuilder.construct(Book.class, selection), from, criteriaQuery, criteriaBuilder);

        if (sortOrder.isAscending()) {
            select.orderBy(criteriaBuilder.asc(from.get(sortColumn)));
        } else if (sortOrder.isDescending()) {
            select.orderBy(criteriaBuilder.desc(from.get(sortColumn)));
        }

        TypedQuery<Book> typedQuery = session.createQuery(select);
        typedQuery.setFirstResult(startFrom);
        typedQuery.setMaxResults(pageSize);

        List<Book> list = typedQuery.getResultList();

        page.setList(list);

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
        page.setTotalCount(Integer.parseInt(count.toString()));

        if (session.getTransaction().isActive()) {
            session.getTransaction().commit();
        }
    }

    public void findAll(SortOrder sortOrder, String sortColumn, int startFrom, int pageSize) {
        currentDataCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot);
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root));

        runCountCriteria();
        runCurrentCriteria(sortOrder, sortColumn, startFrom, pageSize);
    }

    public void getBooksByGenre(Long genreId, SortOrder sortOrder, String sortColumn, int startFrom, int pageSize) {
        currentDataCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.equal(root.get("genre").get("id"), genreId));
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root)).where(cb.equal(root.get("genre").get("id"), genreId));

        runCountCriteria();
        runCurrentCriteria(sortOrder, sortColumn, startFrom, pageSize);
    }

    public void getBooksByLetter(Character letter, SortOrder sortOrder, String sortColumn, int startFrom, int pageSize) {
        currentDataCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.like(cb.lower(root.get("name")), letter.toString().toLowerCase() + "%"));
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root)).where(cb.like(cb.lower(root.get("name")), letter.toString().toLowerCase() + "%"));

        runCountCriteria();
        runCurrentCriteria(sortOrder, sortColumn, startFrom, pageSize);
    }

    public void getBooksByAuthor(String authorName, SortOrder sortOrder, String sortColumn, int startFrom, int pageSize) {
        currentDataCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.like(cb.lower(root.get("author").get("fio")), "%" + authorName.toLowerCase() + "%"));
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root)).where(cb.like(cb.lower(root.get("author").get("fio")), "%" + authorName.toLowerCase() + "%"));

        runCountCriteria();
        runCurrentCriteria(sortOrder, sortColumn, startFrom, pageSize);
    }

    public void getBooksByName(String bookName, SortOrder sortOrder, String sortColumn, int startFrom, int pageSize) {
        currentDataCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.like(cb.lower(root.get("name")), "%" + bookName.toLowerCase() + "%"));
        countCriteria = (selectionRoot, queryRoot, query, cb) -> query.select(cb.count(queryRoot)).where(cb.like(cb.lower(queryRoot.get("name")), "%" + bookName.toLowerCase() + "%"));

        runCountCriteria();
        runCurrentCriteria(sortOrder, sortColumn, startFrom, pageSize);
    }

    @Override
    public Page<Book> find(BookSearchValues bookSearchValues, int startFrom, int pageSize, String sortColumn, SortOrder sortOrder) {
        if (sortOrder == null) {
            sortOrder = SortOrder.ASCENDING;
        }

        if (sortColumn == null) {
            sortColumn = Book_.NAME;
        }

        // if search variables are filled, use them
        if (bookSearchValues != null) {
            // search by selected genre
            if (bookSearchValues.getGenreId() != 0L) {
                getBooksByGenre(bookSearchValues.getGenreId(), sortOrder, sortColumn, startFrom, pageSize);
            // search by selected letter
            } else if (bookSearchValues.getLetter() != null && Character.isLetter(bookSearchValues.getLetter())) {
                getBooksByLetter(bookSearchValues.getLetter(), sortOrder, sortColumn, startFrom, pageSize);
            // search by text included in ...
            } else if (bookSearchValues.getSearchText() != null && !bookSearchValues.getSearchText().trim().isEmpty()) {
                // ... author book's field
                if (bookSearchValues.getSearchType() == SearchType.AUTHOR) {
                    getBooksByAuthor(bookSearchValues.getSearchText(), sortOrder, sortColumn, startFrom, pageSize);
                // ... title book's field
                } else if (bookSearchValues.getSearchType() == SearchType.TITLE) {
                    getBooksByName(bookSearchValues.getSearchText(), sortOrder, sortColumn, startFrom, pageSize);
                }
            } else {
                findAll(sortOrder, sortColumn, startFrom, pageSize);
            }
        }

        return page;
    }

    /* old implementation using Pager with 'from' and 'to' fields w/o sorting*/
    public void runCurrentCriteria() {
        Session session = getSession();
        if (!session.getTransaction().isActive()) {
            session.beginTransaction();
        }

        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaQuery<Book> criteriaQuery = criteriaBuilder.createQuery(Book.class);
        Root<Book> from = criteriaQuery.from(Book.class);
        CriteriaQuery<Book> select;

        // which fields to return from the table (we exclude content so as not to load the database. Content is obtained only upon request when the book is opened)
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

        select = currentDataCriteria.getCriteriaQuery(criteriaBuilder.construct(Book.class, selection), from, criteriaQuery, criteriaBuilder).orderBy(criteriaBuilder.asc(from.get(Book_.NAME)));

        TypedQuery<Book> typedQuery = session.createQuery(select);
        typedQuery.setFirstResult(pager.getFrom());
        typedQuery.setMaxResults(pager.getTo());

        List<Book> list = typedQuery.getResultList();

        pager.setList(list);

        if (session.getTransaction().isActive()) {
            session.getTransaction().commit();
        }
    }

    public void getBooksByGenre(Long genreId) {
        currentDataCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.equal(root.get("genre").get("id"), genreId));
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root)).where(cb.equal(root.get("genre").get("id"), genreId));

        runCountCriteria();
        runCurrentCriteria();
    }

    public void getBooksByLetter(Character letter) {
        currentDataCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.like(cb.lower(root.get("name")), letter.toString().toLowerCase() + "%"));
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root)).where(cb.like(cb.lower(root.get("name")), letter.toString().toLowerCase() + "%"));

        runCountCriteria();
        runCurrentCriteria();
    }

    public void getBooksByAuthor(String authorName) {
        currentDataCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.like(cb.lower(root.get("author").get("fio")), "%" + authorName.toLowerCase() + "%"));
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root)).where(cb.like(cb.lower(root.get("author").get("fio")), "%" + authorName.toLowerCase() + "%"));

        runCountCriteria();
        runCurrentCriteria();
    }

    public void getBooksByName(String bookName) {
        currentDataCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot).where(cb.like(cb.lower(root.get("name")), "%" + bookName.toLowerCase() + "%"));
        countCriteria = (selectionRoot, queryRoot, query, cb) -> query.select(cb.count(queryRoot)).where(cb.like(cb.lower(queryRoot.get("name")), "%" + bookName.toLowerCase() + "%"));

        runCountCriteria();
        runCurrentCriteria();
    }

    public void findAll() {
        currentDataCriteria = (selectionRoot, root, query, cb) -> query.select(selectionRoot);
        countCriteria = (selectionRoot, root, query, cb) -> query.select(cb.count(root));

        runCountCriteria();
        runCurrentCriteria();
    }

    public void populateList() {
        runCountCriteria();
        runCurrentCriteria();
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