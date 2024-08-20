package axl.compiler.analysis.syntax;

import axl.compiler.analysis.lexical.TokenType;
import axl.compiler.analysis.lexical.utils.TokenStream;
import axl.compiler.analysis.syntax.ast.Node;
import axl.compiler.analysis.syntax.utils.Analyzer;
import axl.compiler.analysis.syntax.utils.LinkedList;

import java.util.List;

public interface SyntaxAnalyzer {
    Node analyze(TokenStream tokenStream);


    Node analyze(TokenStream tokenStream, Analyzer analyzer);

    Node analyze(TokenStream tokenStream, Analyzer analyzer, Class<? extends Node>... exclude);

    Node analyze(TokenStream tokenStream, Class<? extends Node>... allowed);

    List<? extends Node> analyze(TokenStream tokenStream, TokenType delimiter, Class<? extends Node>... allowed);

    Node analyzeExpression(TokenStream tokenStream, LinkedList<Analyzer> without);

    void addAnalyzer(Analyzer analyzer);

    Object getContext();

    void setContext(Object context);

}
