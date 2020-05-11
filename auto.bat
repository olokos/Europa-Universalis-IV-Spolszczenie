:: BUILDER SETTINGS
set language="pl"
set charset="polish"
set project_folder="Spolszczenie_EUIV_Community_Edition"
set parser_version="0.1.8"

:: LOGIC
echo "Converting translated files from Transifex into the EU4 format for workshop delivery and automated mod building."
echo "Removing temp directory - might say does not exist. That is normal, just a precaution."
rd /s /q temp
java -jar "tools\\LocaleParser\\bin\\LocaleParser-%parser_version%-SNAPSHOT.jar" "folder_supply" "translations\\%language%" "translations\\en" "temp\\supply" yaml
java -jar "tools\\LocaleParser\\bin\\LocaleParser-%parser_version%-SNAPSHOT.jar" "folder_to_eu4" "temp\\supply" "temp\\eu4" "%charset%"
cd "%project_folder%"
echo "Removing old localisations"
del /s /f /q localisation
mkdir localisation
cd ../
echo "Copying new localisation"
xcopy /s temp\\eu4 "%project_folder%\\localisation"
rd /s /q temp
pause