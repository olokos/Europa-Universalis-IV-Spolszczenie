package net.rafkos.tools.eu4.euivlocaleparser.charsets

/**
 * Purpose of this class is to register charsets.
 */
object Charsets {
    val charsets = mapOf(
            "polish" to PolishCharset,
            "empty" to NoCharset
    )
}