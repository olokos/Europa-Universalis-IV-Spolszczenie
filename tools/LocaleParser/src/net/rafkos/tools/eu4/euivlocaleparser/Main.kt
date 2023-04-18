package net.rafkos.tools.eu4.euivlocaleparser

import net.rafkos.tools.eu4.euivlocaleparser.cli.CommandDispatcher.processInput
import org.apache.logging.log4j.core.config.ConfigurationFactory

open class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            ConfigurationFactory.newConfigurationBuilder().build()
            processInput(args)
        }
    }
}