package axl.lexer.impl

import axl.lexer.AxolotlLexerFrame
import axl.lexer.AxolotlToken

interface IAxolotlLexer {

    /**
     * Reads the next token from the input stream.
     * @return the next token, or null if the end of the input is reached.
     */
    fun nextToken(): AxolotlToken?

    /**
     * Peeks at the next token without consuming it.
     * @return the next token, or null if the end of the input is reached.
     */
    fun peekToken(): AxolotlToken?

    /**
     * Resets the lexer to its initial state.
     */
    fun reset()

    /**
     * Checks if the lexer has more tokens to read.
     * @return true if there are more tokens, false otherwise.
     */
    fun hasMoreTokens(): Boolean

    /**
     * Saves the current state of the lexer to a frame.
     * @return a frame representing the current state of the lexer.
     */
    fun saveFrame(): AxolotlLexerFrame

    /**
     * Restores the lexer's state from a given frame.
     * @param frame the frame to restore the state from.
     */
    fun restoreFrame(frame: AxolotlLexerFrame)

}