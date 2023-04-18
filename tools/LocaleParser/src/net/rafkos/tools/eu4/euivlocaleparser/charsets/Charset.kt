package net.rafkos.tools.eu4.euivlocaleparser.charsets

/**
 * Represents a fake charset map for custom font symbols.
 */
abstract class Charset(
    /**
     * Custom EUIV symbols to UTF8 symbols.
     */
    val eu4toutf8: Map<String, String>) {

    /**
     * UTF8 to custom EUIV symbols.
     */
    val utf8toeu4: Map<String, String>

    init {
        utf8toeu4 = hashMapOf()
        eu4toutf8.forEach { (utf8, eu4) -> utf8toeu4[eu4] = utf8}
    }
}