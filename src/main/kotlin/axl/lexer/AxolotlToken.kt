package axl.lexer

data class Position(val row: Int, val column: Int)

abstract class AxolotlToken (open val content: Any) {

    private var position: Position? = null

    private var length: Int = 0

    fun setPosition(row: Int, column: Int) {
        this.position = Position(row, column)
    }

    internal fun setLength(length: Int) {
        this.length = length
    }

    fun getPosition() : Position? = this.position

    fun getLength(length: Int) = this.length

    override fun hashCode(): Int {
        throw IllegalStateException("")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AxolotlToken

        return content == other.content
    }

    override fun toString(): String {
        return " ${position!!.row}:${position!!.column}:$length]"
    }

}

class AxolotlTokenKeyword(override val content: AxolotlKeyword) : AxolotlToken(content) {

    init {
        setLength(content.value.length)
    }

    override fun toString(): String {
        return "[KEYWORD \"${content.value}\"" + super.toString()
    }

}

class AxolotlTokenOperator(override val content: AxolotlOperator) : AxolotlToken(content) {

    init {
        setLength(content.value.length)
    }

    override fun toString(): String {
        return "[OPERATOR \"${content.value}\"" + super.toString()
    }

}

class AxolotlTokenIdentify(override val content: String) : AxolotlToken(content) {

    init {
        setLength(content.length)
    }

    override fun toString(): String {
        return "[IDENTIFY \"$content\"" + super.toString()
    }

}

enum class AxolotlTokenLiteralType {
    STRING,
    CHARACTER,
    INTEGER,
    FLOAT,
    BOOLEAN
}

class AxolotlTokenLiteral(override val content: String, val type: AxolotlTokenLiteralType) : AxolotlToken(content) {

    init {
        if (type == AxolotlTokenLiteralType.STRING || type == AxolotlTokenLiteralType.CHARACTER)
            setLength(content.length + 2)
        else
            setLength(content.length)
    }

    override fun toString(): String {
        return "[LITERAL \"${type.name}\" \"${content}\"" + super.toString()
    }

}

class AxolotlTokenComment(override val content: String) : AxolotlToken(content) {

    init {
        if ('\n' in content)
            setLength(content.length + 4)
        else
            setLength(content.length + 2)
    }

    override fun toString(): String {
        return "[COMMENT {\n${content}\n}" + super.toString()
    }

}

enum class AxolotlTokenDelimiterType {
    LEFT_PARENT, RIGHT_PARENT,
    LEFT_BRACE, RIGHT_BRACE,
    LEFT_SQUARE, RIGHT_SQUARE,
    COMMA,
    SEMI,
    LOW_SEMI
}

class AxolotlTokenDelimiter(override val content: String, val type: AxolotlTokenDelimiterType) : AxolotlToken(content) {

    init {
        setLength(content.length)
    }

    override fun toString(): String {
        return "[DELIMITER \"${type.name}\" \"${content}\"" + super.toString()
    }

}