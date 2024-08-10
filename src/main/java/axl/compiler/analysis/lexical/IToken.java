package axl.compiler.analysis.lexical;

public interface IToken {

    int getOffset();

    int getLength();

    int getLine();

    int getColumn();

    TokenType getType();

}
