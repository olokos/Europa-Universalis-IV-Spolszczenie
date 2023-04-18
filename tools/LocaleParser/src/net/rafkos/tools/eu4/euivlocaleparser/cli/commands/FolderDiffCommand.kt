package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.ProcessHelper
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.*

object FolderDiffCommand : Command {
    private val logger = LogManager.getLogger(this.javaClass)

    override fun validForArguments(args: List<String>): Boolean {
        if (args.size != 4) {
            logger.error("Incorrect number of arguments.")
            return false
        }

        val input = File(args[0])

        if (!input.isDirectory) {
            logger.error("Input folder \"${input.canonicalPath}\" does not exist or is a file.")
            return false
        }

        val diff = File(args[1])

        if (!diff.isDirectory) {
            logger.error("Diff folder \"${diff.canonicalPath}\" does not exist or is a file.")
            return false
        }

        val output = File(args[2])
        output.mkdirs()

        if (!output.isDirectory) {
            logger.error("Output folder \"${output.canonicalPath}\" does not exist or is a file.")
            return false
        }

        val format = args[3].lowercase(Locale.getDefault())
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
        val diff = File(args[1])
        val output = File(args[2])

        val inputLocales = ProcessHelper.loadDirectoryOfType(input, format)
        val diffLocales = ProcessHelper.loadDirectoryOfType(diff, format)
        val outputLocales = ProcessHelper.filterLocales(diffLocales, inputLocales) {i, d -> i != d}
        ProcessHelper.writeLocalesToDirectory(output, outputLocales)
    }

    override val help: String = "<input folder> <diff folder> <output folder> <locale type: yaml/eu4> - Outputs differences between input and diff folders's locales and writes them into the output folder. Both diff and input folders need to have the same provided format."
}