package axl.compiler.analysis.lexical;

import axl.compiler.IFile;

public interface IToken {

    int getOffset();

    int getLength();

    int getLine();

    int getColumn();

    TokenType getType();

    String getContent(IFile file);

}
