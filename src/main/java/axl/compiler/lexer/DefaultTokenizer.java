package axl.compiler.lexer;

import axl.compiler.IFile;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefaultTokenizer implements Tokenizer {

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


    private IToken readIdOrKeyword() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append(peek(0));
        char current = next();
        while (LexerHelper.isIdentifierPart(current)) {
            buffer.append(current);
            current = next();
        }
        final String word = buffer.toString();
        if (TokenType.keywords().contains(word)) {
            //return keyword token
        }
        return new DefaultToken(TokenType.IDENTIFY);


    }

    //so far number tokenization only scans int and float
    private IToken readNumber() {
        char current = peek(0);
        boolean isFloat = false;
        boolean hasDot = false;
        while (true) {
            if (current == '.') {
                isFloat = true;
                if (hasDot)
                    throw new RuntimeException("Invalid float number ");
                hasDot = true;
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
        char current = get();
        offset++;
        if (current == '\n') {
            line++;
            column = 0;
        } else {
            column++;
        }
        return current;
    }

    private char get() {
        if (end())
            return '\0';

        return file.getContent().charAt(offset);
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

    @Override
    public boolean isProcessed() {
        return end();
    }

}

record DefaultTokenizerFrame(int offset, int line, int column) {

}