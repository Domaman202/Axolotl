package axl.compiler.analysis.lexical.utils;

import axl.compiler.IFile;
import axl.compiler.analysis.lexical.IToken;

import java.util.List;

public interface TokenStream {

    IFile getFile();

    IToken next();

    IToken get();

    boolean hasNext();

    Frame createFrame();

    void restoreFrame(Frame frame);

    TokenStream createSubStream(Frame start, Frame end);

    List<IToken> copy();

}
