package axl

class File(val filename: String, content: String) {
    val content = content.replace("\r", "")

    fun getLine(line: Int) : String =
        this.content.split("\n")[line]
}