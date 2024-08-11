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

        skip();
    }

    @Override
    public IToken tokenize() {
        this.last = new DefaultTokenizerFrame(offset, line, column);
        if (end())
            throw new RuntimeException();

        DefaultToken token = null; // TODO tokenize

        token.offset = last.offset();
        token.column = last.column();
        token.line = last.line();
        token.length = this.offset - last.offset();

        skip();
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
        if (peek(0) == '0') {
            if (peek(1) == 'x' || peek(1) == 'X')
                return readHexNumber();
            else if (peek(1) == 'b' || peek(1) == 'B')
                return readBinNumber();
            else if (peek(1) == '.')
                return readFloatingPointNumber();
            else if (peek(1) == '_' && isNumber(peek(1)))
                return readDecNumber();

            next();
            return new DefaultToken(TokenType.DEC_NUMBER);
        }

        if (peek(0) == '.') {
            if (!isNumber(peek(1))) {
                next();
                return new DefaultToken(TokenType.DOT);
            }

            return readFloatingPointNumber();
        }

        return readDecNumber();
    }

    private IToken readHexNumber() {
        next(2);
        int cnt = 0;
        boolean zeroStart = true;

        if (peek() != '0') {
            zeroStart = false;
            cnt++;
        }
        if (!isHexNumber(next()))
            throw new RuntimeException();

        boolean lastUnderscore = false;

        for(;;) {
            if (isHexNumber(peek())) {
                lastUnderscore = false;
                if (peek() != '0' && zeroStart) {
                    zeroStart = false;
                    cnt++;
                }
                next();
            }
            else if (peek() == '_') {
                lastUnderscore = true;
                next();
            } else {
                break;
            }
        }

        if (lastUnderscore)
            throw new RuntimeException();

        if (peek() == 'L' || peek() == 'l') {
            if (cnt > 16)
                throw new RuntimeException();
            next();
            return new DefaultToken(TokenType.HEX_LONG_NUMBER);
        }

        if (cnt > 8)
            throw new RuntimeException();
        return new DefaultToken(TokenType.HEX_NUMBER);
    }

    private IToken readBinNumber() {
        next(2);
        int cnt = 0;
        boolean zeroStart = true;

        if (peek() != '0') {
            zeroStart = false;
            cnt++;
        }
        if (!isBinNumber(next()))
            throw new RuntimeException();

        boolean lastUnderscore = false;

        for(;;) {
            if (isBinNumber(peek())) {
                lastUnderscore = false;
                if (peek() != '0' && zeroStart) {
                    zeroStart = false;
                    cnt++;
                }
                next();
            }
            else if (peek() == '_') {
                lastUnderscore = true;
                next();
            } else {
                break;
            }
        }

        if (lastUnderscore)
            throw new RuntimeException();

        if (peek() == 'L' || peek() == 'l') {
            if (cnt > 64)
                throw new RuntimeException();
            next();
            return new DefaultToken(TokenType.BIN_LONG_NUMBER);
        }

        if (cnt > 32)
            throw new RuntimeException();
        return new DefaultToken(TokenType.BIN_NUMBER);
    }

    private IToken readDecNumber() {
        if (!isNumber(next()))
            throw new RuntimeException();

        readDecPart(true);

        if (peek() == '.')
            return readFloatingPointNumber();

        if (peek() == 'L' || peek() == 'l') {
            next();
            return new DefaultToken(TokenType.DEC_LONG_NUMBER);
        }

        return new DefaultToken(TokenType.DEC_NUMBER);
    }

    private IToken readFloatingPointNumber() {
        boolean exp = false;
        readDecPart(false);

        if (peek() == '.') {
            next();
            readDecPart(true);
        }

        if (peek() == 'E' || peek() == 'e') {
            switch (next()) {
                case '-', '+':
                    next();
                default:
            };
            readDecPart(true);
            exp = true;
        }

        if (peek() == 'F' || peek() == 'f') {
            next();
            return new DefaultToken(exp ? TokenType.FLOAT_EXP_NUMBER : TokenType.FLOAT_NUMBER);
        }

        if (peek() == 'D' || peek() == 'd')
            next();

        return new DefaultToken(exp ? TokenType.DOUBLE_EXP_NUMBER : TokenType.DOUBLE_NUMBER);
    }

    private void readDecPart(boolean req) {
        boolean firstUnderscore = true;
        boolean lastUnderscore = false;
        boolean hasNumber = false;

        for (;;) {
            if (isNumber(peek())) {
                lastUnderscore = false;
                firstUnderscore = false;
                hasNumber = true;
                next();
            } else if (peek() == '_') {
                if (firstUnderscore)
                    throw new RuntimeException();

                lastUnderscore = true;
                next();
            } else {
                break;
            }
        }

        if (!hasNumber && req)
            throw new RuntimeException();

        if (lastUnderscore)
            throw new RuntimeException();
    }

    private void readSingleComment() {
        next(2);
        while (peek() != '\r' && peek() != '\n' && peek() !='\0')
            next();
        next();
    }

    private void readMultilineComment() {
        next(2);
        while (peek(0) != '*' && peek(1) != '/')
            next();
        next(2);
    }

    private void skip() {
        for (;;) {
            if (peek(0) == '/' && peek(1) == '*')
                readMultilineComment();
            else if (peek(0) == '/' && peek(1) == '/')
                readSingleComment();
            else if (peek() == ' ' || peek() == '\t' || peek() == '\n' || peek() == '\r')
                next();
            else
                break;
        }
    }

    private void next(int n) {
        for (int i = 0; i < n; i++)
            next();
    }

    private char next() {
        char result = peek();
        if (result == '\n') {
            line++;
            column = 1;
        } else {
            column++;
        }
        offset++;

        return result;
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