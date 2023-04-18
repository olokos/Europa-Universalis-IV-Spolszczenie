package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.*

object SupplementCommand : Command {
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

        val supplementary = File(args[1])

        if (!supplementary.isFile) {
            logger.error("Supplementary file \"${supplementary.canonicalPath}\" does not exist or is a folder.")
            return false
        }

        val output = File(args[2])

        if (output.isDirectory) {
            logger.error("Output file \"${output.canonicalPath}\" has the same name as folder in this directory.")
            return false
        }

        if (input.canonicalPath == supplementary.canonicalPath || output.canonicalPath == input.canonicalPath || output.canonicalPath == supplementary.canonicalPath) {
            logger.error("At least one of the folders is duplicated. All folders must be different for safety reasons.")
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
        val output = File(args[2])
        val supplement = File(args[1])

        val inputLocale = LocaleLoader.loadFile(input, format)
        val supplyLocale = LocaleLoader.loadFile(supplement, format)
        inputLocale.supplement(supplyLocale)
        LocaleLoader.writeToFile(output, inputLocale)
    }

    override val help: String = "<input file> <supply file> <output file> <locale type: yaml/eu4> - Replaces empty strings in input file with values from supplement file then writes to output file. Both input and supplement files need to be of the same provided format."
}