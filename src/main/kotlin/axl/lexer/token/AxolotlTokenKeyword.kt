package axl.lexer.token

import axl.lexer.AxolotlKeyword

class AxolotlTokenKeyword(override val content: AxolotlKeyword) : AxolotlToken(content) {
    override fun toString(): String =
        "[KEYWORD \"${content.value}\"" + super.toString()
}