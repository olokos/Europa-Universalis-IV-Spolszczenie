package net.rafkos.tools.eu4.euivlocaleparser

object SpecialFilenames {
    val eu4ToYaml = hashMapOf(
        "missing_correct_english_tut_hint.txt" to "missing_correct_english_tut_hint.yml"
    )
    val yamlToeu4 = hashMapOf<String, String>().also { eu4ToYaml.forEach { (eu4, yaml) -> it[yaml] = eu4 } }

    fun getFilename(name: String, ofFormat: LocaleType) = when (ofFormat) {
        LocaleType.YAML -> yamlToeu4[name] ?: name
        LocaleType.EUIV -> eu4ToYaml[name] ?: name
    }
}