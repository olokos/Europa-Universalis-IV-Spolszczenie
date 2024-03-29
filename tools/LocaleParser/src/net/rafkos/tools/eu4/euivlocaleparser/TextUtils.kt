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

    fun removeComments(text: String, type: LocaleType): String {
        return when (type) {
            LocaleType.YAML -> removeCommentsInYAMLFormat(text)
            LocaleType.EUIV -> removeCommentsInEU4Format(text)
        }
    }

    private fun removeCommentsInYAMLFormat(text: String): String {
        val builder = StringBuilder()
        var insideQuote = false
        var index = 0
        loop@ while (index < text.length) {
            val c = text[index]
            when (c) {
                '#' -> if (!insideQuote) break@loop
                '\\' -> {
                    builder.append("\\${text[++index]}")
                    index++
                    continue@loop
                } // skip escaped char
                '"' -> insideQuote = !insideQuote
            }
            builder.append(c)
            index++
        }
        return builder.toString()
    }

    /**
     * Due to inconsistent yaml format in Paradox games the comments are supported at the beginning of the text only.
     *
     * Example url comment:
     *  "De Architectura" #https://en.wikipedia.org/wiki/French_Renaissance_architecture#Religious_architecture
     *
     * Example category comment:
     * "Have at least $VALUE|Y$ $CATEGORY$ $TYPE$.\n" #category = musketeer, tercio, streltsy etc; type = infantry, cavalry etc
     *
     */
    private fun removeCommentsInEU4Format(text: String): String {
        val trimmed = text.trim()

        // Remove url comment
        val patternUrlComment = Regex(" #https://\\S+")
        val withoutUrlComment = trimmed.replace(patternUrlComment, "")

        // Remove category comment
        val patternCategoryComment = Regex(" #category = .+")
        val withoutCategoryComment = withoutUrlComment.replace(patternCategoryComment, "")

        if (withoutCategoryComment.isEmpty()) return ""
        return when (withoutCategoryComment[0]) {
            '#' -> ""
            else -> withoutCategoryComment
        }
    }
}