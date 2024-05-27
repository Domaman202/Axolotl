import axl.File
import axl.lexer.AxolotlKeyword
import axl.lexer.AxolotlLexer
import axl.lexer.AxolotlOperator

fun main(args: Array<String>) {
    val lexer = AxolotlLexer(File("Main.axl", """
        package axl.example
        
        fn main(args: List<String>) > void {
            println("Hello, world!")
        }
    """.trimIndent()))

    lexer.add(AxolotlKeyword("package", "package"))
    lexer.add(AxolotlKeyword("fn", "function"))
    lexer.add(AxolotlKeyword("void", "void"))

    lexer.add(AxolotlOperator(".", "DOT"))
    lexer.add(AxolotlOperator("<", "LESS"))
    lexer.add(AxolotlOperator(">", "MORE"))
    println(0B1_01uL)

    lexer.confirm()
}