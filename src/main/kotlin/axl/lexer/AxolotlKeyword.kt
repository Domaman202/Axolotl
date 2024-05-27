package axl.lexer

import java.lang.IllegalArgumentException

class AxolotlKeyword(val value: String, val debugName: String) {

    companion object {
        val REGEX = Regex("[A-Za-z]+")
    }

    init {
        if (!value.matches(REGEX))
            throw IllegalArgumentException("The keyword must consist of Latin letters only and contain at least one character.")
    }

    override fun equals(other: Any?): Boolean {
        if (other is AxolotlKeyword)
            return this.value == other.value

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}