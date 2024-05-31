package axl.lexer

class AxolotlTokenOperator(override val content: AxolotlOperator) : AxolotlToken(content) {
    init {
        length = content.value.length
    }

    override fun toString(): String =
        "[OPERATOR \"${content.value}\"" + super.toString()
}