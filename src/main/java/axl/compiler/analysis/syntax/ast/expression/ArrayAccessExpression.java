package axl.compiler.analysis.syntax.ast.expression;

import axl.compiler.analysis.lexical.IToken;
import axl.compiler.analysis.lexical.TokenType;
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
public class ArrayAccessExpression extends Expression {

    private final Expression array;
    private final Expression index;

    @SubAnalyzer(target = ArrayAccessExpression.class)
    public static class ArrayAccessExpressionAnalyzer extends ExpressionAnalyzer {
        @Override
        public Node analyzeExpression(SyntaxAnalyzer syntaxAnalyzer, TokenStream tokenStream, LinkedList<Analyzer> without) {
            Node leftNode = syntaxAnalyzer.analyzeExpression(tokenStream, new LinkedList<>(without, this));
            IToken token = tokenStream.get();
            if (token == null || token.getType() != TokenType.LEFT_SQUARE)
                return leftNode;
            tokenStream.next();
            Expression array = (Expression) leftNode;
            Expression index = (Expression) syntaxAnalyzer.analyzeExpression(tokenStream, without);
            if (tokenStream.next().getType() != TokenType.RIGHT_SQUARE)
                throw new RuntimeException(); // todo: сообщить пользователю о необходимости закрыть скобку
            return new ArrayAccessExpression(array, index);
        }
    }

    @Override
    public String toString() {
        return "ArrayAccessExpression{"+
                "array="+array+
                ",index="+index+
                '}';
    }
}
