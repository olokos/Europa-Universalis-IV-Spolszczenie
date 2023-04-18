package net.rafkos.tools.eu4.euivlocaleparser

import java.util.regex.Pattern

object Constants {
    val BOM = byteArrayOf(0xEF.toByte(), 0xBB.toByte(), 0xBF.toByte())

    /**
     * Does the Transifex use quotes to start and end strings?
     */
    const val TRANSIFEX_QUOTES = true

    /**
     * Regex for accepted characters for keys.
     */
    val ACCEPTED_KEY_CHARS: Pattern = Pattern.compile("[a-zA-Z0-9-_.']")

    /**
     * Filter for create_short command.
     */
    val SHORT_FILES_FILTER: (String, String) -> Boolean = { key, value -> value.length in 0..30 && !key.startsWith("PROV") }
    val SHORT_FILES_FILTER_2: (String, String) -> Boolean = { key, value -> value.length in 31..60 && !key.startsWith("PROV") }
}