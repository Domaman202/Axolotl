package axl.lexer

import axl.File
import axl.getLine
import axl.lexer.impl.IAxolotlLexer

class AxolotlLexer(private val file: File) : IAxolotlLexer {
    private var offset: Int = 0
    private var row: Int = 1
    private var column: Int = 1
    private val keywords: MutableSet<AxolotlKeyword> = mutableSetOf()
    private val operators: MutableSet<AxolotlOperator> = mutableSetOf()
    private val tokens: MutableList<AxolotlToken> = mutableListOf()
    private var lastToken: AxolotlToken? = null

    fun add(keyword: AxolotlKeyword) {
        if (keyword in keywords)
            throw IllegalArgumentException("The keyword \"${keyword.value}\" is already in the lexer.")
        if (confirm)
            throw IllegalStateException("You cannot add keywords after confirming the lexer.")
        keywords += keyword
    }

    fun add(operator: AxolotlOperator) {
        if (operator in operators)
            throw IllegalArgumentException("The keyword \"${operator.value}\" is already in the lexer.")
        if (confirm)
            throw IllegalStateException("You cannot add operators after confirming the lexer.")
        operators += operator
    }

    private var confirm = false

    private fun confirm() {
        confirm = true
    }

    // process

    private fun next(): Char {
        if (offset >= file.content.length)
            throw IllegalStateException("The file is over.")

        val char = file.content[offset++]
        if (char == '\n') {
            row++
            column = 1
        } else column++
        return char
    }

    private fun prev(): Char {
        if (offset <= 0)
            throw IllegalStateException("The file is over.")

        offset--
        val char = file.content[offset]
        if (char == '\n') {
            row--
            column = 1
            var i = offset - 1
            while (i >= 0 && file.content[i] != '\n') {
                column++
                i--
            }
        } else column--
        return char
    }

    private fun get(): Char {
        if (offset >= file.content.length) {
            if (offset == file.content.length)
                return file.content[offset-1]
            throw IllegalStateException("The file is over.")
        }

        return file.content[offset]
    }

    private fun end(): Boolean =
        offset >= file.content.length

    private fun skip() {
        next()
        while (!end() && (get() == ' ' || get() == '\n' || get() == '\r' || get() == '\t')) {
            next()
        }
    }

    override fun tokenize() {
        if (tokens.isNotEmpty())
            throw IllegalStateException("The lexer has already been launched before.")
        confirm()

        while (!end()) {
            if (get() == ' ' || get() == '\n' || get() == '\r' || get() == '\t') {
                skip()
                if (end()) {
                    return
                }
            }

            val row: Int = row
            val column: Int = column
            val token: AxolotlToken =
                if (get().isDigit() || get() == '.' || get() == '-')
                    number()
                else if (get().isLetter() || get() == '_')
                    keywordOrIdentify()
                else if (get() == '"')
                    string()
                else if (get() == '\'')
                    character()
                else if (get() == '/' && offset + 1 != file.content.length)
                    if (file.content[offset + 1] == '/')
                        singleComment()
                    else if (file.content[offset + 1] == '*')
                        multilineComment()
                    else operator()
                else operator()

            token.setPosition(row, column)
            lastToken = token
            tokens += token
            continue
        }
    }

    private fun keywordOrIdentify(): AxolotlToken {
        val builder: StringBuilder = StringBuilder()

        do {
            if (end())
                break
            if (offset > 3) {
                prev()
                prev()
                prev()
                next()
                next()
                next()
            }
            builder.append(next())
        } while (Character.isDigit(get()) || Character.isLetter(get()) || get() == '_' || get() in '0'..'9')

        val value = builder.toString()

        keywords.forEach {
            if (it.value == value) {
                return AxolotlTokenKeyword(it)
            }
        }

        return AxolotlTokenIdentify(value)
    }

    private fun number(): AxolotlToken {
        var isFloat = false;

        val value = StringBuilder()

        if (get() == '.') {
            isFloat = true;
            value.append(next())
        }

        if (lastToken != null && get() == '-') {
            when (lastToken) {
                is AxolotlTokenLiteral,
                is AxolotlTokenIdentify -> return operator()
                is AxolotlTokenDelimiter -> {
                    when ((lastToken as AxolotlTokenDelimiter).type) {
                        AxolotlTokenDelimiterType.RIGHT_PARENT,
                        AxolotlTokenDelimiterType.RIGHT_BRACE,
                        AxolotlTokenDelimiterType.RIGHT_SQUARE -> return operator()
                        else -> Unit
                    }
                }
            }
            value.append(next())
        }

        do {
            if(end())
                break
            if(get() == '.') {
                if (isFloat)
                    throw IllegalStateException("2 точки в 1 числе") // TODO
                else isFloat = true
            }

            value.append(next());
        } while ((get() in '0'..'9') || get() == '.')

        var containsDigit = false
        for (char in value) {
            if (char.isDigit()) {
                containsDigit = true
                break
            }
        }

        if(!containsDigit) {
            for (char in value)
                prev()
            println(column)
            return operator()
        }

        val postfix = get().lowercaseChar()
        if (postfix == 'l' && isFloat)
            throw IllegalStateException("Нельзя же так..")
        if (postfix == 'l' || postfix == 'd' || postfix == 'f')
            value.append(next())
        return AxolotlTokenLiteral(value.toString(), if (isFloat) AxolotlTokenLiteralType.FLOAT else AxolotlTokenLiteralType.INTEGER)
    }

    private fun string(): AxolotlTokenLiteral {
        TODO()
    }

    private fun character(): AxolotlTokenLiteral {
        TODO()
    }

    private fun singleComment(): AxolotlTokenComment {
        TODO()
//        next()
    }

    private fun multilineComment(): AxolotlTokenComment {
        TODO()
//        next()
    }

    private fun operator(): AxolotlTokenOperator {
        TODO()
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

    override fun nextToken(): AxolotlToken? =
        tokens.getOrNull(cursor++)

    override fun peekToken(): AxolotlToken? =
        tokens.getOrNull(cursor)

    override fun hasMoreTokens(): Boolean =
        cursor < tokens.size

    override fun saveFrame(): AxolotlLexerFrame =
        AxolotlLexerFrame(cursor)

    override fun restoreFrame(frame: AxolotlLexerFrame) {
        this.cursor = frame.cursor
    }

    override fun copy(file: File): IAxolotlLexer {
        val clone = AxolotlLexer(file)
        keywords.forEach { clone.add(it) }
        operators.forEach { clone.add(it) }
        return clone
    }

    fun errorMessage(position: AxolotlTokenPosition, errorMessage: String): String {
        val whitespaces = " ".repeat(row.toString().length + 1)
        return """
            $whitespaces[ERROR]: $errorMessage
            
            $whitespaces╭ ⎯⎯⎯ ${file.filename}:${position.row}:${position.column} ⎯⎯⎯⎯⎯
            ${position.row} │ ${file.getLine(position.row - 1)}
            $whitespaces│${" ".repeat(position.column)}╰ $errorMessage
        """.trimIndent()
    }
}