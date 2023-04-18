package net.rafkos.tools.eu4.euivlocaleparser

import org.apache.logging.log4j.LogManager

/**
 * Represents a locale file with correct YML syntax (not EUIV version)
 */
class EUIVLocale(val fileName: String, val type: LocaleType) {
    private val logger = LogManager.getLogger(this.type)

    /**
     * Represents locale contents:
     * language -> keys -> (num, text)
     */
    val entries = hashMapOf<PriorityString, HashMap<PriorityString, Pair<Int, String>>>()

    /**
     * This function will replace all empty texts with ones in supplementary locale file.
     * This operation can only be performed on locales of the same types.
     */
    fun supplement(supplement: EUIVLocale) {
        logger.info("Combining locale files for supplementing empty texts between \"$fileName\" and \"$fileName\".")
        if (type != supplement.type) {
            logger.error("Can only supplement locales of the same type.")
            throw IllegalArgumentException()
        }
        hashMapOf<PriorityString, HashMap<PriorityString, Pair<Int, String>>>().also { it.putAll(entries) }.forEach { (language, lines) ->
            lines.forEach { (key, pair) ->
                if ((pair.second == "" || pair.second == "\"\"") && supplement.entries[language]?.containsKey(key) == true) {
                    entries[language]!![key] = supplement.entries[language]!![key]!!
                }
            }
        }
    }

    fun clone(): EUIVLocale {
        val clone = EUIVLocale(fileName, type)
        entries.forEach { (lang, keys) ->
            if (!clone.entries.containsKey(lang))
                clone.entries[lang] = hashMapOf()
            keys.forEach { (key, pair) ->
                clone.entries[lang]!![key] = Pair(pair.first, pair.second)
            }

        }
        return clone
    }
}