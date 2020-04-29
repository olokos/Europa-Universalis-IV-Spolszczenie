echo "Converting translated files from Transifex into the EU4 format for workshop delivery."
java -jar LocaleParser-0.1.1-SNAPSHOT.jar folder_supply translations/pl translations/en translations/supplied_pl yaml
java -jar LocaleParser-0.1.1-SNAPSHOT.jar folder_to_eu4 translations/supplied_pl translations/supplied_pl_eu4 polish
pause