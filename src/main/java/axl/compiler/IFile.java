package axl.compiler;

import axl.compiler.analysis.lexical.utils.TokenStream;

public interface IFile {

    String getName();

    String getContent();

    TokenStream createTokenStream();

}

