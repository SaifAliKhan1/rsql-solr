package tech.saif.rsql.solr;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;

/**
 *
 * @author Saif Ali Khan
 *
 */
public class RsqlSelector implements Serializable {

    private static final long serialVersionUID = -1032074044786151327L;

    private String            fields;

    private String            filters          = StringUtils.EMPTY;

    private String            sort             = CommonConstants.FIELD_NAME_ID;

    private Integer           start            = 0;

    private Integer           rows             = CommonConstants.DEFAULT_NUMBER_OF_ROWS;

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public String getFilters() {
        return filters;
    }

    public void setFilters(String filters) {
        this.filters = filters;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getStart() {
        return start;
    }

    public void setStart(Integer start) {
        this.start = start;
    }

    public Integer getRows() {
        return rows;
    }

    public void setRows(Integer rows) {
        this.rows = rows;
    }

    public Pageable getPageable() {
        String sortFields = sort;
        String[] splittedSortFields = sortFields.split(",");
        Order[] orders = new Order[splittedSortFields.length];
        for (int i = 0; i < splittedSortFields.length; i++) {
            String splittedSortField = splittedSortFields[i];
            Direction sortDirection = Direction.ASC;
            if (splittedSortField.startsWith("-")) {
                sortDirection = Direction.DESC;
                splittedSortField = splittedSortField.substring(1);
            }
            orders[i] = new Order(sortDirection, splittedSortField);
        }
        return new CustomPageable(start, rows, Sort.by(orders));
    }

}
