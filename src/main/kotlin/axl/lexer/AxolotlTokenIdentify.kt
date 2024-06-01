package axl.lexer

class AxolotlTokenIdentify(override val content: String) : AxolotlToken(content) {
    override fun toString(): String =
        "[IDENTIFY \"$content\"" + super.toString()
}