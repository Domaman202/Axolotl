package axl.lexer

import axl.File
import axl.lexer.impl.IAxolotlLexer
import axl.utils.mutableListToImmutableList
import axl.utils.mutableSetToImmutableSet

class AxolotlLexer(val file: File) : IAxolotlLexer {

    private var offset: Int = 0

    private var row: Int = 1

    private var column: Int = 1

    private val keywords: Set<AxolotlKeyword> = mutableSetOf()

    private val operators: Set<AxolotlOperator> = mutableSetOf()

    private val tokens: List<AxolotlToken> = mutableListOf()

    fun add(keyword: AxolotlKeyword) {
        if (keyword in keywords)
            throw IllegalArgumentException("The keyword \"${keyword.value}\" is already in the lexer.")

        if (keywords !is MutableSet)
            throw IllegalStateException("You cannot add keywords after confirming the lexer.")

        keywords += keyword
    }

    fun add(operator: AxolotlOperator) {
        if (operator in operators)
            throw IllegalArgumentException("The keyword \"${operator.value}\" is already in the lexer.")

        if (operators !is MutableSet)
            throw IllegalStateException("You cannot add operators after confirming the lexer.")

        operators += operator
    }

    private fun confirm() {
        mutableSetToImmutableSet(keywords)
        mutableSetToImmutableSet(operators)
    }

    // process

    private fun next(): Char {
        if (offset >= file.content.length - 1)
            throw IllegalStateException("The file is over.")

        offset++
        val char = file.content[offset]
        if (char == '\n') {
            row++
            column = 0
        } else {
            column++
        }
        return char
    }

    private fun prev(): Char {
        if (offset <= 0)
            throw IllegalStateException("The file is over.")

        offset--
        val char = file.content[offset]
        if (char == '\n') {
            row--
            column = 0
            var i = offset - 1
            while (i >= 0 && file.content[i] != '\n') {
                column++
                i--
            }
        } else {
            column--
        }
        return char
    }

    private fun get(): Char = if (offset >= file.content.length || offset < 0) throw IllegalStateException("The file is over.") else file.content[offset]

    private fun end(): Boolean = offset >= file.content.length

    private fun skip() {
        next();
        while (!end() && (get() == ' ' || get() == '\n' || get() == '\r' || get() == '\t'))
            next();
    }

    override fun tokenize() {
        if (tokens.isNotEmpty())
            throw IllegalStateException("The lexer has already been launched before.")

        tokens as MutableList

        while (!end()) {
            if (get() == ' ' || get() == '\n' || get() == '\r' || get() == '\t') {
                skip()
                if (end()) {
                    mutableListToImmutableList(tokens)
                    return
                }
            }

            val row: Int = row
            val column: Int = column

            if (get().isDigit() || get() == '.' || get() == '-') {
                val token = number()
                token.setPosition(row, column)
                tokens += token
                continue
            }

            if (get().isLetter() || get() == '_') {
                val token = keywordOrIdentify()
                token.setPosition(row, column)
                tokens += token
                continue
            }

            if (get() == '"') {
                val token = string()
                token.setPosition(row, column)
                tokens += token
                continue
            }

            if (get() == '\'') {
                val token = character()
                token.setPosition(row, column)
                tokens += token
                continue
            }

            if (get() == '/') {
                if (offset + 1 != file.content.length) if (file.content[offset + 1] == '/') {
                    singleComment()
                    next()
                    continue
                } else if (file.content[offset + 1] == '*') {
                    multilineComment()
                    next()
                    continue
                }
            }

            val token = operator()
            token.setPosition(row, column)
            tokens += token
            continue
        }
        mutableListToImmutableList(tokens)
    }
    private fun keywordOrIdentify(): AxolotlToken {
        val builder: StringBuilder = StringBuilder()

        do {
            if (end()) break
            builder.append(next())
        } while (Character.isDigit(get()) || Character.isLetter(get()) || get() == '_' || get() in '0'..'9')

        val value = builder.toString()

        keywords.forEach {
            if (it.value == value)
                return AxolotlTokenKeyword(it)
        }

        return AxolotlTokenIdentify(value)

    }

    private fun number(): AxolotlToken {
        return TODO()
    }

    private fun string(): AxolotlTokenLiteral {
        return TODO()
    }

    private fun character(): AxolotlTokenLiteral {
        return TODO()
    }

    private fun singleComment(): AxolotlTokenComment {
        return TODO()
    }

    private fun multilineComment(): AxolotlTokenComment {
        return TODO()
    }

    private fun operator(): AxolotlTokenOperator {
        return TODO()
    }

    // interface

    private var cursor: Int = 0

    override fun eat(token: AxolotlToken): AxolotlToken {
        var current: AxolotlToken = nextToken() ?: throw IllegalStateException("No more tokens.");
        while (current is AxolotlTokenComment)
            current = nextToken() ?: throw IllegalStateException("No more tokens.");

        if (token is AxolotlTokenIdentify && current is AxolotlTokenIdentify)
            return current

        if (token is AxolotlTokenLiteral && current is AxolotlTokenLiteral) {
            if (token.type != current.type)
                throw AxolotlLexerTokenWillNotEaten("Token \"$token\" will not be eaten.")

            return current
        }

        if (token is AxolotlTokenDelimiter && current is AxolotlTokenDelimiter) {
            if (token.type != current.type)
                throw AxolotlLexerTokenWillNotEaten("Token \"$token\" will not be eaten.")

            return current
        }

        if (current != token)
            throw AxolotlLexerTokenWillNotEaten("Token \"$token\" will not be eaten.")
        return current
    }

    override fun nextToken(): AxolotlToken? = tokens.getOrNull(cursor++)

    override fun peekToken(): AxolotlToken? = tokens.getOrNull(cursor)

    override fun hasMoreTokens(): Boolean = cursor < tokens.size

    override fun saveFrame(): AxolotlLexerFrame = AxolotlLexerFrame(cursor)

    override fun restoreFrame(frame: AxolotlLexerFrame) {
        this.cursor = frame.cursor
    }

    override fun copy(file: File): IAxolotlLexer {
        val clone = AxolotlLexer(file)
        keywords.forEach { clone.add(it) }
        operators.forEach { clone.add(it) }
        return clone
    }


}

data class AxolotlLexerFrame(val cursor: Int)

class AxolotlLexerTokenWillNotEaten(override val message: String?) : Exception(message)