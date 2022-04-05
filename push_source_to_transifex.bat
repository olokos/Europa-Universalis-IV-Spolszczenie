echo "Pushing localisation files to Transifex"
:: Force push all translations
::Push source
tx.exe push -s
::Push translations
::tx.exe push -t -f --parallel
pause