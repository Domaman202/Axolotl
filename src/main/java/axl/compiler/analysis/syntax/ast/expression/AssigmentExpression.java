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
public class AssigmentExpression extends Expression {

    private final IToken operation;
    private final Expression left;
    private final Expression right;

    @SubAnalyzer(target = BinaryExpression.class)
    public static class AssigmentExpressionAnalyzer extends ExpressionAnalyzer {
        @Override
        public Node analyzeExpression(SyntaxAnalyzer syntaxAnalyzer, TokenStream tokenStream, LinkedList<Analyzer> without) {
            Node leftNode = syntaxAnalyzer.analyzeExpression(tokenStream, new LinkedList<>(without, this));
            IToken token = tokenStream.get();
            if (token == null)
                return leftNode;
            return switch (token.getType()) {
                case ASSIGN,
                     PLUS_ASSIGN,
                     MINUS_ASSIGN,
                     MULTIPLY_ASSIGN,
                     DIVIDE_ASSIGN,
                     MODULO_ASSIGN,
                     BIT_AND_ASSIGN,
                     BIT_OR_ASSIGN,
                     BIT_XOR_ASSIGN,
                     BIT_SHIFT_LEFT_ASSIGN,
                     BIT_SHIFT_RIGHT_ASSIGN -> {
                    IToken operation = tokenStream.next();
                    Expression left = (Expression) leftNode;
                    Expression right = (Expression) syntaxAnalyzer.analyzeExpression(tokenStream, without);
                    yield new BinaryExpression(operation, left, right);
                }
                default -> leftNode;
            };
        }
    }

    @Override
    public String toString() {
        return "AssigmentExpression{"+
                "operation="+operation.getType()+
                ",left="+left+
                ",right="+right+
                '}';
    }
}
