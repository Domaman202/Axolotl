package axl.lexer

import java.lang.IllegalArgumentException

class AxolotlKeyword(val value: String, val debugName: String) {
    init {
        if (!value.matches(REGEX)) {
            throw IllegalArgumentException("The keyword must consist of Latin letters only and contain at least one character.")
        }
    }

    override fun equals(other: Any?): Boolean =
        when (other) {
            is AxolotlKeyword -> this.value == other.value
            is String -> this.value == other
            else -> super.equals(other)
        }

    override fun hashCode(): Int =
        value.hashCode()

    companion object {
        val REGEX = Regex("[A-Za-z]+")
    }
}