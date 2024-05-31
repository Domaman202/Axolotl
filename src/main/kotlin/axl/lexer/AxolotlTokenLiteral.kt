package axl.lexer

class AxolotlTokenLiteral(override val content: String, val type: AxolotlTokenLiteralType) : AxolotlToken(content) {
    init {
        length = when (type) {
            AxolotlTokenLiteralType.STRING,
            AxolotlTokenLiteralType.CHARACTER -> content.length + 2
            else -> content.length
        }
    }

    override fun toString(): String =
        "[LITERAL \"${type.name}\" \"${content}\"" + super.toString()
}