package axl.compiler.analysis.lexical.utils;

import axl.compiler.analysis.lexical.IToken;
import axl.compiler.analysis.lexical.TokenType;

public class TokenStreamUtils {
    public static TokenType nextTokenType(TokenStream tokenStream) {
        IToken next = tokenStream.next();
        if (next == null)
            return null;
        return next.getType();
    }

    public static boolean nextTokenTypeIs(TokenStream tokenStream, TokenType compare) {
        return nextTokenType(tokenStream) == compare;
    }

    public static boolean nextTokenTypeNot(TokenStream tokenStream, TokenType compare) {
        return nextTokenType(tokenStream) != compare;
    }
}
