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

    //so far number tokenization only scans int and float
    private IToken readNumber() {
        char current = peek();
        boolean isFloat = false;
        while (true) {
            if (current == '.') { // FIXME TokenType.DOT
                if (isFloat)
                    throw new RuntimeException("Invalid float number ");
                isFloat = true;
            } else if (!Character.isDigit(current)) {
                break;
            }
            current = next();
        }
        if (isFloat) {
            return new DefaultToken(TokenType.FLOAT_LITERAL);
        } else {
            return new DefaultToken(TokenType.INTEGER_LITERAL);
        }
    }

    private char next(int n) {
        for (int i = 1; i < n; i++)
            next();

        return next();
    }

    private char next() {
        char current = peek();
        offset++;
        if (current == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }
        return current;
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