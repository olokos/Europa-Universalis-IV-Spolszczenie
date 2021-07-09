:: BUILDER SETTINGS
set language="pl"
set charset="polish"
set project_folder="Spolszczenie_EUIV_Community_Edition"
set parser_version="0.1.11"

:: LOGIC
echo "Starting conversion of translated files from Transifex into the EU4 format for workshop delivery and automated mod building."
echo "Removing temp directory - might say does not exist. That is normal, just a precaution."
rd /s /q temp
echo "Starting compiling latest source strings directory for EU4 format into the temp supply directory"
java -jar "tools\\LocaleParser\\bin\\LocaleParser-%parser_version%-SNAPSHOT.jar" "folder_supply" "translations\\%language%" "translations\\en" "temp\\supply" yaml


echo "Copy polish localisation file"
xcopy /s CL_files temp\\supply

echo "Delete space from empty key"
for %%F in (temp\supply\custom_localisation_l_english.yml) do (
call jrepl ": \q \q"^
         ": \q\q" /m /x /t "|" /f "temp\supply\custom_localisation_l_english.yml" /o -
)

pause
echo "Starting compiling latest translations to desired language - in this case - polish."
java -jar "tools\\LocaleParser\\bin\\LocaleParser-%parser_version%-SNAPSHOT.jar" "folder_to_eu4" "temp\\supply" "temp\\eu4" "%charset%"


cd "%project_folder%"
echo "Removing old localisations"
del /s /f /q localisation
mkdir localisation
cd ../
echo "Copying new localisation"
xcopy /s temp\\eu4 "%project_folder%\\localisation"
echo "Cleaning up temp directory."
rd /s /q temp
echo "Temp directory cleaned up, goodbye, have a nice day! :smile:
pause