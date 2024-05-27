package axl.lexer

import axl.getLine

fun AxolotlLexer.errorMessage(position: Position, errorMessage: String): String {
    val whitespaces = " ".repeat(row.toString().length + 1)
    return """
        $whitespaces[ERROR]: $errorMessage
        
        $whitespaces╭ ⎯⎯⎯ ${file.filename}:${position.row}:${position.column} ⎯⎯⎯⎯⎯
        ${position.row} │ ${file.getLine(position.row-1)}
        $whitespaces│${" ".repeat(position.column)}╰ $errorMessage
    """.trimIndent()
}