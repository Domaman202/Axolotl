package axl.compiler.lexer;

import axl.compiler.IFile;

public interface Tokenizer {

    IFile getFile();

    IToken tokenize();

    boolean isProcessed();

}
