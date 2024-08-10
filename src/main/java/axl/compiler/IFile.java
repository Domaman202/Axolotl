package axl.compiler;

import axl.compiler.utils.Serializable;
import axl.compiler.analysis.lexical.utils.TokenStream;

public interface IFile extends Serializable {

    String getName();

    String getContent();

    TokenStream createTokenStream();

}

