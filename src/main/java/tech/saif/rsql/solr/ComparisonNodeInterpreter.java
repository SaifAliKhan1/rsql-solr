package tech.saif.rsql.solr;

import cz.jirutka.rsql.parser.ast.ComparisonNode;

/**
 *
 * @author Saif Ali Khan
 *
 * @param <T>
 */
@FunctionalInterface
public interface ComparisonNodeInterpreter<T> {

    T interpret(ComparisonNode comparisonNode);

}
