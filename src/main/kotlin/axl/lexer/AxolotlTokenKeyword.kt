package axl.lexer

class AxolotlTokenKeyword(override val content: AxolotlKeyword) : AxolotlToken(content) {
    override fun toString(): String =
        "[KEYWORD \"${content.value}\"" + super.toString()
}