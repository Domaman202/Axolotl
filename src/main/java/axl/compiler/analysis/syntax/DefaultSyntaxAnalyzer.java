package axl.compiler.analysis.syntax;

import axl.compiler.analysis.lexical.TokenGroup;
import axl.compiler.analysis.lexical.TokenType;
import axl.compiler.analysis.lexical.utils.Frame;
import axl.compiler.analysis.lexical.utils.TokenStream;
import axl.compiler.analysis.syntax.ast.Node;
import axl.compiler.analysis.syntax.utils.Analyzer;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DefaultSyntaxAnalyzer implements SyntaxAnalyzer {

    private final Set<Analyzer> analyzers = new HashSet<>();

    @Getter
    @Setter
    private Object context;

    @Override
    @SuppressWarnings("all")
    public Node analyze(TokenStream tokenStream) {
        return analyze(tokenStream, new Class[]{Node.class});
    }

    @Override
    public Node analyze(TokenStream tokenStream, Analyzer analyzer) {
        return analyze(tokenStream, analyzer.getAllowed());
    }

    @Override
    public Node analyze(TokenStream tokenStream, Analyzer analyzerAllowed, Class<? extends Node>... exclude) {
        Class<? extends Node>[] allowed = analyzerAllowed.getAllowed();

        Node node = null;
        Frame frame;

        for (Analyzer analyzer : analyzers) {
            if (!canCast(analyzer.getTarget(), allowed) || List.of(exclude).contains(analyzer.getTarget()))
                continue;

            frame = tokenStream.createFrame();
            node = analyzer.analyze(this, tokenStream);
            if (node != null)
                break;

            tokenStream.restoreFrame(frame);
        }

        return node;
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
        Node node;

        node = analyze(tokenStream, allowed);
        if (node == null)
            return nodes;

        while (tokenStream.hasNext()) {
            if (delimiter != null) {
                if (tokenStream.get().getType() != delimiter)
                    return nodes;

                tokenStream.next();
            }

            node = analyze(tokenStream, allowed);
            if (node == null)
                return nodes;

            nodes.add(node);
        }

        return nodes;
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

