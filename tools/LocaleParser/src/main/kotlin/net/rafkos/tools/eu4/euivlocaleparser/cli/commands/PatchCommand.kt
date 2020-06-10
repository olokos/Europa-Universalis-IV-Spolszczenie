package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.ProcessHelper
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import org.apache.logging.log4j.LogManager
import java.io.File

object PatchCommand : Command {
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

        val patch = File(args[1])

        if (patch.isDirectory) {
            logger.error("Patch file \"${patch.canonicalPath}\" has the same name as folder in this directory.")
            return false
        }

        val output = File(args[2])
        output.mkdirs()

        if (!output.isDirectory) {
            logger.error("Output folder \"${output.canonicalPath}\" does not exist or is a file.")
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
        val patch = File(args[1])
        val output = File(args[2])
        val inputLocales = ProcessHelper.loadDirectoryOfType(input, format)
        val patchLocale = LocaleLoader.loadFile(patch, format)
        val patchedLocales = ProcessHelper.patchLocales(inputLocales, patchLocale)
        ProcessHelper.writeLocalesToDirectory(output, patchedLocales)
    }

    override val help: String = "<input folder> <patch file> <output folder> <locale type: yaml/eu4> - Patches input folder's locales with provided patch file and writes the localess into the output folder. Both patch and input folder has to have the same provided format."
}