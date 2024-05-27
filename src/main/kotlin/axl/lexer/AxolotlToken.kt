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

}

class AxolotlTokenKeyword(override val content: AxolotlKeyword) : AxolotlToken(content) {

    init {
        setLength(content.value.length)
    }

}

class AxolotlTokenOperator(override val content: AxolotlOperator) : AxolotlToken(content)

class AxolotlTokenIdentify(override val content: String) : AxolotlToken(content)

enum class AxolotlTokenLiteralType {
    STRING,
    CHARACTER,
    INTEGER,
    FLOAT,
    BOOLEAN
}

class AxolotlTokenLiteral(override val content: String, val type: AxolotlTokenLiteralType) : AxolotlToken(content)

class AxolotlTokenComment(override val content: String) : AxolotlToken(content)

enum class AxolotlTokenDelimiterType {
    LEFT_PARENT, RIGHT_PARENT,
    LEFT_BRACE, RIGHT_BRACE,
    LEFT_SQUARE, RIGHT_SQUARE,
    COMMA,
    SEMI,
    LOW_SEMI
}

class AxolotlTokenDelimiter(override val content: String, val type: AxolotlTokenDelimiterType) : AxolotlToken(content)