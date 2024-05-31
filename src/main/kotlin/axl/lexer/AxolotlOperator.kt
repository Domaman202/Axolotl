package axl.lexer

import java.lang.IllegalArgumentException

class AxolotlOperator(val value: String, val debugName: String) {
    init {
        if (!value.matches(REGEX)) {
            throw IllegalArgumentException("The operator can only contain characters from \"`!@#\$%^&*\\-=+:.<>/?|~\"")
        }
    }

    override fun equals(other: Any?): Boolean =
        if (other is AxolotlOperator)
            this.value == other.value
        else super.equals(other)

    override fun hashCode(): Int =
        value.hashCode()

    companion object {
        val REGEX = Regex("[`!@#$%^&*\\\\\\-=+:.<>/?|~]+")
    }
}
