package axl.compiler.analysis.syntax;

import axl.compiler.analysis.syntax.ast.Argument;
import axl.compiler.analysis.syntax.ast.Function;
import axl.compiler.analysis.syntax.ast.expression.Number;
import axl.compiler.analysis.syntax.ast.statement.Block;
import axl.compiler.analysis.syntax.ast.statement.ConditionStatement;
import axl.compiler.analysis.syntax.ast.statement.ReturnStatement;

public class SyntaxAnalyzerAgent {

    public static SyntaxAnalyzer createSyntaxAnalyzer() {
        SyntaxAnalyzer syntaxAnalyzer = new DefaultSyntaxAnalyzer();
        syntaxAnalyzer.addAnalyzer(new ConditionStatement.ConditionAnalyzer());
        syntaxAnalyzer.addAnalyzer(new ReturnStatement.ReturnAnalyzer());
        syntaxAnalyzer.addAnalyzer(new Argument.ArgumentAnalyzer());
        syntaxAnalyzer.addAnalyzer(new Function.FunctionAnalyzer());
        syntaxAnalyzer.addAnalyzer(new Block.BlockAnalyzer());

        syntaxAnalyzer.addAnalyzer(new Number.NumberAnalyzer());

        return syntaxAnalyzer;
    }

}
