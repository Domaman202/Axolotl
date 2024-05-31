package axl.lexer

class AxolotlTokenIdentify(override val content: String) : AxolotlToken(content) {
    init {
        length = content.length
    }

    override fun toString(): String =
        "[IDENTIFY \"$content\"" + super.toString()
}
