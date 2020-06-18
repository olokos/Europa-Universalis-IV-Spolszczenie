@echo off
:: SCRIPT SETTINGS
set charset="polish"
set parser_version="0.1.9"

::
echo Ten skrypt kopiuje pliki jezykowe z ".../EUIV/localisation/" oraz konwertuje je na format YAML. Przekonwertowane pliki zostana przeniesione do "translations/en/".
set /p source="Podaj sciezke do folderu localisation z EUIV: "
echo Konwertowanie plikow...

set pattern=*_english.*
for %%A in ("%source%\%pattern%") do java -jar "tools\\LocaleParser\\bin\\LocaleParser-%parser_version%-SNAPSHOT.jar" "to_yaml" "%%~A" "translations\\en\\%%~nxA" %charset%
goto EOF

echo Koniec.
pause