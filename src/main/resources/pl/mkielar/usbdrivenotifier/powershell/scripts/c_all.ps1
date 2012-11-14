$dp0 = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
. "$dp0/commons.ps1"

# Outputs info about all attached removable drives
WriteInfoAll $(Create-MessageData $C_CREATION_STATUS) $False