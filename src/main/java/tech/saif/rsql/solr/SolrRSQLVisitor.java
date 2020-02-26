package tech.saif.rsql.solr;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cz.jirutka.rsql.parser.ast.AndNode;
import cz.jirutka.rsql.parser.ast.ComparisonNode;
import cz.jirutka.rsql.parser.ast.LogicalNode;
import cz.jirutka.rsql.parser.ast.NoArgRSQLVisitorAdapter;
import cz.jirutka.rsql.parser.ast.Node;
import cz.jirutka.rsql.parser.ast.OrNode;

/**
 *
 * @author Saif Ali Khan
 *
 */
public class SolrRSQLVisitor extends NoArgRSQLVisitorAdapter<String> {

    private final ComparisonNodeInterpreter<String> comparisonNodeInterpreter;

    public SolrRSQLVisitor(Map<String, String> fieldMap) {
        super();
        this.comparisonNodeInterpreter = new SolrComparisonNodeInterpreter(fieldMap);
    }

    public SolrRSQLVisitor() {
        super();
        this.comparisonNodeInterpreter = new SolrComparisonNodeInterpreter();
    }

    @Override
    public String visit(AndNode node) {
        return visitLogicalNode(node);
    }

    @Override
    public String visit(OrNode node) {
        return visitLogicalNode(node);
    }

    @Override
    public String visit(ComparisonNode node) {
        return comparisonNodeInterpreter.interpret(node);
    }

    private String visitLogicalNode(final LogicalNode logicalNode) {
        List<Node> childNodes = logicalNode.getChildren();
        return "( " + childNodes.stream().map(this::visitUnknownNode)
                .collect(Collectors.joining(String.format(" %s ", logicalNode.getOperator().name()))) + " )";
    }

    private String visitUnknownNode(final Node node) {
        if (node instanceof LogicalNode) {
            return visitLogicalNode((LogicalNode) node);
        }
        else {
            return visit((ComparisonNode) node);
        }
    }

}
