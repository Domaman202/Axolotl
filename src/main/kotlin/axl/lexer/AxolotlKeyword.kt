package axl.lexer

import java.lang.IllegalArgumentException

class AxolotlKeyword(val value: String, val debugName: String? = null) {

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

        if (other is String)
            return this.value == other

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}