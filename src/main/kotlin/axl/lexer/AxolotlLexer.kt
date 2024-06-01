package axl.lexer

import axl.File
import axl.lexer.impl.IAxolotlLexer
import axl.lexer.impl.IAxolotlLexer.AxolotlLexerFrame

class AxolotlLexer(private val file: File) : IAxolotlLexer {
    private var tabSize = 4
    private var offset: Int = 0
    private var row: Int = 1
    private var column: Int = 1
    private val keywords: MutableSet<AxolotlKeyword> = mutableSetOf()
    private val operators: MutableSet<AxolotlOperator> = mutableSetOf()
    private var maxOperatorLength = 0
    private val tokens: MutableList<AxolotlToken> = mutableListOf()
    private var lastToken: AxolotlToken? = null
    private var frame: AxolotlLexerFrame? = null

    override fun add(keyword: AxolotlKeyword) {
        if (keyword in keywords)
            throw IllegalArgumentException("The keyword \"${keyword.value}\" is already in the lexer.")
        if (confirm)
            throw IllegalStateException("You cannot add keywords after confirming the lexer.")
        keywords += keyword
    }

    override fun add(operator: AxolotlOperator) {
        if (operator in operators)
            throw IllegalArgumentException("The keyword \"${operator.value}\" is already in the lexer.")
        if (confirm)
            throw IllegalStateException("You cannot add operators after confirming the lexer.")
        maxOperatorLength = maxOf(maxOperatorLength, operator.value.length)
        operators += operator
    }

    private var confirm = false

    private fun confirm() {
        confirm = true
    }

    // process

    private fun next(n: Int): Char {
        for (i in 1..<n)
            next()
        return next()
    }

    private fun next(): Char {
        if (offset >= file.content.length) {
            throw IllegalStateException("The file is over.")
        }

        val char = file.content[offset++]
        if (char == '\n') {
            row++
            column = 1
        } else column++
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
        while (!end() && get().let { it == ' ' || it == '\n' || it == '\r' || it == '\t' }) {
            next()
        }
    }

    private fun keywordOrIdentify(): AxolotlToken {
        val builder: StringBuilder = StringBuilder()

        do {
            if (end())
                break
            builder.append(next())
        } while (Character.isDigit(get()) || Character.isLetter(get()) || get() == '_' || get() in '0'..'9')

        val value = builder.toString()
        keywords.find { it.value == value }?.let { return AxolotlTokenKeyword(it) }
        return AxolotlTokenIdentify(value)
    }

    private fun number(): AxolotlToken {
        val value = StringBuilder()
        val rootFrame = saveFrame()
        var isNegative = false
        val lastToken = this.lastToken
        if (lastToken != null && get() == '-') {
            when (lastToken) {
                is AxolotlTokenLiteral,
                is AxolotlTokenIdentify -> return operator()
                is AxolotlTokenDelimiter -> {
                    when (lastToken.type) {
                        AxolotlTokenDelimiterType.RIGHT_PARENT,
                        AxolotlTokenDelimiterType.RIGHT_BRACE,
                        AxolotlTokenDelimiterType.RIGHT_SQUARE -> return operator()
                        else -> Unit
                    }
                }
            }
            isNegative = true
            next()
        }

        var ignored = 0;
        val confirmLiterals: MutableMap<AxolotlTokenLiteral, AxolotlLexerFrame> = mutableMapOf()

        while (!end()) {
            value.append(next())

            if (value.toString().matches(REGEX_INTEGER)) {
                val frame = saveFrame()
                confirmLiterals[AxolotlTokenLiteral(value.toString(), AxolotlTokenLiteralType.INTEGER)] = frame
                ignored = 0
            } else if (value.toString().matches(REGEX_FLOAT)) {
                val frame = saveFrame()
                confirmLiterals[AxolotlTokenLiteral(value.toString(), AxolotlTokenLiteralType.FLOAT)] = frame
                ignored = 0
            } else {
                if (ignored++ == 5) {
                    break
                }
            }
        }

        if (confirmLiterals.isEmpty()) {
            restoreFrame(rootFrame)
            return operator()
        }

        val literal = confirmLiterals.maxWith(compareBy { it.key.content.length })
        restoreFrame(literal.value)
        return if (isNegative)
            AxolotlTokenLiteral("-" + literal.key.content, literal.key.type)
        else literal.key
    }

    private fun processCommentOrOperator() : AxolotlToken =
        when (file.content[offset + 1]) {
            '/' -> singleComment()
            '*' -> multilineComment()
            else -> operator()
        }

    private fun processDelimiterOrOperator() : AxolotlToken =
        when (get()) {
            '(' -> AxolotlTokenDelimiter(next().toString(), AxolotlTokenDelimiterType.LEFT_PARENT)
            ')' -> AxolotlTokenDelimiter(next().toString(), AxolotlTokenDelimiterType.RIGHT_PARENT)
            '[' -> AxolotlTokenDelimiter(next().toString(), AxolotlTokenDelimiterType.LEFT_SQUARE)
            ']' -> AxolotlTokenDelimiter(next().toString(), AxolotlTokenDelimiterType.RIGHT_SQUARE)
            '{' -> AxolotlTokenDelimiter(next().toString(), AxolotlTokenDelimiterType.LEFT_BRACE)
            '}' -> AxolotlTokenDelimiter(next().toString(), AxolotlTokenDelimiterType.RIGHT_BRACE)
            ',' -> AxolotlTokenDelimiter(next().toString(), AxolotlTokenDelimiterType.COMMA)
            ';' -> AxolotlTokenDelimiter(next().toString(), AxolotlTokenDelimiterType.SEMI)
            else -> operator()
        }

    private fun processString(): AxolotlTokenLiteral {
        return if (offset + 1 < file.content.length && file.content[offset + 1] == '"') {
            if (offset + 2 < file.content.length && file.content[offset + 2] == '"') {
                multilineString()
            } else {
                next(2)
                AxolotlTokenLiteral("")
            }
        } else {
            string()
        }
    }

    private fun string(): AxolotlTokenLiteral {
        val value = StringBuilder()

        next();
        while (true) {
            if (end() || get() == '\n') {
                throw AxolotlLexerTokenizeException(error("The string is not closed."))
            }

            when (get()) {
                '\\' -> {
                    value.append(character(false).content)
                    continue;
                }

                '"' -> {
                    next()
                    break;
                }

                else -> value.append(next());
            }
        }

        return AxolotlTokenLiteral(value.toString());
    }

    private fun multilineString(): AxolotlTokenLiteral {
        next(3)

        val lines = mutableListOf<StringBuilder>()
        var minLineOffset = Int.MAX_VALUE
        if (get() != '\n' && get() != ' ' && get() != '\t') {
            lines += StringBuilder()
            minLineOffset = 0
        } else {
            lines += StringBuilder()
        }

        var whitespaces = 0
        var isLineBegin = true
        while (true) {
            if (end())
                throw AxolotlLexerTokenizeException(error("The string is not closed."))
            val it = get()
            when {
                it == '"' -> {
                    if (offset + 1 != file.content.length && file.content[offset + 1] == '"') {
                        if (offset + 2 != file.content.length && file.content[offset + 2] == '"') {
                            if (isLineBegin)
                                lines.removeLast()
                            next(3)
                            break
                        }
                    }
                }

                isLineBegin -> when (it) {
                    ' ' -> whitespaces++
                    '\t' -> whitespaces += tabSize
                }

                it == '\n' -> {
                    if (!isLineBegin)
                        minLineOffset = minOf(minLineOffset, whitespaces)
                    whitespaces = 0
                    isLineBegin = true
                    lines += StringBuilder()
                    next()
                    continue
                }

                else -> isLineBegin = false
            }

            lines.last().append(next());
        }

        isLineBegin = true
        for (char in lines.first()) {
            if (char != '\t' && char != ' ') {
                isLineBegin = false
                break
            }
        }

        if (isLineBegin) {
            lines.removeFirst()
        }

        return AxolotlTokenLiteral(lines.joinToString(separator = "\n") { it ->
            it.toString().replace("\t", " ".repeat(tabSize)).let {
                if (it.length <= minLineOffset)
                    it
                else it.substring(minLineOffset)
            }
        })
    }

    private fun character(needNext: Boolean): AxolotlTokenLiteral {
        fun eatUnicode(): Int {
            when(get()) {
                in '0'..'9',
                in 'a'..'f',
                in 'A'..'F' -> {
                    if (!end()) {
                        return next().digitToInt(16)
                    }
                }
            }
            throw AxolotlLexerTokenizeException(error("Invalid character literal."))
        }

        if (needNext) {
            next()
        }

        if (end() || get() == '\n')
            throw AxolotlLexerTokenizeException(error("Invalid character literal."))
        if (get() == '\'') {
            throw AxolotlLexerTokenizeException(error("Invalid character literal."))
        }

        val value: Char = if (get() == '\\')
            when (next(2)) {
                '\\' -> '\\'
                '\'' -> if (!needNext) throw AxolotlLexerTokenizeException(error("Invalid string literal.")) else '\''
                '\"' -> if (needNext) throw AxolotlLexerTokenizeException(error("Invalid character literal.")) else '"'
                'n' -> '\n'
                't' -> '\t'
                'b' -> '\b'
                'r' -> '\r'
                'u' -> {
                    val c1 = eatUnicode()
                    val c2 = eatUnicode()
                    val c3 = eatUnicode()
                    val c4 = eatUnicode()
                    ((c1 * 16 * 16 * 16) + (c2 * 16 * 16) + (c3 * 16) + c4).toChar()
                }
                else -> throw AxolotlLexerTokenizeException(error("Invalid character literal."))
            }
        else next()

        if (end() || (needNext && next() != '\''))
            throw AxolotlLexerTokenizeException(error("Invalid character literal."))
        return AxolotlTokenLiteral(value);
    }

    private fun singleComment(): AxolotlTokenComment {
        val value = StringBuilder()
        while (!end() && !(get() == '\n' || get() == '\r'))
            value.append(next())
        if (!end())
            next()
        return AxolotlTokenComment(value.toString())
    }

    private fun multilineComment(): AxolotlTokenComment {
        val value = StringBuilder()
        next()
        while (!end())
        {
            next()
            if (get() == '*') {
                if (end())
                    break
                next()
                if (get() =='/') {
                    break
                }
            }
            value.append(get())
        }

        if (end())
            throw AxolotlLexerTokenizeException(error("The comment is not closed."))
        next()
        return AxolotlTokenComment(value.toString())
    }

    private fun operator(): AxolotlTokenOperator {
        val confirmOperators: MutableMap<AxolotlOperator, AxolotlLexerFrame> = mutableMapOf()
        val value = StringBuilder()

        while (!end() && get().toString().matches(AxolotlOperator.REGEX)) {
            value.append(next())
            operators.forEach {
                if (it.value == value.toString()) {
                    confirmOperators[it] = saveFrame()
                }
            }
        }

        if (value.isEmpty())
            throw AxolotlLexerTokenizeException(error("Unknown character."))
        if (confirmOperators.isEmpty()) {
            throw AxolotlLexerTokenizeException(error("Unknown character."))
        }

        val operator = confirmOperators.maxWith(compareBy { it.key.value.length })
        restoreFrame(operator.value)
        return AxolotlTokenOperator(operator.key)
    }

    override fun eat(token: AxolotlToken): AxolotlToken {
        var current: AxolotlToken = nextToken() ?: throw IllegalStateException("No more tokens.")
        while (current is AxolotlTokenComment) {
            current = nextToken() ?: throw IllegalStateException("No more tokens.")
        }

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

    override fun nextToken(): AxolotlToken? {
        if (get() == ' ' || get() == '\n' || get() == '\r' || get() == '\t')
            skip()
        if (end()) {
            return null
        }

        this.frame = saveFrame()
        return when (val it = get()) {
            '.', '-' -> number()
            '_' -> keywordOrIdentify()
            '"' -> processString()
            '\'' -> character(true)
            else -> {
                when {
                    it == '/' && offset + 1 != file.content.length -> processCommentOrOperator()
                    it.isDigit() -> number()
                    it.isLetter() -> keywordOrIdentify()
                    else -> processDelimiterOrOperator()
                }
            }
        }.apply {
            position = AxolotlTokenPosition(frame!!.row, frame!!.column)
            length = offset - frame!!.offset
            lastToken = this
        }
    }

    override fun peekToken(): AxolotlToken? =
        lastToken

    override fun saveFrame(): AxolotlLexerFrame =
        AxolotlLexerFrame(lastToken, offset, row, column)

    override fun restoreFrame(frame: AxolotlLexerFrame) {
        this.lastToken = frame.lastToken
        this.offset = frame.offset
        this.row = frame.row
        this.column = frame.column
    }

    override fun getFile(): File =
        this.file

    override fun setTabSize(size: Int) {
        if (confirm)
            throw IllegalStateException("You cannot set tab size after confirming the lexer.")
        this.tabSize = size
    }

    override fun copy(file: File): IAxolotlLexer {
        val clone = AxolotlLexer(file)
        keywords.forEach { clone.add(it) }
        operators.forEach { clone.add(it) }
        return clone
    }

    private fun error(message: String) : String{
        val whitespaces = " ".repeat(row.toString().length + 1)

        return if (frame == null)
            " [ERROR] $message"
        else """
            $whitespaces[ERROR]: $message
        
            $whitespaces╭ ⎯⎯⎯ ${file.filename}:${frame!!.row}:${frame!!.column} ⎯⎯⎯⎯⎯
            ${frame!!.row} │ ${file.getLine(frame!!.row - 1)}
            $whitespaces│${" ".repeat(frame!!.column)}╰ $message
        """.trimIndent()
    }

    companion object {
        val REGEX_INTEGER = Regex("(0|([1-9][0-9_]*))([lL])?|(0[0-7][1-7_]+)([lL])?|0[Xx][0-9a-fA-F_]*([lL])?|0[Bb][0-1_]*([lL])?")
        val REGEX_FLOAT   = Regex("([0-9]+)\\.([0-9]+)([Ee][+\\-]?[0-9_]*)?([fFdD])?|\\.[0-9]+([Ee][+\\-]?[0-9_]*)?([fFdD])?|([0-9]+)([Ee][+\\-]?[0-9_]*)?([fFdD])?|([0-9]+)([fFdD])")
    }
}
