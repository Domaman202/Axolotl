package axl.lexer

class AxolotlTokenComment(override val content: String) : AxolotlToken(content) {
    override fun toString(): String =
        if ('\n' in content)
            "[COMMENT {\n$content\n}" + super.toString()
        else "[COMMENT \"$content\"" + super.toString()
}
