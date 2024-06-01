package axl.lexer

import java.lang.IllegalArgumentException

class AxolotlOperator(val value: String, val debugName: String? = null) {
    init {
        for (char in value) {
            if (!char.isOperator()) {
                throw IllegalArgumentException("The operator can only contain characters from \"`!@#\$%^&*\\-=+:.<>/?|~`\"")
            }
        }
    }

    override fun equals(other: Any?): Boolean =
        if (other is AxolotlOperator)
            this.value == other.value
        else super.equals(other)

    override fun hashCode(): Int =
        value.hashCode()

}

fun Char.isOperator() : Boolean {
    return when(this) {
        '!', '@', '#', '$', '%', '^', '&', '*', '\\',
        '-', '=', '+', ':', '.', '<', '>', '/', '?',
        '|', '~', '`' -> true
        else -> false
    }
}