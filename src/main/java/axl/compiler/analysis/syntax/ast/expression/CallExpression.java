package axl.compiler.analysis.syntax.ast.expression;

import axl.compiler.analysis.lexical.IToken;
import axl.compiler.analysis.lexical.TokenType;
import axl.compiler.analysis.lexical.utils.TokenStream;
import axl.compiler.analysis.syntax.SyntaxAnalyzer;
import axl.compiler.analysis.syntax.ast.Node;
import axl.compiler.analysis.syntax.utils.Analyzer;
import axl.compiler.analysis.syntax.utils.ExpressionAnalyzer;
import axl.compiler.analysis.syntax.utils.LinkedList;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class CallExpression extends Expression {

    private final IToken name;
    private final List<Expression> arguments;


//    public static class CallExpressionAnalyzer extends ExpressionAnalyzer {
//        @Override
//        public Node analyzeExpression(SyntaxAnalyzer syntaxAnalyzer, TokenStream tokenStream, LinkedList<Analyzer> without) {
//            if (tokenStream.get().getType() != TokenType.IDENTIFY)
//                return null;
//
//            IToken name = tokenStream.next();
//
//            if (tokenStream.next().getType() != TokenType.LEFT_PARENT)
//                return null;
//
//            List<Expression> arguments =
//        }
//    }

}
