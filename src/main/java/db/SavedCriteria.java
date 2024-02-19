package db;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

import java.io.Serializable;

/**
 * The alternative to deprecated DetachedCriteria.
 * @return CriteriaQuery object constructed according to the passed arguments Selection, Root, initial CriteriaQuery, CriteriaBuilder containing imposed conditions
 */
public interface SavedCriteria<T> extends Serializable {
    CriteriaQuery getCriteriaQuery(Selection<T> selection, Root<T> from, CriteriaQuery query, CriteriaBuilder cb);
}
