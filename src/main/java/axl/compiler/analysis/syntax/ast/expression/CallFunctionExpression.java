package axl.compiler.analysis.syntax.ast.expression;

import axl.compiler.analysis.lexical.TokenType;
import axl.compiler.analysis.lexical.utils.Frame;
import axl.compiler.analysis.lexical.utils.TokenStream;
import axl.compiler.analysis.lexical.utils.TokenStreamUtils;
import axl.compiler.analysis.syntax.SyntaxAnalyzer;
import axl.compiler.analysis.syntax.utils.Analyzer;
import axl.compiler.analysis.syntax.utils.ExpressionAnalyzer;
import axl.compiler.analysis.syntax.utils.LinkedList;
import axl.compiler.analysis.syntax.utils.SubAnalyzer;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor
public class CallFunctionExpression extends Expression {
    private final Expression function;
    private final List<Expression> args;

    @SubAnalyzer(target = CallFunctionExpression.class)
    public static class CallFunctionExpressionAnalyzer extends ExpressionAnalyzer {
        @Override
        public Expression analyzeExpression(SyntaxAnalyzer syntaxAnalyzer, TokenStream tokenStream, LinkedList<Analyzer> without) {
            Expression left = syntaxAnalyzer.analyzeExpression(tokenStream, new LinkedList<>(without, this));
            if (left == null || TokenStreamUtils.nextTokenTypeNot(tokenStream, TokenType.LEFT_PARENT))
                return null;
            List<Expression> args = new ArrayList<>();
            if (tokenStream.get().getType() == TokenType.RIGHT_PARENT)
                tokenStream.next();
            else {
                while (true) {
                    Expression expr = syntaxAnalyzer.analyzeExpression(tokenStream, null);
                    if (expr == null)
                        break;
                    args.add(expr);
                    Frame frame = tokenStream.createFrame();
                    TokenType type = tokenStream.next().getType();
                    if (type == TokenType.COMMA)
                        continue;
                    if (type == TokenType.RIGHT_PARENT)
                        break;
                    tokenStream.restoreFrame(frame);
                }
            }
            return new CallFunctionExpression(left, args);
        }
    }

    @Override
    public String toString() {
        return "CallFunction{"+
                "function="+function+
                ",args="+args+
                '}';
    }
}
