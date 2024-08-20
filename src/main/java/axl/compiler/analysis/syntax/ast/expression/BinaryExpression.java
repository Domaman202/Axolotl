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

import java.util.List;

@Getter
@AllArgsConstructor
public class BinaryExpression extends Expression {
    private final IToken operation;
    private final Expression left;
    private final Expression right;

    @SubAnalyzer(target = BinaryExpression.class)
    public static class BinaryExpressionAnalyzer extends ExpressionAnalyzer {
        private final List<TokenType> tokenTypes;

        public BinaryExpressionAnalyzer(List<TokenType> tokenTypes) {
            this.tokenTypes = tokenTypes;
        }

        @Override
        public Node analyzeExpression(SyntaxAnalyzer syntaxAnalyzer, TokenStream tokenStream, LinkedList<Analyzer> without) {
            Node leftNode = syntaxAnalyzer.analyzeExpression(tokenStream, new LinkedList<>(without, this));
            IToken token = tokenStream.get();
            if (token == null || !tokenTypes.contains(token.getType()))
                return leftNode;
            IToken operation = tokenStream.next();
            Expression left = (Expression) leftNode;
            Expression right = (Expression) syntaxAnalyzer.analyzeExpression(tokenStream, without);
            return new BinaryExpression(operation, left, right);
        }
    }

    @Override
    public String toString() {
        return "BinaryExpression{"+
                "operation="+operation.getType()+
                ",left="+left+
                ",right="+right+
                '}';
    }
}
