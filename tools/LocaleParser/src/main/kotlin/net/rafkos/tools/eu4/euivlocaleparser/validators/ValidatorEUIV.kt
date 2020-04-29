package net.rafkos.tools.eu4.euivlocaleparser.validators

import net.rafkos.tools.eu4.euivlocaleparser.TextUtils
import org.apache.logging.log4j.LogManager


object ValidatorEUIV : Validator {
    private val logger = LogManager.getLogger(this.javaClass)

    override fun validate(text: String) {
        TextUtils.quotesValidation(text)
    }
}