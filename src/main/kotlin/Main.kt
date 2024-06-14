import axl.File
import axl.lexer.*

// 0B1_01uL

fun main() {
    val lexer = AxolotlLexer(File("Main.axl", """
        package axl.example;
        
        fn main(args: List<String>) {
            println("Hello, world!);
        }
    """.trimIndent()))

    lexer.add(AxolotlOperator("<"))
    lexer.add(AxolotlOperator(">"))
    lexer.add(AxolotlOperator(":"))
    lexer.add(AxolotlOperator("."))
    lexer.add(AxolotlKeyword("package"))
    lexer.add(AxolotlKeyword("fn"))

    try {

        val frame = lexer.saveFrame()
        val time = System.nanoTime()

        while (lexer.nextToken() != null);

        println(System.nanoTime() - time)
        lexer.restoreFrame(frame)
        while (lexer.nextToken() != null)
            println(lexer.peekToken())

    } catch (e: AxolotlLexerTokenizeException) {
        System.err.println(e.message)
    }
}