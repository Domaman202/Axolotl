package axl.compiler.lexer.utils;

public interface TokenizerUtils {

    default boolean isIdentifierStart(char current) {
        return (Character.isLetter(current) || (current == '_') || (current == '$'));
    }

    default boolean isIdentifierPart(char current) {
        return isIdentifierStart(current) || isNumber(current);
    }

    default boolean isNumber(char current) {
        return ('0' <= current && current <= '9');
    }

    default boolean isHexNumber(char current) {
        return ('0' <= current && current <= '9')
                || ('a' <= current && current <= 'f')
                || ('A' <= current && current <= 'F');
    }

    default boolean isOctNumber(char current) {
        return ('0' <= current && current <= '7');
    }

    default boolean isBinNumber(char current) {
        return current == '0' || current == '1';
    }

}