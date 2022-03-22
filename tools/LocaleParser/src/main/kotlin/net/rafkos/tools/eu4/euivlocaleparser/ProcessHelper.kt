package net.rafkos.tools.eu4.euivlocaleparser

import net.rafkos.tools.eu4.euivlocaleparser.charsets.Charset
import net.rafkos.tools.eu4.euivlocaleparser.converters.LocaleConverter
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import org.apache.logging.log4j.LogManager
import java.io.File

object ProcessHelper {
    private val logger = LogManager.getLogger(this.javaClass)

    /**
     * Converts all files in directory to specified format type.
     */
    fun loadDirectoryOfType(directory: File, type: LocaleType): List<EUIVLocale> {
        if (!directory.isDirectory && directory.exists()) {
            logger.error("Source folder \"${directory.canonicalPath}\" is not a folder.")
            throw IllegalArgumentException()
        }

        val EUIVLocales = mutableListOf<EUIVLocale>()

        for (file in directory.listFiles()!!) {
            if (file.isFile && (file.extension == "yml" || file.extension == "txt")) {
                val locale = LocaleLoader.loadFile(file, type)
                EUIVLocales.add(locale)
            }
        }

        return EUIVLocales
    }

    /**
     * Writes specified locales to given directory.
     */
    fun writeLocalesToDirectory(directory: File, EUIVLocales: List<EUIVLocale>) {
        if (!directory.isDirectory && directory.exists()) {
            logger.error("Output folder \"${directory.canonicalPath}\" is not a folder.")
            throw IllegalArgumentException()
        }
        for (locale in EUIVLocales) {
            val target = File(directory,locale.fileName)
            LocaleLoader.writeToFile(target, locale)
        }
    }

    /**
     * Converts locales with given charset to given type.
     */
    fun convertLocalesToType(EUIVLocales: List<EUIVLocale>, charset: Charset, type: LocaleType): List<EUIVLocale> {
        val converted = mutableListOf<EUIVLocale>()
        for (locale in EUIVLocales) {
            logger.info("Converting locale \"${locale.fileName}\" from type \"${locale.type}\" to \"$type\" using charset \"${charset.javaClass.simpleName}\".")
            if (locale.fileName != SpecialFilenames.getFilename(locale.fileName, type))
                logger.info("Also changing filename from \"${locale.fileName}\" to \"${SpecialFilenames.getFilename(locale.fileName, type)}\".")
            converted.add(LocaleConverter.convertToType(locale, charset, type))
        }
        return converted.toList()
    }

    /**
     * Combines two locale lists to replace empty strings with ones in supplement locales.
     * The locales are identified by their names.
     */
    fun combineLocales(EUIVLocales: List<EUIVLocale>, supplementEUIVLocales: List<EUIVLocale>) {
        EUIVLocales.forEach { locale ->
            supplementEUIVLocales.forEach { supplement ->
                if (locale.fileName == supplement.fileName) {
                    locale.supplement(supplement)
                }
            }
        }
    }

    /**
     * Filters input locales based on condition. Each input locale's entry will be checked against condition.
     * - when condition is true the input entry will be added to output locale
     * - when condition is false the input entry will be filtered from output
     * Condition takes input's entry.
     */
    fun filterLocales(input: List<EUIVLocale>, condition: (String, String) -> Boolean): List<EUIVLocale> {
        val output = mutableListOf<EUIVLocale>()
        input.forEach { i ->
                    val o = EUIVLocale(i.fileName, i.type)
                    i.entries.forEach { (lang, lines) ->
                        lines.forEach { (key, pair) ->
                            if (condition(key.value, pair.second)) {
                                if (!o.entries.containsKey(lang))
                                    o.entries[lang] = hashMapOf()
                                o.entries[lang]!![key] = Pair(pair.first, pair.second)
                            }
                    }
                    output.add(o)
                }
            }
        return output
    }

    fun filterLocales(input: List<EUIVLocale>, condition: (String) -> Boolean): List<EUIVLocale> =
            filterLocales(input) { _, value -> condition(value)}

    /**
     * Filters input locales based on filter locales and condition. Each input locale's entry will be checked against
     * corresponding filter locale's entry:
     * - when condition is true the input entry will be added to output locale
     * - when condition is false the input entry will be filtered from output
     * - when corresponding locale file does not exist then the locale will be included in output
     * - when corresponding entry does not exist then the entry will be included in output
     * Condition takes input's entry and filter's entry.
     */
    fun filterLocales(input: List<EUIVLocale>, filter: List<EUIVLocale>, condition: (String, String) -> Boolean): List<EUIVLocale> {
        val output = mutableListOf<EUIVLocale>()
        input.forEach { i ->
            when (val f = findCorrespondingLocale(i, filter)) {
                null -> output.add(i.clone())
                else -> {
                    val o = EUIVLocale(i.fileName, i.type)
                    i.entries.forEach { (lang, lines) ->
                        lines.forEach { (key, pair) ->
                            val add = when {
                                f.entries.containsKey(lang) && f.entries[lang]!!.containsKey(key) ->
                                    condition(pair.second, f.entries[lang]!![key]!!.second)
                                else -> true
                            }
                            if (add) {
                                if (!o.entries.containsKey(lang))
                                    o.entries[lang] = hashMapOf()
                                o.entries[lang]!![key] = Pair(pair.first, pair.second)
                            }
                        }
                    }
                    output.add(o)
                }
            }
        }
        return output
    }

    fun filterLocales(input: EUIVLocale, filter: EUIVLocale, output: EUIVLocale, condition: (String, String) -> Boolean) {
        input.entries.forEach { (lang, lines) ->
            lines.forEach { (key, pair) ->
                val add = when {
                    filter.entries.containsKey(lang) && filter.entries[lang]!!.containsKey(key) ->
                        condition(pair.second, filter.entries[lang]!![key]!!.second)
                    else -> true
                }
                if (add) {
                    if (!output.entries.containsKey(lang))
                        output.entries[lang] = hashMapOf()
                    output.entries[lang]!![key] = Pair(pair.first, pair.second)
                }
            }
        }
    }

    /**
     * Returns a locale with input's filename from locales list. Returns null if not found. In case of many
     * locales it will return first one found.
     */
    private fun findCorrespondingLocale(input: EUIVLocale, EUIVLocales: List<EUIVLocale>): EUIVLocale? =
        EUIVLocales.find { it.fileName == input.fileName }

    /**
     * Merges provided locales to one locale.
     */
    fun mergeLocales(EUIVLocales: List<EUIVLocale>, fileName: String, type: LocaleType): EUIVLocale {
        val o = EUIVLocale(fileName, type)
        EUIVLocales.forEach { loc ->
            when {
                loc.type != type -> {
                    logger.error("Incorrect locale type provided. Expected \"$type\" format, got \"${loc.type}\".")
                    throw IllegalArgumentException()
                }
                else -> loc.entries.forEach { (lang, lines) ->
                    lines.forEach { (key, pair) ->
                        if (!o.entries.containsKey(lang))
                            o.entries[lang] = hashMapOf()
                        o.entries[lang]!![key] = Pair(pair.first, pair.second)
                    }
                }
            }
        }
        return o
    }

    /**
     * Patches entries in provided locales with given patches.
     */
    fun patchLocales(EUIVLocales: List<EUIVLocale>, patches: List<EUIVLocale>): List<EUIVLocale> {
        val output = mutableListOf<EUIVLocale>()
        EUIVLocales.forEach {
            loc -> patches.find { it.fileName == loc.fileName }?.also { output.add(patchLocales(loc, it)) }
        }
        return output
    }

    /**
     * Patches entries in provided locale with given patch.
     */
    fun patchLocales(EUIVLocale: EUIVLocale, patch: EUIVLocale): EUIVLocale {
        val output = EUIVLocale.clone()
        patch.entries.forEach { (lang, lines) ->
            lines.forEach { (key, pair) ->
                val patchPair = patch.entries[lang]!![key]!!
                val keyPriority = EUIVLocale.entries[lang]?.keys?.find { it.value == key.value }?.priority ?: key.priority
                val langPriority = EUIVLocale.entries.keys.find { it.value == lang.value }?.priority ?: lang.priority
                if (!EUIVLocale.entries.containsKey(lang)) {
                    EUIVLocale.entries[PriorityString(lang.value, langPriority)] = hashMapOf()
                }
                output.entries[PriorityString(lang.value, langPriority)]!![PriorityString(key.value, keyPriority)] = Pair(patchPair.first, patchPair.second)
            }
        }

        return output
    }
}