package net.rafkos.tools.eu4.euivlocaleparser.converters

import net.rafkos.tools.eu4.euivlocaleparser.Locale
import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.SpecialFilenames
import net.rafkos.tools.eu4.euivlocaleparser.charsets.Charset
import org.apache.logging.log4j.LogManager

/**
 * Purpose of this class is to convert between locale types.
 */
object LocaleConverter {
    private val logger = LogManager.getLogger(this.javaClass)

    /**
     * Converts specified locale file with given charset to given type.
     */
    fun convertToType(loc: Locale, charset: Charset, type: LocaleType): Locale {
        if (loc.type == type) {
            logger.error("This file is already in specified format.")
            throw IllegalArgumentException()
        }
        return Locale(SpecialFilenames.getFilename(loc.fileName,
                if (type == LocaleType.EUIV) LocaleType.YAML else LocaleType.EUIV), type).also { locc ->
            loc.entries.forEach { (lang, lines) ->
                lines.forEach {(key, pair) ->
                    if (!locc.entries.containsKey(lang))
                        locc.entries[lang] = hashMapOf()
                    val text = when (type) {
                        LocaleType.YAML -> TextConverter.convertToYaml(pair.second, charset)
                        LocaleType.EUIV -> TextConverter.convertToEUIV(pair.second, charset)
                    }
                    locc.entries[lang]!![key] = Pair(pair.first, text)
                }

            }
        }
    }
}