# About
Europa Universalis IV locale parser. The purpose of this tool is to provide an easy and automated way of converting between EUIV's specific YML format and standard YML files. This tool also offers replacement of custom characters - e.g. Polish alphabet letters - to different characters supported by EUIV. Another offered functionality are functions for merging locale files in case of empty strings, this is useful when using Transifex. Please note that the tool does not support whole YML standard, it is only for locale specific files for EUIV.

# How to use
* Install Java 8+.
* Run the following command from command line:
 `java -jar LocaleParser-X.X.X-SNAPSHOT.jar`
* Follow displayed help text.

# How to compile
* Install JDK8.
* Install Maven.
* Clone the repository and run the following command from command line:
`mvn clean package`

# Compatibility
* EUIV
* CK2 (should work, not tested)
* CK3 (works, does not need charsets)
* Other Paradox games following the same file structure (???)
