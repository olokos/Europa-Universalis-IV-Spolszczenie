package net.rafkos.tools.eu4.euivlocaleparser.validators

import net.rafkos.tools.eu4.euivlocaleparser.Constants
import net.rafkos.tools.eu4.euivlocaleparser.TextUtils
import org.apache.logging.log4j.LogManager

object ValidatorTransifex : Validator {
    private val logger = LogManager.getLogger(this.javaClass)

    override fun validate(text: String) {
        val inQuotes = Constants.TRANSIFEX_QUOTES
        if (inQuotes) {
            TextUtils.quotesValidation(text)
        }
        var index = 0
        while (index < text.length) {
            val c = text[index]
            when (c) {
                '\\' -> {
                    val next = if (index + 1 < text.length) text[++index]
                    else {
                        logger.error("Escape symbol found but no following character provided in string: \"$text\" at index $index.")
                        throw IllegalArgumentException()
                    }
                    when (next) {
                        'n', 't', '\\', '"' -> {} // correct
                        else -> {
                            logger.error("Illegal symbol escaped: \"$next\" in string \"$text\" at index $index.")
                            throw IllegalArgumentException()
                        }
                    }
                }
                '"' -> {
                    if (!inQuotes || (inQuotes && index != 0 && index != text.lastIndex)) {
                        logger.error("Unescaped quote symbol in string \"$text\" at index $index.\nCheck if provided file is correct and not in EUIV format.")
                        throw IllegalArgumentException()
                    }
                }
            }
            index++
        }
    }
}