package axl.compiler.analysis.syntax.ast.expression;

import axl.compiler.analysis.lexical.IToken;
import axl.compiler.analysis.lexical.utils.TokenStream;
import axl.compiler.analysis.syntax.SyntaxAnalyzer;
import axl.compiler.analysis.syntax.ast.Node;
import axl.compiler.analysis.syntax.utils.Analyzer;
import axl.compiler.analysis.syntax.utils.ExpressionAnalyzer;
import axl.compiler.analysis.syntax.utils.LinkedList;
import axl.compiler.analysis.syntax.utils.SubAnalyzer;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UnaryExpression extends Expression {
    private final boolean prefix;
    private final IToken operation;
    private final Expression expression;

    @SubAnalyzer(target = UnaryExpression.class)
    public static class UnaryExpressionPostfixAnalyzer extends ExpressionAnalyzer {
        @Override
        public Node analyzeExpression(SyntaxAnalyzer syntaxAnalyzer, TokenStream tokenStream, LinkedList<Analyzer> without) {
            Node leftNode = syntaxAnalyzer.analyzeExpression(tokenStream, new LinkedList<>(without, this));
            IToken token = tokenStream.get();
            if (token == null)
                return leftNode;
            return switch (token.getType()) {
                case INCREMENT, DECREMENT -> {
                    tokenStream.next();
                    yield new UnaryExpression(false, token, (Expression) leftNode);
                }
                default -> leftNode;
            };
        }
    }

    @SubAnalyzer(target = UnaryExpression.class)
    public static class UnaryExpressionPrefixAnalyzer extends ExpressionAnalyzer {
        @Override
        public Node analyzeExpression(SyntaxAnalyzer syntaxAnalyzer, TokenStream tokenStream, LinkedList<Analyzer> without) {
            IToken token = tokenStream.get();
            if (token == null)
                return null;
            return switch (token.getType()) {
                case NOT, BIT_NOT, UNARY_MINUS, INCREMENT, DECREMENT -> {
                    tokenStream.next();
                    Expression right = (Expression) syntaxAnalyzer.analyzeExpression(tokenStream, new LinkedList<>(without, this));
                    yield new UnaryExpression(true, token, right);
                }
                default -> null;
            };
        }
    }

    @Override
    public String toString() {
        return "UnaryExpression{"+
                "prefix="+prefix+
                ",operation="+operation.getType()+
                ",expression="+expression+
                '}';
    }
}
