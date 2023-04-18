package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.Constants
import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.ProcessHelper
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import org.apache.logging.log4j.LogManager
import java.io.File

object CreateShortCommand : Command {
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

        val format = args[2].lowercase()
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
        val output1 = File("${output.canonicalPath}.0_30")
        val output2 = File("${output.canonicalPath}.31_60")
        val locales = ProcessHelper.loadDirectoryOfType(input, format)
        val filtered1 = ProcessHelper.filterLocales(locales, Constants.SHORT_FILES_FILTER)
        val filtered2 = ProcessHelper.filterLocales(locales, Constants.SHORT_FILES_FILTER_2)
        val merged1 = ProcessHelper.mergeLocales(filtered1, output1.name, format)
        val merged2 = ProcessHelper.mergeLocales(filtered2, output2.name, format)
        LocaleLoader.writeToFile(output1, merged1)
        LocaleLoader.writeToFile(output2, merged2)
    }

    override val help: String = "<input folder> <output file> <locale type: yaml/eu4> - Creates files with short strings based on input folder locales. Input folder has to be of specified type."
}