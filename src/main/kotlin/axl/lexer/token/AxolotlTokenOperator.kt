package axl.lexer.token

import axl.lexer.AxolotlOperator

class AxolotlTokenOperator(override val content: AxolotlOperator) : AxolotlToken(content) {
    init {
        length = content.value.length
    }

    override fun toString(): String =
        "[OPERATOR \"${content.value}\"" + super.toString()
}