package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.LocaleType
import net.rafkos.tools.eu4.euivlocaleparser.charsets.Charsets
import net.rafkos.tools.eu4.euivlocaleparser.converters.LocaleConverter
import net.rafkos.tools.eu4.euivlocaleparser.loaders.LocaleLoader
import java.io.File

object ToYamlCommand : Command {
    override fun validForArguments(args: List<String>): Boolean {
        if (args.size != 3) {
            println("Incorrect number of arguments.")
            return false
        }
        val input = File(args[0])

        if (!input.isFile) {
            println("Input file \"${input.canonicalPath}\" does not exist or is not a file.")
            return false
        }

        val output = File(args[1])
        output.mkdirs()

        if (output.isDirectory) {
            println("Output file \"${output.canonicalPath}\" has the same name as folder in this directory.")
            return false
        }

        if (input.canonicalPath == output.canonicalPath) {
            println("Input and output files must be different.")
            return false
        }

        if (!Charsets.charsets.containsKey(args[2])) {
            println("Incorrect charset provided \"${args[2]}\".")
            return false
        }
        val charset = Charsets.charsets[args[2]]

        return true
    }

    override fun execute(args: List<String>) {
        val input = File(args[0])
        val output = File(args[1])
        val charset = Charsets.charsets[args[2]]
        val inputLocale = LocaleLoader.loadFile(input, LocaleType.EUIV)
        val convertedLocale = LocaleConverter.convertToType(inputLocale, charset!!, LocaleType.YAML)
        LocaleLoader.writeToFile(output, convertedLocale)
    }

    override val help: String = "<input file> <output file> <charset> - Converts EUIV locale file to standard YML format."

}