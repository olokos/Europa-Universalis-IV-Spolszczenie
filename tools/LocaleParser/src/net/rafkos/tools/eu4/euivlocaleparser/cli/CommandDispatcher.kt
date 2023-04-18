package net.rafkos.tools.eu4.euivlocaleparser.cli

import net.rafkos.tools.eu4.euivlocaleparser.cli.commands.*

/**
 * Simple command dispatcher.
 */
object CommandDispatcher {
    private val commands = mapOf<String, Command>(
            "charsets" to CharsetsCommand,
            "to_yaml" to ToYamlCommand,
            "folder_to_yaml" to FolderToYamlCommand,
            "to_eu4" to ToEUIVCommand,
            "folder_to_eu4" to FolderToEUIVCommand,
            "supply" to SupplementCommand,
            "folder_supply" to FolderSupplementCommand,
            "extract_untranslated" to FolderExtractUntranslatedCommand,
            "folder_merge" to FolderMergeCommand,
            "patch" to PatchCommand,
            "folder_patch" to FolderPatchCommand,
            "create_short" to CreateShortCommand,
            "diff" to DiffCommand,
            "folder_diff" to FolderDiffCommand
    )

    fun processInput(args: Array<String>) {
        if (args.isEmpty()) {
            displayHelp()
            return
        }
        if (commands.containsKey(args[0])) {
            val command = commands[args[0]]
            val commandArgs = args.toMutableList()
            commandArgs.removeAt(0)

            if (command!!.validForArguments(commandArgs)) {
                command.execute(commandArgs)
            } else {
                displayHelp()
            }
        } else {
            println("Command \"${args[0]}\" does not exist.")
            displayHelp()
        }
    }

    private fun displayHelp() {
        println("Europa Universalis IV locale parser.\nThe purpose of this tool is to provide an easy and automated way of " +
                "converting between EUIV's specific YML format and standard YML files. This tool also offers replacement of custom characters " +
                "- e.g. Polish alphabet letters - to different characters supported by EUIV. Another offered functionality are functions for " +
                "merging locale files in case of empty strings, this is useful when using Transifex.\nPlease note that the tool does not" +
                "support whole YML standard, it is only for locale specific files for EUIV.\nby Rafal Kosyl, admin@rafkos.net\n")
        println("Commands:")
        println()
        commands.forEach { (name, cmd) ->
            println("$name ${cmd.help}")
            println()
        }
    }
}