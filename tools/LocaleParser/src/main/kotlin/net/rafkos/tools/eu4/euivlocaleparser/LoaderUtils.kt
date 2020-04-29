package net.rafkos.tools.eu4.euivlocaleparser

import org.apache.logging.log4j.LogManager
import java.io.File

object LoaderUtils {
    private val logger = LogManager.getLogger(this.javaClass)

    /**
     * Reads and returns file language.
     */
    fun readLanguageHeader(line: String): String {
        if (line.length < 4) {
            logger.error("Incorrect file language header. Line should have the following pattern: \"l_language:\". Got \"$line\". Header too short.")
        }
        if (line.startsWith("l_")) {
            var language = ""
            for (index in 2 until line.length) {
                if (index == line.lastIndex) {
                    if (line[index] != ':') {
                        logger.error("Incorrect file language header. Line should end with \":\". Got \"$line\".")
                    }
                } else {
                    language += line[index]
                }
            }
            return language
        } else {
            logger.error("Incorrect file language header. Line should start with \"l_\". Got \"$line\".")
            throw IllegalArgumentException()
        }
    }

    /**
     * Reads and returns entry key. Supports both EUIV and Yaml versions.
     */
    fun readEntryKey(line: String): String {
        var key = ""
        var trimmedLine = line.trim()
        loop@ for (index in trimmedLine.indices) {
            val c = trimmedLine[index]
            if (c == ':')
                break
            if (index == trimmedLine.lastIndex) {
                logger.error("Key must end with \":\" symbol.")
                throw IllegalArgumentException()
            }
            if(Constants.ACCEPTED_KEY_CHARS.matcher("$c").matches()) {
                key += c
            } else {
                logger.error("Incorrect char: \"$c\" was found in key. Keys should contain chars of specific regex: \"${Constants.ACCEPTED_KEY_CHARS}\".")
                throw IllegalArgumentException()
            }
        }

        if (key == "") {
            logger.error("Key must not be empty.")
            throw IllegalArgumentException()
        }
        return key
    }

    /**
     * Reads and returns number next to the entry. Returns 0 if number not found. Supports EUIV version only.
     */
    fun readEntryNum(line: String): Int? {
        var number = ""
        for (index in line.indices) {
            val c = line[index]
            if (c.isDigit()) {
                number += c
            } else {
                break
            }
        }

        return number.toIntOrNull()
    }

    /**
     * Validates if provided file exists.
     */
    fun validateFile(file: File) {
        if (!file.exists()) {
            logger.error("File does not exist: \"${file.canonicalPath}\".")
            throw IllegalArgumentException()
        }
        if (!file.isFile) {
            logger.error("File is a folder: \"${file.canonicalPath}\".")
            throw IllegalArgumentException()
        }
    }

    /**
     * Returns true if line is language header.
     */
    fun isLanguageHeader(line: String): Boolean {
        var correctSymbols = true
        for (index in 0 until line.lastIndex) {
            val c = line[index]
            if (!Constants.ACCEPTED_KEY_CHARS.matcher("$c").matches()) {
                correctSymbols = false
                break
            }
        }
        return line.startsWith("l_") && line.endsWith(":")  && correctSymbols
    }
}