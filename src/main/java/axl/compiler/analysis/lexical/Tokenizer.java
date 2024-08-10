package axl.compiler.analysis.lexical;

import axl.compiler.IFile;

public interface Tokenizer {

    IFile getFile();

    IToken tokenize();

    boolean isProcessed();

}
