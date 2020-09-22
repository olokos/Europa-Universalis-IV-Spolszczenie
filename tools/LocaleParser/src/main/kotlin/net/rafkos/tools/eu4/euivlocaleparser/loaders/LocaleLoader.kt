package net.rafkos.tools.eu4.euivlocaleparser.loaders

import net.rafkos.tools.eu4.euivlocaleparser.*
import net.rafkos.tools.eu4.euivlocaleparser.validators.ValidatorEUIV
import net.rafkos.tools.eu4.euivlocaleparser.validators.ValidatorTransifex
import org.apache.commons.io.input.BOMInputStream
import org.apache.logging.log4j.LogManager
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * Purpose of this parser is to load locale file. The loader does not check syntax of text.
 */
object LocaleLoader {
    private val logger = LogManager.getLogger(this.javaClass)

    /**
     * Load provided file of specified type.
     * src - Source file.
     * type - Type of the locale file.
     */
    fun loadFile(src: File, type: LocaleType): Locale {
        logger.info("Loading locale file \"${src.canonicalPath}\" of type \"$type\".")
        LoaderUtils.validateFile(src)
        val defaultEncoding = "UTF-8"
        val inputStream = FileInputStream(src)
        inputStream.use {
            val stream = BOMInputStream(it)
            val reader = BufferedReader(
                    InputStreamReader(BufferedInputStream(stream),
                    if (stream.bom == null) defaultEncoding else stream.bom.charsetName))

            val locale = Locale(src.name, type)
            var language: PriorityString? = null
            var languagePriorityCounter = 0
            var keyPriorityCounter = 0
            while (reader.ready()) {
                var line = TextUtils.removeComments(reader.readLine(), type).trim()
                if (line == "")
                    continue

                if (LoaderUtils.isLanguageHeader(line)) {
                    language = PriorityString(LoaderUtils.readLanguageHeader(line), languagePriorityCounter++)
                    locale.entries[language] = hashMapOf()
                    continue
                } else if (language == null) {
                    logger.warn("Locale file should start with language header. Got \"$line\".")
                    language = PriorityString("NO_HEADER", languagePriorityCounter++)
                    if (!locale.entries.containsKey(language))
                        locale.entries[language] = hashMapOf()
                }

                val key = PriorityString(LoaderUtils.readEntryKey(line), keyPriorityCounter++)
                line = line.substring(key.value.length + 1, line.length)

                var num = 0
                if (type == LocaleType.EUIV) {
                    val testNum = LoaderUtils.readEntryNum(line)
                    if (testNum != null) {
                        line = line.substring(testNum.toString().length + 1, line.length)
                        num = testNum
                    }
                }
                val text = line.trim()
                if (type == LocaleType.EUIV) ValidatorEUIV.validate(text) else ValidatorTransifex.validate(text)
                locale.entries[language]!![key] = Pair(num, text)
            }
            logger.info("Finished loading locale file \"${src.canonicalPath}\".")
            return locale
        }
    }

    /**
     * Writes specified locale to
     */
    fun writeToFile(output: File, locale: Locale) {
        logger.info("Writing locale file \"${locale.fileName}\" to file \"${output.canonicalPath}\".")
        val outputStream = FileOutputStream(output)
        outputStream.use {
            val writer = OutputStreamWriter(BufferedOutputStream(outputStream), StandardCharsets.UTF_8)
            writer.write("\ufeff")
            locale.entries.keys.sortedBy { it.priority }.forEach { language ->
                if (language.value != "NO_HEADER")
                    writer.write("l_${language.value}:\n")
                else if (locale.type == LocaleType.YAML)
                    writer.write("l_NO_HEADER:\n")
                locale.entries[language]!!.keys.sortedBy { it.priority }.forEach { key ->
                    val pair = locale.entries[language]!![key]
                    when (locale.type) {
                        LocaleType.EUIV -> writer.write(" ${key.value}:${pair!!.first} ${pair.second}\n")
                        LocaleType.YAML -> writer.write(" ${key.value}: ${pair!!.second}\n")
                    }
                }
            }
            writer.close()
        }
    }
}