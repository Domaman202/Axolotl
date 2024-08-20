package axl.compiler.analysis.syntax.utils;

import axl.compiler.analysis.lexical.utils.TokenStream;
import axl.compiler.analysis.syntax.SyntaxAnalyzer;
import axl.compiler.analysis.syntax.ast.Node;
import lombok.Getter;

import java.util.List;

public abstract class Analyzer {

    {
        SubAnalyzer subAnalyzer = this.getClass().getAnnotation(SubAnalyzer.class);
        if (subAnalyzer == null)
            throw new IllegalArgumentException("The analyzer \"" + this.getClass().getName() + "\" must have an annotation \"" + SubAnalyzer.class.getName() + "\"!");

        this.target = subAnalyzer.target();
        this.allowed = subAnalyzer.allowed();
    }

    @Getter
    private final Class<? extends Node> target;

    @Getter
    private final Class<? extends Node>[] allowed;

    public abstract Node analyze(SyntaxAnalyzer syntaxAnalyzer, TokenStream tokenStream);

    public Node analyzeExpression(SyntaxAnalyzer syntaxAnalyzer, TokenStream tokenStream, LinkedList<Analyzer> without) {
        return analyze(syntaxAnalyzer, tokenStream);
    }
}
