package axl

class File(val filename: String, val content: String)

fun File.getLine(line: Int) : String = this.content.split("\n")[line]