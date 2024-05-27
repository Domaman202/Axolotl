package axl.lexer

fun AxolotlLexer.next(): Char? {
    if (offset >= file.content.length - 1) return null
    offset++
    val char = file.content[offset]
    if (char == '\n') {
        row++
        column = 0
    } else {
        column++
    }
    return char
}

fun AxolotlLexer.prev(): Char? {
    if (offset <= 0) return null
    offset--
    val char = file.content[offset]
    if (char == '\n') {
        row--
        column = 0
        var i = offset - 1
        while (i >= 0 && file.content[i] != '\n') {
            column++
            i--
        }
    } else {
        column--
    }
    return char
}

fun AxolotlLexer.get(): Char? = if (offset >= file.content.length || offset < 0) null else file.content[offset]

fun AxolotlLexer.skip() {
    next();
    while (get() != null && (get() == ' ' || get() == '\n' || get() == '\r' || get() == '\t'))
        next();
}