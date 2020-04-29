package net.rafkos.tools.eu4.euivlocaleparser.loaders

import net.rafkos.tools.eu4.euivlocaleparser.LoaderUtils
import net.rafkos.tools.eu4.euivlocaleparser.Locale
import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.TextUtils
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
            var language: String? = null
            while (reader.ready()) {
                var line = TextUtils.removeComments(reader.readLine()).trim()
                if (line == "")
                    continue

                if (LoaderUtils.isLanguageHeader(line)) {
                    language = LoaderUtils.readLanguageHeader(line)
                    locale.entries[language] = hashMapOf()
                    continue
                } else if (language == null) {
                    logger.warn("Locale file should start with language header. Got \"$line\".")
                    language = "NO_HEADER"
                    if (!locale.entries.containsKey(language))
                        locale.entries[language] = hashMapOf()
                }

                val key = LoaderUtils.readEntryKey(line)
                line = line.substring(key.length + 1, line.length)

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
            locale.entries.forEach { (language, entries) ->
                if (language != "NO_HEADER")
                    writer.write("l_$language:\r\n")
                entries.forEach { (key, pair) ->
                    when (locale.type) {
                        LocaleType.EUIV -> writer.write(" $key:${pair.first} ${pair.second}\r\n")
                        LocaleType.YAML -> writer.write(" $key: ${pair.second}\r\n")
                    }
                }
            }
            writer.close()
        }
    }
}