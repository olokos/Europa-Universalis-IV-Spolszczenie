package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.Locale
import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.ProcessHelper
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import org.apache.logging.log4j.LogManager
import java.io.File

object DiffCommand : Command {
    private val logger = LogManager.getLogger(this.javaClass)

    override fun validForArguments(args: List<String>): Boolean {
        if (args.size != 4) {
            logger.error("Incorrect number of arguments.")
            return false
        }
        val input = File(args[0])

        if (!input.isFile) {
            logger.error("Input file \"${input.canonicalPath}\" does not exist or is a folder.")
            return false
        }

        val diff = File(args[1])

        if (!diff.isFile) {
            logger.error("Diff file \"${diff.canonicalPath}\" does not exist or is a folder.")
            return false
        }

        val output = File(args[2])

        if (output.isDirectory) {
            logger.error("Output file \"${output.canonicalPath}\" has the same name as folder in this directory.")
            return false
        }

        if (input.canonicalPath == diff.canonicalPath || output.canonicalPath == input.canonicalPath || output.canonicalPath == diff.canonicalPath) {
            logger.error("At least one of the folders is duplicated. All folders must be different for safety reasons.")
            return false
        }

        val format = args[3].toLowerCase()
        if (format != "eu4" && format != "euiv" && format != "yaml" && format != "yml") {
            logger.error("Incorrect format type \"$format\". Should be one of the two: \"eu4\", \"yaml\".")
            return false
        }

        return true
    }

    override fun execute(args: List<String>) {
        val format = when (args[3]) {
            "eu4", "euiv" -> LocaleType.EUIV
            "yaml", "yml" -> LocaleType.YAML
            else -> throw IllegalArgumentException() // never happens
        }
        val input = File(args[0])
        val output = File(args[2])
        val diff = File(args[1])

        val inputLocale = LocaleLoader.loadFile(input, format)
        val diffLocale = LocaleLoader.loadFile(diff, format)
        val outputLocale = Locale(output.name, format)
        ProcessHelper.filterLocales(diffLocale, inputLocale, outputLocale) {i, d -> i != d}
        LocaleLoader.writeToFile(output, outputLocale)
    }

    override val help: String = "<input file> <diff file> <output file> <locale type: yaml/eu4> - Outputs differences between input and diff files then writes them to output file. Both input and diff files need to be of the same provided format."
}