package net.rafkos.tools.eu4.euivlocaleparser

/**
 * Basically a string with priority field
 */
class PriorityString(val value: String, val priority: Int) : Comparable<PriorityString> {
    override fun hashCode(): Int = value.hashCode()
    override fun equals(other: Any?): Boolean =
            if (other is PriorityString) value == other.value else false

    override fun compareTo(other: PriorityString): Int = value.compareTo(other.value)
}