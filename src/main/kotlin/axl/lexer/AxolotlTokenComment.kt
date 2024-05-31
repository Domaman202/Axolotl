package axl.lexer


class AxolotlTokenComment(override val content: String) : AxolotlToken(content) {
    init {
        length = content.length + if ('\n' in content) 4 else 2
    }

    override fun toString(): String =
        "[COMMENT {\n${content}\n}" + super.toString()
}