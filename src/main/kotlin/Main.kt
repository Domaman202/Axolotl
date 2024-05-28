import axl.File
import axl.lexer.AxolotlKeyword
import axl.lexer.AxolotlLexer
import axl.lexer.AxolotlOperator

// 0B1_01uL

fun main(args: Array<String>) {
//    val lexer = AxolotlLexer(File("Main.axl", """
//        package axl.example
//
//        fn main(args: List<String>) > void {
//            println("Hello, world!")
//        }
//    """.trimIndent()))
    val lexer = AxolotlLexer(File("Main.axl", """
        package axl
        bla bla
        bla
        
        -.d bla
    """.trimIndent()))

    lexer.add(AxolotlKeyword("package", "package"))
    lexer.tokenize()
    while (lexer.hasMoreTokens())
        println(lexer.nextToken())
}