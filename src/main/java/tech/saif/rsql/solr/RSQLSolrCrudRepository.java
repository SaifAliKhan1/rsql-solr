package tech.saif.rsql.solr;

import java.io.Serializable;

import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.repository.SolrCrudRepository;

/**
 *
 * @author Saif Ali Khan
 *
 * @param <T>
 * @param <V>
 */
public interface RSQLSolrCrudRepository<T, V extends Serializable> extends SolrCrudRepository<T, V> {

    SolrOperations getSolrOperations();

    Class<T> getEntityClass();

}
