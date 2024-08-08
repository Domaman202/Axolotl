package axl.compiler;

import axl.compiler.utils.Serializable;
import axl.compiler.lexer.utils.TokenStream;

public interface IFile extends Serializable {

    String getName();

    String getContent();

    TokenStream createTokenStream();

}

