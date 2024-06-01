import axl.File
import axl.lexer.*

// 0B1_01uL

fun main() {
    val lexer = AxolotlLexer(File("Main.axl", """
        :-/
    """.trimIndent()))

    lexer.add(AxolotlOperator(":-/"))
    
    try {
        val frame = lexer.saveFrame()
        val time = System.nanoTime()
        while (lexer.nextToken() != null) ;
        println(System.nanoTime() - time)

        lexer.restoreFrame(frame)
        while (lexer.nextToken() != null)
            println(lexer.peekToken())
    } catch (e: AxolotlLexerTokenizeException) {
        System.err.println(e.message)
    }
}