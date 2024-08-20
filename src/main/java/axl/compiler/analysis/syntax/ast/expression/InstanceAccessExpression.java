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
public class InstanceAccessExpression extends Expression {
    private final Expression instance;
    private final IToken access;

    @SubAnalyzer(target = InstanceAccessExpression.class)
    public static class InstanceAccessExpressionAnalyzer extends ExpressionAnalyzer {
        @Override
        public Node analyzeExpression(SyntaxAnalyzer syntaxAnalyzer, TokenStream tokenStream, LinkedList<Analyzer> without) {
            Node leftNode = syntaxAnalyzer.analyzeExpression(tokenStream, new LinkedList<>(without, this));
            IToken token = tokenStream.get();
            if (token == null || token.getType() != TokenType.DOT)
                return leftNode;
            tokenStream.next();
            Expression instance = (Expression) leftNode;
            IToken access = tokenStream.next();
            return new InstanceAccessExpression(instance, access);
        }
    }

    @Override
    public String toString() {
        return "InstanceAccessExpression{"+
                "instance="+instance+
                ",access="+access.getType()+
                '}';
    }
}
