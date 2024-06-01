package axl.lexer

class AxolotlTokenLiteral(override val content: String, val type: AxolotlTokenLiteralType) : AxolotlToken(content) {
    constructor(content: Char) : this(content.toString(), AxolotlTokenLiteralType.CHARACTER)
    constructor(content: String) : this(content, AxolotlTokenLiteralType.STRING)

    override fun toString(): String =
        if ('\n' in content)
            "[LITERAL \"${type.name}\" {\n$content\n}" + super.toString()
        else "[LITERAL \"${type.name}\" \"${content}\"" + super.toString()
}