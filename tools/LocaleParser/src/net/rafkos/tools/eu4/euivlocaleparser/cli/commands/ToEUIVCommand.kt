package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.charsets.Charsets
import net.rafkos.tools.eu4.euivlocaleparser.converters.LocaleConverter
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import java.io.File

object ToEUIVCommand : Command {
    override fun validForArguments(args: List<String>) = ToYamlCommand.validForArguments(args)

    override fun execute(args: List<String>) {
        val input = File(args[0])
        val output = File(args[1])
        val charset = Charsets.charsets[args[2]]
        val inputLocale = LocaleLoader.loadFile(input, LocaleType.YAML)
        val convertedLocale = LocaleConverter.convertToType(inputLocale, charset!!, LocaleType.EUIV)
        LocaleLoader.writeToFile(output, convertedLocale)
    }

    override val help: String = "<input file> <output file> <charset> - Converts YML locale file to special EUIV format."
}