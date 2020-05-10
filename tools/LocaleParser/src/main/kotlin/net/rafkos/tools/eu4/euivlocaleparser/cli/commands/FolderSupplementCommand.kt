package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.ProcessHelper
import java.io.File

object FolderSupplementCommand : Command {
    override fun validForArguments(args: List<String>): Boolean {
        if (args.size != 4) {
            println("Incorrect number of arguments.")
            return false
        }
        val input = File(args[0])

        if (!input.isDirectory) {
            println("Input folder \"${input.canonicalPath}\" does not exist or is a file.")
            return false
        }

        val supplementary = File(args[1])

        if (!supplementary.isDirectory) {
            println("Supplementary folder \"${supplementary.canonicalPath}\" does not exist or is a file.")
            return false
        }

        val output = File(args[2])
        output.mkdirs()

        if (!output.isDirectory) {
            println("Output folder \"${output.canonicalPath}\" does not exist or is a file.")
            return false
        }

        if (input.canonicalPath == supplementary.canonicalPath || output.canonicalPath == input.canonicalPath || output.canonicalPath == supplementary.canonicalPath) {
            println("At least one of the folders is duplicated. All folders must be different for safety reasons.")
            return false
        }

        val format = args[3].toLowerCase()
        if (format != "eu4" && format != "euiv" && format != "yaml" && format != "yml") {
            println("Incorrect format type \"$format\". Should be one of the two: \"eu4\", \"yaml\".")
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
        val supplement = File(args[1])
        val inputLocales = ProcessHelper.loadDirectoryOfType(input, format)
        val supplementaryLocales = ProcessHelper.loadDirectoryOfType(supplement, format)
        ProcessHelper.combineLocales(inputLocales, supplementaryLocales)
        ProcessHelper.writeLocalesToDirectory(output, inputLocales)
    }

    override val help: String = "<input folder> <supply folder> <output folder> <locale type: yaml/eu4> - Replaces empty strings in .yml files inside input folder with values from corresponding .yml files from supply folder then writes them to output folder. Both input and supplement folders need to contain files of the same provided format."
}