package axl.lexer.impl

import axl.File
import axl.lexer.AxolotlKeyword
import axl.lexer.AxolotlLexerTokenWillNotEaten
import axl.lexer.AxolotlOperator
import axl.lexer.token.AxolotlToken

interface IAxolotlLexer {

    /**
     * Creates a new lexer with the same settings.
     * @param file the file to be tokenized.
     * @return a copy of the lexer with a new file.
     */
    fun copy(file: File) : IAxolotlLexer

    /**
    * Consumes the next token if it matches the expected type.
    * @param token the expected type of the token.
    * @return the consumed token if it matches the expected type, or null if it does not match.
    * @throws IllegalStateException if there are no more tokens.
    * @throws AxolotlLexerTokenWillNotEaten if token will not be eaten.
    */
    fun eat(token: AxolotlToken): AxolotlToken

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
     * Saves the current state of the lexer to a frame.
     * @return a frame representing the current state of the lexer.
     */
    fun saveFrame(): AxolotlLexerFrame

    /**
     * Restores the lexer's state from a given frame.
     * @param frame the frame to restore the state from.
     */
    fun restoreFrame(frame: AxolotlLexerFrame)

    /**
     * @return file from the lexer.
     */
    fun getFile(): File

    /**
     * @param size tab size in whitespaces. The default value is 4.
     */
    fun setTabSize(size: Int)

    /**
     * TODO docs, throws
     */
    fun add(keyword: AxolotlKeyword)

    /**
     * TODO docs, throws
     */
    fun add(operator: AxolotlOperator)

    data class AxolotlLexerFrame(val lastToken: AxolotlToken?, val offset: Int, val row: Int, val column: Int)

}