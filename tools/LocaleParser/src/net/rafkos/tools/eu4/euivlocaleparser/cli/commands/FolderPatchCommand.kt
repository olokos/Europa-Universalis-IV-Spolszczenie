package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.ProcessHelper
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import org.apache.logging.log4j.LogManager
import java.io.File
import java.util.*

object FolderPatchCommand : Command {
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

        if (!patch.isDirectory) {
            logger.error("Patch folder \"${patch.canonicalPath}\" does not exist or is a file.")
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
        val patch = File(args[1])
        val output = File(args[2])
        val inputLocales = ProcessHelper.loadDirectoryOfType(input, format)
        val patchLocales = ProcessHelper.loadDirectoryOfType(patch, format)
        val patchedLocales = ProcessHelper.patchLocales(inputLocales, patchLocales)
        ProcessHelper.writeLocalesToDirectory(output, patchedLocales)
    }

    override val help: String = "<input folder> <patch folder> <output folder> <locale type: yaml/eu4> - Patches input folder's locales with provided patches and writes the locales into the output folder. Both patch and input folders need to have the same provided format."
}