$dp0 = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
. "$dp0/commons.ps1"

# Wypisujemy wszystkie dost�pne dyski wymienne
WriteInfoAll $(Create-MessageData $C_CREATION_STATUS)