package axl.lexer

data class Position(val row: Int, val column: Int)

abstract class AxolotlToken (open val content: Any) {

    private var position: Position? = null

    private var length: Int = 0

    fun setPosition(row: Int, column: Int) {
        this.position = Position(row, column)
    }

    fun setLength(length: Int) {
        this.length = length
    }

    fun getPosition() : Position? = this.position

    fun getLength() = this.length

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AxolotlToken

        return content == other.content
    }

    override fun toString(): String {
        return " ${position!!.row}:${position!!.column}:$length]"
    }

    override fun hashCode(): Int {
        var result = content.hashCode()
        result = 31 * result + (position?.hashCode() ?: 0)
        result = 31 * result + length
        return result
    }

    fun isRightDelimiter(): Boolean {
        if (this !is AxolotlTokenDelimiter) return false

        return this.type == AxolotlTokenDelimiterType.RIGHT_SQUARE ||
                this.type == AxolotlTokenDelimiterType.RIGHT_BRACE ||
                this.type == AxolotlTokenDelimiterType.RIGHT_PARENT
    }

}

class AxolotlTokenKeyword(override val content: AxolotlKeyword) : AxolotlToken(content) {

    override fun toString(): String {
        return "[KEYWORD \"${content.value}\"" + super.toString()
    }

}

class AxolotlTokenOperator(override val content: AxolotlOperator) : AxolotlToken(content) {

    override fun toString(): String {
        return "[OPERATOR \"${content.value}\"" + super.toString()
    }

}

class AxolotlTokenIdentify(override val content: String) : AxolotlToken(content) {

    override fun toString(): String {
        return "[IDENTIFY \"$content\"" + super.toString()
    }

}

enum class AxolotlTokenLiteralType {
    STRING,
    CHARACTER,
    INTEGER,
    FLOAT
}

class AxolotlTokenLiteral(override val content: String, val type: AxolotlTokenLiteralType) : AxolotlToken(content) {

    constructor(content: Char) : this(content.toString(), AxolotlTokenLiteralType.CHARACTER)

    constructor(content: String) : this(content, AxolotlTokenLiteralType.STRING)

    override fun toString(): String {
        if ('\n' in content)
            return "[LITERAL \"${type.name}\" {\n$content\n}" + super.toString()
        return "[LITERAL \"${type.name}\" \"${content}\"" + super.toString()
    }

}

class AxolotlTokenComment(override val content: String) : AxolotlToken(content) {

    override fun toString(): String {
        if ('\n' in content)
            return "[COMMENT {\n$content\n}" + super.toString()

        return "[COMMENT \"$content\"" + super.toString()
    }

}

enum class AxolotlTokenDelimiterType {
    LEFT_PARENT, RIGHT_PARENT,
    LEFT_BRACE, RIGHT_BRACE,
    LEFT_SQUARE, RIGHT_SQUARE,
    COMMA,
    SEMI
}

class AxolotlTokenDelimiter(override val content: String, val type: AxolotlTokenDelimiterType) : AxolotlToken(content) {

    override fun toString(): String {
        return "[DELIMITER \"${type.name}\" \"${content}\"" + super.toString()
    }

}