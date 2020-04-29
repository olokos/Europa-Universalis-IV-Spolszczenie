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
    fun loadDirectoryOfType(directory: File, type: LocaleType): List<Locale> {
        if (!directory.isDirectory && directory.exists()) {
            logger.error("Source folder \"${directory.canonicalPath}\" is not a folder.")
            throw IllegalArgumentException()
        }

        val locales = mutableListOf<Locale>()

        for (file in directory.listFiles()!!) {
            if (file.isFile && (file.extension == "yml" || file.extension == "txt")) {
                val locale = LocaleLoader.loadFile(file, type)
                locales.add(locale)
            }
        }

        return locales
    }

    /**
     * Writes specified locales to given directory.
     */
    fun writeLocalesToDirectory(directory: File, locales: List<Locale>) {
        if (!directory.isDirectory && directory.exists()) {
            logger.error("Output folder \"${directory.canonicalPath}\" is not a folder.")
            throw IllegalArgumentException()
        }
        for (locale in locales) {
            val target = File(directory, locale.fileName)
            LocaleLoader.writeToFile(target, locale)
        }
    }

    /**
     * Converts locales with given charset to given type.
     */
    fun convertLocalesToType(locales: List<Locale>, charset: Charset, type: LocaleType): List<Locale> {
        val converted = mutableListOf<Locale>()
        for (locale in locales) {
            logger.info("Converting locale \"${locale.fileName}\" from type \"${locale.type}\" to \"${type}\" using charset \"${charset.javaClass.simpleName}\".")
            converted.add(LocaleConverter.convertToType(locale, charset, type))
        }
        return converted.toList()
    }

    /**
     * Combines two locale lists to replace empty strings with ones in supplement locales.
     * The locales are identified by their names.
     */
    fun combineLocales(locales: List<Locale>, supplementLocales: List<Locale>) {
        locales.forEach { locale ->
            supplementLocales.forEach { supplement ->
                if (locale.fileName == supplement.fileName) {
                    locale.supplement(supplement)
                }
            }
        }
    }
}