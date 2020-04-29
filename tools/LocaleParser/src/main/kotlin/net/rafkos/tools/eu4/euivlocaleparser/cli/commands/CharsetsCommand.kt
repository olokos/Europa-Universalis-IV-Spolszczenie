package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

import net.rafkos.tools.eu4.euivlocaleparser.charsets.Charsets

object CharsetsCommand : Command {
    override fun validForArguments(args: List<String>) = args.isEmpty()

    override fun execute(args: List<String>) {
        println("Available charsets:")
        Charsets.charsets.forEach { (name, _) -> println(name) }
    }

    override val help = "- Returns a list of available charsets for special symbols support."
}