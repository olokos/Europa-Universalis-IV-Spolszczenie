package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.ProcessHelper
import java.io.File

object FolderExtractUntranslatedCommand : Command {
    override fun validForArguments(args: List<String>): Boolean {
        if (args.size != 4) {
            println("Incorrect number of arguments.")
            return false
        }
        val base = File(args[0])

        if (!base.isDirectory) {
            println("Base folder \"${base.canonicalPath}\" does not exist or is a file.")
            return false
        }

        val translation = File(args[1])

        if (!translation.isDirectory) {
            println("Translation folder \"${translation.canonicalPath}\" does not exist or is a file.")
            return false
        }

        val output = File(args[2])
        output.mkdirs()

        if (!output.isDirectory) {
            println("Output folder \"${output.canonicalPath}\" does not exist or is a file.")
            return false
        }

        if (base.canonicalPath == translation.canonicalPath || output.canonicalPath == base.canonicalPath || output.canonicalPath == translation.canonicalPath) {
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
        val base = File(args[0])
        val output = File(args[2])
        val translation = File(args[1])
        val baseLocales = ProcessHelper.loadDirectoryOfType(base, format)
        val translationLocales = ProcessHelper.loadDirectoryOfType(translation, format)

        val filteredLocales = ProcessHelper.filterLocales(
                baseLocales, translationLocales) { a, b -> a == b || b == "" || b == "\"\"" }

        ProcessHelper.writeLocalesToDirectory(output, filteredLocales)
    }

    override val help: String = "<base folder> <translation folder> <output folder> <locale type: yaml/eu4> - Collects strings from base folder file if not present (or empty) in the translation folder file. Outputs files with untranslated texts only (texts from base folder that do not appear in translation folder). Both base and translation folders need to contain files of the same provided format."
}