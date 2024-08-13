package axl.compiler.analysis.lexical;

import axl.compiler.IFile;
import lombok.Getter;
import sun.misc.Unsafe;

import java.lang.reflect.Field;

@Getter
public class DefaultToken implements IToken {

    private final TokenType type;
    int offset;
    int length;
    int line;
    int column;

    DefaultToken(TokenType type) {
        this.type = type;
    }

    @Override
    public String getContent(IFile file) {
        return file
                .getContent()
                .substring(
                        this.offset,
                        this.offset + this.length
                );
    }
}
