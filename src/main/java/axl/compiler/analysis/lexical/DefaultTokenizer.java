package axl.compiler.analysis.lexical;

import axl.compiler.IFile;
import axl.compiler.analysis.lexical.utils.TokenizerUtils;
import lombok.Getter;
import lombok.NonNull;

public class DefaultTokenizer implements Tokenizer, TokenizerUtils {

    @Getter
    private final IFile file;

    private int offset;

    private int line;

    private int column;

    private DefaultTokenizerFrame last;

    public DefaultTokenizer(@NonNull IFile file, IToken last) {
        this.file = file;

        if (last != null) {
            this.offset = last.getOffset();
            this.line = last.getLine();
            next(last.getLength());
        }

        // TODO skip();
    }

    @Override
    public IToken tokenize() {
        this.last = new DefaultTokenizerFrame(offset, line, column);
        if (end())
            return null; // TODO throw
        while (Character.isWhitespace(peek())) {
            skip();
        }
        DefaultToken token = null; // TODO tokenize

        token.offset = last.offset();
        token.column = last.column();
        token.line = last.line();
        token.length = this.offset - last.offset();

        // TODO skip();
        return token;
    }

    private IToken readIdentifyOrKeyword() {
        do {
            next();
        } while (isIdentifierPart(peek()));

        TokenType type = TokenType.getByRepresentation(slice());
        return new DefaultToken(type == null ? TokenType.IDENTIFY : type);
    }

    private IToken readNumber() {
        StringBuilder buffer = new StringBuilder();
        char current = peek();
        if (current == '0' && (peek(1) == 'x' || (peek(1) == 'X'))) {
            readHexNumber(2);
        }

        boolean isFloat = false;
        boolean isDecimal = false;
        while (true) {
            if (current == '.') { // FIXME TokenType.DOT
                isDecimal = true;
                if (isFloat)
                    throw new RuntimeException("Invalid float number ");
                isFloat = true;
            } else if (current == 'e' || current == 'E') {
                isDecimal = true;
                int exp = readScientificNumber();
                buffer.append(current).append(exp);
                break;
            } else if (!Character.isDigit(current)) {
                break;
            }
            buffer.append(current);
            current = next();
        }
        String number = buffer.toString();
        if (isDecimal) {
            return new DefaultToken(TokenType.DECIMAL_LITERAL);
        }else if (isFloat) {
            return new DefaultToken(TokenType.FLOAT_LITERAL);
        } else {
            return new DefaultToken(TokenType.INTEGER_LITERAL);
        }
    }
    private int readScientificNumber() {
        int sign = switch (next()) {
            case '-' -> { skip(); yield -1; }
            case '+' -> { skip(); yield 1; }
            default -> 1;
        };

        boolean hasValue = false;
        char current = peek(0);
        while (current == '0') {
            hasValue = true;
            current = next();
        }
        int result = 0;
        int position = 0;
        while (Character.isDigit(current)) {
            result = result * 10 + (current - '0');
            current = next();
            position++;
        }
        if (position == 0 && !hasValue) throw new RuntimeException("Empty floating point exponent");
        if (position >= 4) {
            if (sign > 0) throw new RuntimeException("Float number too large");
            else throw new RuntimeException("Float number too small");
        }
        return sign * result;
    }

    private IToken readHexNumber(int skipChars) {
        final StringBuilder buffer = new StringBuilder();
        // Skip HEX prefix 0x or #
        for (int i = 0; i < skipChars; i++) skip();
        char current = peek(0);
        while (isHexNumber(current) || (current == '_')) {
            if (current != '_') {
                buffer.append(current);
            }
            current = next();
        }

        if (buffer.isEmpty()) throw new RuntimeException("Empty HEX value");
        if (peek(-1) == '_') throw new RuntimeException("HEX value cannot end with _");


        String number = buffer.toString();
        return new DefaultToken(TokenType.HEX_LITERAL);

    }
    private void readComment() {
        skip();
        skip();
        char current = peek();
        while ("\r\n\0".indexOf(current) == -1) {
            current = next();
        }
    }
    private char next(int n) {
        for (int i = 1; i < n; i++)
            next();

        return next();
    }
    private void skip() {
        final char result = peek();
        if (result == '\n') {
            line++;
            column = 1;
        } else column++;
        offset++;
    }

    private char next() {
        skip();
        return peek(0);
    }


    private char peek() {
        return peek(0);
    }

    private char peek(int n) {
        int offset = this.offset + n;
        if (offset >= file.getContent().length())
            return '\0';

        return file.getContent().charAt(offset);
    }

    private boolean end() {
        return offset >= file.getContent().length();
    }

    private String slice() {
        return getFile()
                .getContent()
                .subSequence(
                        last.offset(),
                        this.offset
                ).toString();
    }

    @Override
    public boolean isProcessed() {
        return end();
    }

}

record DefaultTokenizerFrame(int offset, int line, int column) {

}