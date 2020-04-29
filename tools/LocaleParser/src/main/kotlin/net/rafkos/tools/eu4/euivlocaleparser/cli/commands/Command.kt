package net.rafkos.tools.eu4.euivlocaleparser.cli.commands

interface Command {
    fun validForArguments(args: List<String>): Boolean
    fun execute(args: List<String>)
    val help: String
}