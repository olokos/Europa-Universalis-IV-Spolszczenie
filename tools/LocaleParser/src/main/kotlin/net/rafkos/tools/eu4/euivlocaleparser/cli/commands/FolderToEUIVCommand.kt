package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.ProcessHelper
import net.rafkos.tools.eu4.euivlocaleparser.charsets.Charsets
import java.io.File

object FolderToEUIVCommand : Command {
    override fun validForArguments(args: List<String>): Boolean = FolderToYamlCommand.validForArguments(args)

    override fun execute(args: List<String>) {
        val input = File(args[0])
        val output = File(args[1])
        val charset = Charsets.charsets[args[2]]
        val locales = ProcessHelper.loadDirectoryOfType(input, LocaleType.YAML)
        val convertedLocales = ProcessHelper.convertLocalesToType(locales, charset!!, LocaleType.EUIV)
        ProcessHelper.writeLocalesToDirectory(output, convertedLocales)
    }

    override val help: String = "<input folder> <output folder> <charset> - Converts .yml files inside input directory from standard YML format to special EUIV format and writes them to output directory."
}