package axl.compiler.lexer.utils;

public sealed interface Frame permits DefaultFrame {

    int getTokenId();

}
