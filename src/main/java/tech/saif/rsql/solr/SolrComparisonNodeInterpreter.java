package tech.saif.rsql.solr;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import cz.jirutka.rsql.parser.ast.ComparisonNode;

/**
 *
 * @author Saif Ali Khan
 *
 */
public class SolrComparisonNodeInterpreter implements ComparisonNodeInterpreter<String> {

    private static final String WILDCARD_CHAR         = "*";
    private static final String IN_FIELD_TEMPLATE     = "( %s:(%s))";
    private static final String NOT_IN_FIELD_TEMPLATE = "( -%s:(%s))";
    private static final String RANGE_FIELD_TEMPLATE  = "( %s:[%s TO %s])";

    private Map<String, String> fieldMap;

    public SolrComparisonNodeInterpreter(Map<String, String> fieldMap) {
        super();
        this.fieldMap = fieldMap;
    }

    public SolrComparisonNodeInterpreter() {
        super();
    }

    @Override
    public String interpret(ComparisonNode comparisonNode) {
        final ComparisonOperatorProxy operator = ComparisonOperatorProxy.asEnum(comparisonNode.getOperator());
        switch (operator) {
            case EQUAL:
                return createEqual(comparisonNode);
            case GREATER_THAN:
                return createGreaterThan(comparisonNode);
            case GREATER_THAN_OR_EQUAL:
                return createGreaterThanOrEqual(comparisonNode);
            case IN:
                return createIn(comparisonNode);
            case LESS_THAN:
                return createLessThan(comparisonNode);
            case LESS_THAN_OR_EQUAL:
                return createLessThanOrEqual(comparisonNode);
            case NOT_EQUAL:
                return createNotEqual(comparisonNode);
            case NOT_IN:
                return createNotIn(comparisonNode);
        }
        return null;
    }

    private String createIn(ComparisonNode comparisonNode) {
        String fieldName = getField(comparisonNode);
        List<String> arguments = comparisonNode.getArguments();
        return String.format(IN_FIELD_TEMPLATE, fieldName, arguments.stream().collect(Collectors.joining(" OR ")));

    }

    private String createNotIn(ComparisonNode comparisonNode) {
        String fieldName = getField(comparisonNode);
        List<String> arguments = comparisonNode.getArguments();
        return String.format(NOT_IN_FIELD_TEMPLATE, fieldName, arguments.stream().collect(Collectors.joining(" OR ")));
    }

    private String createNotEqual(ComparisonNode comparisonNode) {
        String fieldName = getField(comparisonNode);
        List<String> arguments = comparisonNode.getArguments();
        return String.format(NOT_IN_FIELD_TEMPLATE, fieldName, arguments.stream().collect(Collectors.joining(" OR ")));
    }

    private String createLessThanOrEqual(ComparisonNode comparisonNode) {
        String fieldName = getField(comparisonNode);
        String firstArgument = firstArg(comparisonNode);
        return String.format(RANGE_FIELD_TEMPLATE, fieldName, WILDCARD_CHAR, firstArgument);
    }

    private String createLessThan(ComparisonNode comparisonNode) {
        String fieldName = getField(comparisonNode);
        String firstArgument = firstArg(comparisonNode);
        return String.format(RANGE_FIELD_TEMPLATE, fieldName, WILDCARD_CHAR, firstArgument);
    }

    private String createGreaterThanOrEqual(ComparisonNode comparisonNode) {
        String fieldName = getField(comparisonNode);
        String firstArgument = firstArg(comparisonNode);
        return String.format(RANGE_FIELD_TEMPLATE, fieldName, firstArgument, WILDCARD_CHAR);
    }

    private String createGreaterThan(ComparisonNode comparisonNode) {
        String fieldName = getField(comparisonNode);
        String firstArgument = firstArg(comparisonNode);
        return String.format(RANGE_FIELD_TEMPLATE, fieldName, firstArgument, WILDCARD_CHAR);
    }

    private String createEqual(ComparisonNode comparisonNode) {
        String fieldName = getField(comparisonNode);
        List<String> arguments = comparisonNode.getArguments();
        return String.format(IN_FIELD_TEMPLATE, fieldName, arguments.stream().collect(Collectors.joining(" OR ")));
    }

    private String firstArg(final ComparisonNode comparisonNode) {
        return comparisonNode.getArguments().get(0);

    }

    private String getField(final ComparisonNode comparisonNode) {
        String nodeField = comparisonNode.getSelector();
        return Optional.ofNullable(fieldMap).filter(f -> f.containsKey(nodeField)).map(f -> f.get(nodeField))
                .orElse(nodeField);
    }

}
