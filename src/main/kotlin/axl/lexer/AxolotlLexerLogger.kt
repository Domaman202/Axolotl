package axl.lexer

import axl.getLine

fun AxolotlLexer.errorMessage(row: Int, column: Int, errorMessage: String): String {
    val whitespaces = " ".repeat(row.toString().length + 1)
    return """
        $whitespaces[ERROR]: $errorMessage
        
        $whitespaces╭ ⎯⎯⎯ ${file.filename}:$row:$column ⎯⎯⎯⎯⎯
        $row │ ${file.getLine(row-1)}
        $whitespaces│${" ".repeat(column)}╰ $errorMessage
    """.trimIndent()
}