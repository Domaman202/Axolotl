package axl.lexer

class AxolotlTokenDelimiter(override val content: String, val type: AxolotlTokenDelimiterType) : AxolotlToken(content) {
    init {
        length = content.length
    }

    override fun toString(): String =
        "[DELIMITER \"${type.name}\" \"${content}\"" + super.toString()
}