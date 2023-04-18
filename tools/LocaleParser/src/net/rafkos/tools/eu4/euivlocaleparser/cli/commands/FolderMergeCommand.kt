package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.ProcessHelper
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.*

object FolderMergeCommand : Command {
    private val logger = LogManager.getLogger(this.javaClass)

    override fun validForArguments(args: List<String>): Boolean {
        if (args.size != 3) {
            logger.error("Incorrect number of arguments.")
            return false
        }
        val input = File(args[0])

        if (!input.isDirectory) {
            logger.error("Input folder \"${input.canonicalPath}\" does not exist or is a file.")
            return false
        }

        val output = File(args[1])

        if (output.isDirectory) {
            logger.error("Output file \"${output.canonicalPath}\" has the same name as folder in this directory.")
            return false
        }


        if (output.canonicalPath == input.canonicalPath) {
            logger.error("Provided paths cannot be the same.")
            return false
        }

        val format = args[2].lowercase(Locale.getDefault())
        if (format != "eu4" && format != "euiv" && format != "yaml" && format != "yml") {
            logger.error("Incorrect format type \"$format\". Should be one of the two: \"eu4\", \"yaml\".")
            return false
        }

        return true
    }

    override fun execute(args: List<String>) {
        val format = when (args[2]) {
            "eu4", "euiv" -> LocaleType.EUIV
            "yaml", "yml" -> LocaleType.YAML
            else -> throw IllegalArgumentException() // never happens
        }
        val input = File(args[0])
        val output = File(args[1])
        val inputLocales = ProcessHelper.loadDirectoryOfType(input, format)
        val mergedInput = ProcessHelper.mergeLocales(inputLocales, output.name, format)
        LocaleLoader.writeToFile(output, mergedInput)
    }

    override val help: String = "<input folder> <output file> <locale type: yaml/eu4> - Merges specified input folder's locale files into one and writes it to the output file. The input folder has to be of provided format. The output file will have the same format as input files."
}