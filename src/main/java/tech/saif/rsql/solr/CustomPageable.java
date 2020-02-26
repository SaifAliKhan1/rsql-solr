package tech.saif.rsql.solr;

import java.io.Serializable;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

/**
 *
 * Implementation of {@link Pageable}
 *
 * @author Saif Ali Khan
 *
 */
public class CustomPageable implements Pageable, Serializable {

    private static final long serialVersionUID = 7404955039863647092L;

    private final int         offset;
    private final int         rows;
    private final Sort        sort;

    public CustomPageable(int offset, int rows) {
        this(offset, rows, null);
    }

    public CustomPageable() {
        this(0, CommonConstants.DEFAULT_NUMBER_OF_ROWS, null);
    }

    public CustomPageable(int page, int size, Direction direction, String... properties) {
        this(page, size, Sort.by(direction, properties));
    }

    public CustomPageable(int page, int size, Sort sort) {
        if (0 > page) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        if (0 >= size) {
            throw new IllegalArgumentException("Page size must not be less than or equal to zero!");
        }
        this.offset = page;
        this.rows = size;
        this.sort = sort;
    }

    @Override
    public int getPageSize() {
        return rows;
    }

    @Override
    public long getOffset() {
        return offset;
    }

    @Override
    public Sort getSort() {
        return sort;
    }

    @Override
    public int getPageNumber() {
        return (offset / rows) + 1;
    }

    @Override
    public Pageable next() {
        return new CustomPageable(offset + rows, rows, sort);
    }

    @Override
    public Pageable previousOrFirst() {

        if (offset - rows > 0) {
            return new CustomPageable(offset - rows, rows, sort);
        }
        else {
            return new CustomPageable(0, rows, sort);
        }
    }

    @Override
    public Pageable first() {
        return new CustomPageable(0, rows, sort);
    }

    @Override
    public boolean hasPrevious() {
        return offset - rows >= 0;
    }

    @Override
    public String toString() {
        return "[offset=" + offset + ", rows=" + rows + "]";
    }
}
