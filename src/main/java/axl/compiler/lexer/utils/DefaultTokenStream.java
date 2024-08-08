package axl.compiler.lexer.utils;

import axl.compiler.IFile;
import axl.compiler.lexer.Config;
import axl.compiler.lexer.IToken;
import axl.compiler.lexer.Tokenizer;
import lombok.Getter;
import lombok.NonNull;

import java.util.List;

public class DefaultTokenStream implements TokenStream {

    @Getter
    private final IFile file;

    private final List<IToken> tokens;

    private final Tokenizer tokenizer;

    private int iterator;

    private boolean processed;

    public DefaultTokenStream(@NonNull IFile file, @NonNull List<IToken> tokens) {
        this.file = file;
        this.tokens = tokens;
        this.tokenizer = Config.createTokenizer(
                file,
                tokens.isEmpty() ? null : tokens.getLast()
        );
    }

    private DefaultTokenStream(@NonNull IFile file, @NonNull List<IToken> tokens, boolean processed) {
        this.file = file;
        this.tokens = tokens;
        this.processed = processed;
        this.tokenizer = null;
    }

    @Override
    public IToken next() {
        if (iterator < tokens.size())
            return tokens.get(iterator++);

        if (processed)
            return null;

        tokens.add(this.tokenize());
        return next();
    }

    @Override
    public IToken get() {
        if (iterator < tokens.size())
            return tokens.get(iterator);

        if (processed)
            return null;

        tokens.add(this.tokenize());
        return get();
    }

    @Override
    public boolean hasNext() {
        return this.iterator + 1 < this.tokens.size() || this.processed;
    }

    @NonNull
    @Override
    public Frame createFrame() {
        return new DefaultFrame(iterator);
    }

    @Override
    public void restoreFrame(@NonNull Frame frame) {
        this.iterator = frame.getTokenId();
    }

    @Override
    public TokenStream createSubStream(@NonNull Frame start, @NonNull Frame end) {
        List<IToken> tokens = this.tokens.subList(
                start.getTokenId(),
                end.getTokenId()
        );
        return new DefaultTokenStream(file, tokens, true);
    }

    @NonNull
    private IToken tokenize() {
        IToken token = tokenizer.tokenize();

        processed = tokenizer.isProcessed();
        return token;
    }

}
