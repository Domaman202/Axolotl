package axl.lexer.token

abstract class AxolotlToken(open val content: Any) {
    var row: Int? = null
    var column: Int? = null
    var length: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (javaClass != other?.javaClass)
            return false
        return content == (other as AxolotlToken).content
    }

    override fun toString(): String =
        " $row:$column:$length]"

    override fun hashCode(): Int {
        var result = content.hashCode()
        result = 31 * result + (row?.hashCode() ?: 0)
        result = 31 * result + (column?.hashCode() ?: 0)
        result = 31 * result + length
        return result
    }

    fun isRightDelimiter(): Boolean =
        if (this !is AxolotlTokenDelimiter)
            false
        else when (type) {
            AxolotlTokenDelimiterType.RIGHT_SQUARE,
            AxolotlTokenDelimiterType.RIGHT_BRACE,
            AxolotlTokenDelimiterType.RIGHT_PARENT -> true
            else -> false
        }
}