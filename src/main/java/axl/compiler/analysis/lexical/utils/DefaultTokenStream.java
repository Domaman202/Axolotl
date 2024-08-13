package axl.compiler.analysis.lexical.utils;

import axl.compiler.IFile;
import axl.compiler.analysis.lexical.IToken;
import axl.compiler.analysis.lexical.Tokenizer;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;

public class DefaultTokenStream implements TokenStream {

    @Getter
    private final IFile file;

    private final List<IToken> tokens;

    private final Tokenizer tokenizer;

    private int iterator;

    private boolean processed;

    public DefaultTokenStream(@NonNull IFile file, @NonNull List<IToken> tokens, Tokenizer tokenizer) {
        this.file = file;
        this.tokens = tokens;
        this.tokenizer = tokenizer;
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

        tokenize();
        return next();
    }

    @Override
    public IToken get() {
        if (iterator < tokens.size())
            return tokens.get(iterator);

        if (processed)
            return null;

        tokenize();
        return get();
    }

    @Override
    public boolean hasNext() {
        return !this.processed || this.iterator < tokens.size();
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
                end.getTokenId() - 1
        );
        return new DefaultTokenStream(file, tokens, true);
    }

    @Override
    public List<IToken> copy() {
        return new ArrayList<>(this.tokens);
    }

    public void tokenize() {
        this.tokens.add(tokenizer.tokenize());
        processed = tokenizer.isProcessed();
    }
}
