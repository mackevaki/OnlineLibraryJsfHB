package db;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public interface SavedCriteria<T> {
    CriteriaQuery getCriteriaQuery(Root<T> queryRoot, CriteriaQuery<T> query, CriteriaBuilder cb);
}
