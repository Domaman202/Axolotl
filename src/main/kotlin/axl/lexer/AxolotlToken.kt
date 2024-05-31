package axl.lexer

abstract class AxolotlToken (open val content: Any) {
    var position: AxolotlTokenPosition? = null
        protected set
    var length: Int = 0
        protected set

    fun setPosition(row: Int, column: Int) {
        this.position = AxolotlTokenPosition(row, column)
    }

    override fun hashCode(): Int {
        throw IllegalStateException("")
    }

    override fun equals(other: Any?): Boolean {
        if (this === other)
            return true
        if (javaClass != other?.javaClass)
            return false
        return content == (other as AxolotlToken).content
    }

    override fun toString(): String =
        "[${position!!.row}:${position!!.column}:$length]"
}
