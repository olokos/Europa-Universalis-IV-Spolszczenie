echo "Pushing localisation files to Transifex"
:: Force push all translations
::Push source
tx.exe push -s --parallel
::Push translations
::tx.exe push -t -f --parallel
pause