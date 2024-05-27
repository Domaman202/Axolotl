package axl.lexer

import axl.File
import axl.utils.mutableSetToImmutableSet

class AxolotlLexer(val file: File) {

    var offset: Int = 0

    var row: Int = 0

    var column: Int = 0

    private val keywords: Set<AxolotlKeyword> = mutableSetOf()

    private val operators: Set<AxolotlOperator> = mutableSetOf()

    private val tokens: List<AxolotlToken> = mutableListOf()

    fun add(keyword: AxolotlKeyword) {
        if (keyword in keywords)
            throw IllegalArgumentException("The keyword \"${keyword.value}\" is already in the lexer.")

        if (keywords !is MutableSet)
            throw IllegalStateException("You cannot add keywords after confirming the lexer.")

        keywords += keyword
    }

    fun add(operator: AxolotlOperator) {
        if (operator in operators)
            throw IllegalArgumentException("The keyword \"${operator.value}\" is already in the lexer.")

        if (operators !is MutableSet)
            throw IllegalStateException("You cannot add operators after confirming the lexer.")

        operators += operator
    }

    fun confirm() {
        mutableSetToImmutableSet(keywords)
        mutableSetToImmutableSet(operators)
    }

}

data class AxolotlLexerFrame(val content: List<AxolotlToken>, val offset: Int, val length: Int)