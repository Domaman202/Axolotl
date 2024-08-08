package axl.compiler.lexer.utils;

import axl.compiler.IFile;
import axl.compiler.lexer.IToken;

public interface TokenStream {

    IFile getFile();

    IToken next();

    IToken get();

    boolean hasNext();

    Frame createFrame();

    void restoreFrame(Frame frame);

    TokenStream createSubStream(Frame start, Frame end);

}
