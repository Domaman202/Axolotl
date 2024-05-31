package axl.lexer

class AxolotlTokenKeyword(override val content: AxolotlKeyword) : AxolotlToken(content) {
    init {
        length = content.value.length
    }

    override fun toString(): String =
        "[KEYWORD \"${content.value}\"" + super.toString()
}