@echo off
:: SCRIPT SETTINGS
set charset="polish"
set parser_version="0.1.16"

rd /s /q translations_temp 2>nul
md translations_temp

echo Ten skrypt kopiuje pliki jezykowe z ".../EUIV/localisation/" oraz konwertuje je na format YAML. Przekonwertowane pliki zostana przeniesione do "translations/en/".
set /p source="Podaj sciezke do folderu localisation z EUIV: "
echo Konwertowanie plikow...


set pattern=*_english.*
for %%A in ("%source%\%pattern%") do copy "%%A" "translations_temp\\"
for %%A in ("translations_temp\\*") do (
	echo %%A
	for %%F in (%%A) do (
	call jrepl "#[a-zA-Z0-9_,:;.'() ?]*$"^
             "" /m /x /t "|" /f "%%F" /o -
	)
)
java -jar "tools\\LocaleParser\\bin\\LocaleParser-%parser_version%-SNAPSHOT.jar" "folder_to_yaml" "translations_temp\\" "translations\\en\\" %charset%

rd /s /q translations_temp

echo Koniec.
pause