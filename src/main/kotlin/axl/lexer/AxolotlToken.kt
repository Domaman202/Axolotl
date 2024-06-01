package axl.lexer

abstract class AxolotlToken(open val content: Any) {
    var position: AxolotlTokenPosition? = null
    var length: Int = 0

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (javaClass != other?.javaClass)
            return false
        return content == (other as AxolotlToken).content
    }

    override fun toString(): String =
        " ${position!!.row}:${position!!.column}:$length]"

    override fun hashCode(): Int {
        var result = content.hashCode()
        result = 31 * result + (position?.hashCode() ?: 0)
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