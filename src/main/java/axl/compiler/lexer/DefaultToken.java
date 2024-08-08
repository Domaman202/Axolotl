package axl.compiler.lexer;

import axl.compiler.lexer.utils.Frame;
import lombok.Getter;

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
    public TokenType getType() {
        return null;
    }

}
