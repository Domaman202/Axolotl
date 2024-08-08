package axl.compiler.lexer;

import axl.compiler.IFile;
import lombok.NonNull;

public class Config {

    public static Tokenizer createTokenizer(@NonNull IFile file, IToken last) {
        return new DefaultTokenizer(file, last);
    }

}
