package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.Constants
import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.ProcessHelper
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import java.io.File

object CreateShortCommand : Command {
    override fun validForArguments(args: List<String>): Boolean {
        if (args.size != 3) {
            println("Incorrect number of arguments.")
            return false
        }

        val input = File(args[0])

        if (!input.isDirectory) {
            println("Input folder \"${input.canonicalPath}\" does not exist or is a file.")
            return false
        }

        val output = File(args[1])

        if (output.isDirectory) {
            println("Output file \"${output.canonicalPath}\" has the same name as folder in this directory.")
            return false
        }

        val format = args[2].toLowerCase()
        if (format != "eu4" && format != "euiv" && format != "yaml" && format != "yml") {
            println("Incorrect format type \"$format\". Should be one of the two: \"eu4\", \"yaml\".")
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
        val locales = ProcessHelper.loadDirectoryOfType(input, format)
        val filtered = ProcessHelper.filterLocales(locales, Constants.SHORT_FILES_FILTER)
        val merged = ProcessHelper.mergeLocales(filtered, output.name, format)
        LocaleLoader.writeToFile(output, merged)
    }

    override val help: String = "<input folder> <output file> <locale type: yaml/eu4> - Creates a single file with short strings based on input folder locales. Input folder has to be of specified type."
}