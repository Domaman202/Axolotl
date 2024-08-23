package axl.compiler.analysis.syntax;

import axl.compiler.analysis.lexical.TokenGroup;
import axl.compiler.analysis.lexical.TokenType;
import axl.compiler.analysis.lexical.utils.Frame;
import axl.compiler.analysis.lexical.utils.TokenStream;
import axl.compiler.analysis.syntax.ast.Node;
import axl.compiler.analysis.syntax.ast.expression.Expression;
import axl.compiler.analysis.syntax.utils.Analyzer;
import axl.compiler.analysis.syntax.utils.LinkedList;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class DefaultSyntaxAnalyzer implements SyntaxAnalyzer {

    private final List<Analyzer> analyzers = new ArrayList<>();

    @Getter
    @Setter
    private Object context;

    @Override
    @SuppressWarnings("all")
    public Node analyze(TokenStream tokenStream) {
        return analyze(tokenStream, Node.class);
    }

    @Override
    @SuppressWarnings("all")
    public final Node analyze(TokenStream tokenStream, Class<? extends Node>... allowed) {
        Node node = null;
        Frame frame;

        for (Analyzer analyzer : analyzers) {
            if (!canCast(analyzer.getTarget(), allowed))
                continue;

            Object context = this.context;
            frame = tokenStream.createFrame();
            node = analyzer.analyze(this, tokenStream);
            if (node != null)
                break;

            this.context = context;
            tokenStream.restoreFrame(frame);
        }

        return node;
    }

    @Override
    @SuppressWarnings("all")
    public final List<? extends Node> analyze(TokenStream tokenStream, TokenType delimiter, Class<? extends Node>... allowed) {
        if (delimiter != null && delimiter.getGroup() != TokenGroup.DELIMITER)
            throw new IllegalArgumentException(); // TODO

        List<Node> nodes = new ArrayList<>();

        while (tokenStream.hasNext()) {
            if (delimiter != null) {
                if (tokenStream.get().getType() != delimiter)
                    return nodes;

                tokenStream.next();
            }

            Node node = analyze(tokenStream, allowed);
            if (node == null)
                return nodes;

            nodes.add(node);
        }

        return nodes;
    }

    @Override
    public Expression analyzeExpression(TokenStream tokenStream, LinkedList<Analyzer> without) {
        Expression node = null;
        Frame frame;

        boolean flag = without != null;
        for (Analyzer analyzer : analyzers) {
            if (flag) {
                if (without.contains(analyzer) && without.value == analyzer)
                    flag = false;
                continue;
            }

            frame = tokenStream.createFrame();
            node = analyzer.analyzeExpression(this, tokenStream, without);
            if (node != null)
                break;

            tokenStream.restoreFrame(frame);
        }

        return node;
    }

    @Override
    public List<? extends Expression> analyzeExpression(TokenStream tokenStream, TokenType delimiter, LinkedList<Analyzer> without) {
        if (delimiter != null && delimiter.getGroup() != TokenGroup.DELIMITER)
            throw new IllegalArgumentException(); // TODO

        List<Expression> expressions = new ArrayList<>();

        while (tokenStream.hasNext()) {
            if (delimiter != null) {
                if (tokenStream.get().getType() != delimiter)
                    return expressions;

                tokenStream.next();
            }

            Expression expression = analyzeExpression(tokenStream, without);
            if (expression == null)
                return expressions;

            expressions.add(expression);
        }

        return expressions;
    }

    @Override
    public void addAnalyzer(Analyzer analyzer) {
        this.analyzers.add(analyzer);
    }

    private boolean canCast(Class<? extends Node> target, Class<? extends Node>[] allowed) {
        for (Class<? extends Node> allowedType : allowed)
            if (allowedType.isAssignableFrom(target))
                return true;

        return false;
    }

}

