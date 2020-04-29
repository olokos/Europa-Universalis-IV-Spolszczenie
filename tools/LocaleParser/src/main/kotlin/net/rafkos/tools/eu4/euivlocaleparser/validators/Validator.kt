package net.rafkos.tools.eu4.euivlocaleparser.validators

/**
 * The purpose of this class is to check texts for specific locale types.
 */
interface Validator {
    fun validate(text: String)
}