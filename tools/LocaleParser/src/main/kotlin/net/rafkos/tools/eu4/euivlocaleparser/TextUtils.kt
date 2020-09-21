package net.rafkos.tools.eu4.euivlocaleparser

import org.apache.logging.log4j.LogManager

object TextUtils {
    private val logger = LogManager.getLogger(this.javaClass)
    /**
     * Performs starting/ending quotes validation for both EUIV and Yaml formats.
     */
    fun quotesValidation(text: String) {
        if (text.length < 2) {
            logger.error("Provided string is too short. Shortest string is expected to have at least 2 characters. Got \"$text\".")
            throw IllegalArgumentException()
        }
        if (!text.startsWith("\"") || !text.endsWith("\"")) {
            logger.error("Provided string has to start and end with quote symbols. Got \"$text\".")
        }
    }

    fun removeComments(text: String): String {
        val builder = StringBuilder()
        var insideQuote = false
        var index = 0
        var cutIndex = -1
        loop@ while (index < text.length) {
            val c = text[index]
            when (c) {
                '#' -> if (!insideQuote && cutIndex == -1) cutIndex = index
                '\\' -> {
                    builder.append("\\${text[++index]}")
                    index++
                    continue@loop
                } // skip escaped char
                '"' -> insideQuote = !insideQuote.also { if (insideQuote) cutIndex = -1 }
            }
            builder.append(c)
            index++
        }
        return when (cutIndex) {
            -1 -> builder.toString()
            else -> builder.toString().substring(0, cutIndex)
        }
    }
}