package axl.lexer

import java.lang.IllegalArgumentException

class AxolotlOperator(val value: String, val debugName: String? = null) {

    companion object {
        val REGEX = Regex("[`!@#$%^&*\\\\\\-=+:.<>/?|~]+")
    }

    init {
        if (!value.matches(REGEX))
            throw IllegalArgumentException("The operator can only contain characters from \"`!@#\$%^&*\\-=+:.<>/?|~\"")
    }

    override fun equals(other: Any?): Boolean {
        if (other is AxolotlOperator)
            return this.value == other.value

        return super.equals(other)
    }

    override fun hashCode(): Int {
        return value.hashCode()
    }

}