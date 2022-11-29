package db;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Selection;

public interface SavedCriteria<T> {
    CriteriaQuery getCriteriaQuery(Selection<T> selectionRoot, Root<T> queryRoot, CriteriaQuery query, CriteriaBuilder cb);
}
