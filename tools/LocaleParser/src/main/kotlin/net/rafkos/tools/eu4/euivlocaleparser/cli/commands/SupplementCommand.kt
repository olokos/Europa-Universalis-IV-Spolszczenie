package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import java.io.File

object SupplementCommand : Command {
    override fun validForArguments(args: List<String>): Boolean {
        if (args.size != 4) {
            println("Incorrect number of arguments.")
            return false
        }
        val input = File(args[0])

        if (!input.isFile) {
            println("Input file \"${input.canonicalPath}\" does not exist or is a folder.")
            return false
        }

        val supplementary = File(args[1])

        if (!supplementary.isFile) {
            println("Supplementary file \"${supplementary.canonicalPath}\" does not exist or is a folder.")
            return false
        }

        val output = File(args[2])
        output.mkdirs()

        if (output.isDirectory) {
            println("Output file \"${output.canonicalPath}\" has the same name as folder in this directory.")
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

        val inputLocale = LocaleLoader.loadFile(input, format)
        val supplyLocale = LocaleLoader.loadFile(supplement, format)
        inputLocale.supplement(supplyLocale)
        LocaleLoader.writeToFile(output, inputLocale)
    }

    override val help: String = "<input file> <supply file> <output file> <locale type: yaml/eu4> - Replaces empty strings in input file with values from supplement file then writes to output file. Both input and supplement files need to be of the same provided format."
}