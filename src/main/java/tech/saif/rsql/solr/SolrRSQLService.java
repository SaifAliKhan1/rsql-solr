package tech.saif.rsql.solr;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.SortClause;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.solr.core.SolrOperations;
import org.springframework.data.solr.core.mapping.SimpleSolrMappingContext;
import org.springframework.data.solr.repository.support.SolrEntityInformationCreatorImpl;

import cz.jirutka.rsql.parser.RSQLParser;
import cz.jirutka.rsql.parser.ast.Node;

/**
 *
 * @author Saif Ali Khan
 *
 * @param <T>
 * @param <V>
 */
public class SolrRSQLService<T, V extends Serializable> {

    private RSQLSolrCrudRepository<T, V> rSQLSolrCrudRepository;
    private Map<String, String>          fieldMap;
    private SolrClient                   solrClient;
    private String                       collectionName;
    private Class<T>                     type;

    private static Logger                logger = LoggerFactory.getLogger(SolrRSQLService.class);

    public SolrRSQLService(RSQLSolrCrudRepository<T, V> rSQLSolrCrudRepository) {
        super();
        this.rSQLSolrCrudRepository = rSQLSolrCrudRepository;
        this.type = this.rSQLSolrCrudRepository.getEntityClass();
        this.fieldMap = SolrFieldLoader.getSolrFieldMap(type);
        SolrOperations solrOperations = rSQLSolrCrudRepository.getSolrOperations();
        this.solrClient = solrOperations.getSolrClient();
        this.collectionName = new SolrEntityInformationCreatorImpl(new SimpleSolrMappingContext())
                .getEntityInformation(type).getCollectionName();
    }

    private SolrQuery getSolrQuery(RsqlSelector selector) {
        SolrQuery solrQuery = new SolrQuery("*:*");
        if (StringUtils.isNotBlank(selector.getFilters())) {
            Node rootNode = new RSQLParser().parse(selector.getFilters());
            String solrFilterQuery = rootNode.accept(new SolrRSQLVisitor(SolrFieldLoader.getSolrFieldMap(type)));
            logger.info("SOLR QUERY BUILT FROM RSQL FILTERS {}", solrFilterQuery);
            solrQuery.setFilterQueries(solrFilterQuery);
        }
        List<SortClause> solrSolrParams = builtSolrSortQuery(selector.getPageable());
        solrQuery.setSorts(solrSolrParams);
        solrQuery.setStart(selector.getStart());
        solrQuery.setRows(selector.getRows());
        return solrQuery;
    }

    public QueryResponse findQueryResponseByRSQLSelector(RsqlSelector selector) {
        SolrQuery query = getSolrQuery(selector);
        logger.info("RUNNING SOLR QUERY {} ", query);
        try {
            return solrClient.query(collectionName, query);
        }
        catch (SolrServerException | IOException e) {
            throw new RuntimeException("Unable to run solr query " + query.toQueryString(), e);
        }
    }

    public List<T> findByRSQLSelector(RsqlSelector selector) {
        QueryResponse response = findQueryResponseByRSQLSelector(selector);
        return response.getBeans(type);
    }

    private List<SortClause> builtSolrSortQuery(Pageable pageable) {
        return pageable.getSort().stream()
                .map(o -> new SortClause(getField(o.getProperty()), o.getDirection().name().toLowerCase()))
                .collect(Collectors.toList());
    }

    private String getField(String nodeField) {
        return Optional.ofNullable(fieldMap).filter(f -> f.containsKey(nodeField)).map(f -> f.get(nodeField))
                .orElse(nodeField);
    }
}
