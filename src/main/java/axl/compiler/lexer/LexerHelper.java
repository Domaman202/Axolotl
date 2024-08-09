package axl.compiler.lexer;

public class LexerHelper {
    protected static boolean isIdentifierStart(char current) {
        return (Character.isLetter(current) || (current == '_') || (current == '$'));
    }

    protected static boolean isIdentifierPart(char current) {
        return isIdentifierStart(current) || isNumber(current);
    }
    protected static boolean isNumber(char current) {
        return ('0' <= current && current <= '9');
    }

    protected static boolean isHexNumber(char current) {
        return ('0' <= current && current <= '9')
                || ('a' <= current && current <= 'f')
                || ('A' <= current && current <= 'F');
    }
}
