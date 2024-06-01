package axl

class File(val filename: String, content: String) {
    val content = content.replace("\r", "")
}

fun File.getLine(line: Int) : String = this.content.split("\n")[line]