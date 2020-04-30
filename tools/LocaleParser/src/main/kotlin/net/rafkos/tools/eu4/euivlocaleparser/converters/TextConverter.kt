package net.rafkos.tools.eu4.euivlocaleparser.converters

import net.rafkos.tools.eu4.euivlocaleparser.Constants
import net.rafkos.tools.eu4.euivlocaleparser.TextUtils
import net.rafkos.tools.eu4.euivlocaleparser.charsets.Charset
import org.apache.logging.log4j.LogManager

/**
 * Purpose of this class is to provide conversion between EUIV and Transifex types of texts.
 */
object TextConverter {
    private val logger = LogManager.getLogger(this.javaClass)

    /**
     * Converts from Transifex to EUIV format.
     * text - Text to convert. Supports string both with or without quotation.
     * charset - Charset type (special symbols).
     */
    fun convertToEUIV(text: String, charset: Charset): String {
        val sourceInQuotes = text.startsWith('"') && text.endsWith('"')
        val builder = StringBuilder()
        val textConverted = processCharsets(text, charset.utf8toeu4)
                .replace("##CHAR_DETECT##", ":") //legacy support
        if (sourceInQuotes) {
            TextUtils.quotesValidation(textConverted)
        } else {
            builder.append('"')
        }
        var index = 0
        while (index < textConverted.length) {
            if (sourceInQuotes && (index == 0 || index == textConverted.lastIndex)) {
                if (textConverted[index] != '"') {
                    logger.error("Quote symbol expected in string \"$textConverted\" at index $index. Got \"${textConverted[index]}\".")
                    throw IllegalArgumentException()
                }
                builder.append('"')
            } else {
                when (val c = textConverted[index]) {
                    '\\' -> {
                        val next = if (index + 1 < textConverted.length) textConverted[++index]
                        else {
                            logger.error("Escape symbol found but no following character provided in string: \"$textConverted\" at index $index.")
                            throw IllegalArgumentException()
                        }
                        when (next) {
                            '"' -> builder.append('"')
                            'n', 't', '\\' -> builder.append("\\$next")
                            else -> {
                                logger.error("Illegal symbol escaped: \"$next\" in string \"$textConverted\" at index $index.")
                                throw IllegalArgumentException()
                            }
                        }
                    }
                    else -> builder.append(c)
                }
            }
            index++
        }
        if (!sourceInQuotes)
            builder.append('"')
        return builder.toString()
    }

    /**
     * Converts from EUIV format to Transifex.
     * text - Text to convert.
     * charset - Charset type (special symbols).
     * inQuotes - Determines whether or not the result string should start and end with quotes.
     */
    fun convertToYaml(text: String, charset: Charset, inQuotes: Boolean = Constants.TRANSIFEX_QUOTES): String {
        val builder = StringBuilder()
        val textConverted = processCharsets(text, charset.eu4toutf8)
                .replace("##CHAR_DETECT##", ":") //legacy support
        TextUtils.quotesValidation(textConverted)
        var index = 0
        while (index < textConverted.length) {
            if (index == 0 || index == textConverted.lastIndex) {
                if (textConverted[index] != '"') {
                    logger.error("Quote symbol expected in string \"$textConverted\" at index $index. Got \"${textConverted[index]}\".")
                    throw IllegalArgumentException()
                }
                if (inQuotes)
                    builder.append('"')
            } else {
                when (val c = textConverted[index]) {
                    '\\' -> {
                        val next = if (index + 1 < textConverted.length) textConverted[++index]
                        else {
                            logger.error("Escape symbol found but no following character provided in string: \"$textConverted\" at index ${index}.")
                            throw IllegalArgumentException()
                        }
                        when (next) {
                            'n', 't', '\\', '"' -> builder.append("\\$next")
                            else -> {
                                logger.error("Illegal symbol escaped: \"$next\" in string \"$textConverted\" at index ${index}.")
                                throw IllegalArgumentException()
                            }
                        }
                    }
                    '"' -> {
                        builder.append("\\\"")
                    }
                    else -> builder.append(c)
                }
            }
            index++
        }
        return builder.toString()
    }

    /**
     * Replaces letters in provided text with charset map.
     */
    private fun processCharsets(text: String, map: Map<String, String>): String {
        val converted = StringBuilder()
        val unicodeArray: Array<String> = text.codePoints()
                .mapToObj { cp -> String(Character.toChars(cp)) }
                .toArray { size -> arrayOfNulls<String>(size) }
        unicodeArray.forEach { converted.append(map[it] ?: it) }
        return converted.toString()
    }
}