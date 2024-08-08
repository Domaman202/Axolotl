package axl.compiler.lexer;

public interface IToken {

    int getOffset();

    int getLength();

    int getLine();

    int getColumn();

    TokenType getType();

}
