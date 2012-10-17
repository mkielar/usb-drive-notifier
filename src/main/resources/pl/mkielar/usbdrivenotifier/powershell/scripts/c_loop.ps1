$dp0 = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
. "$dp0/commons.ps1"

# Unregistering Event-Listeners - in case there are any
Unregister $C_CREATION_EVENT_SI
Unregister $C_DELETION_EVENT_SI

# Pretend, that the drives "just appeared" and output them first.
## We do it before registering Event-Listeners, so they do not catch that
$MessageData = $(Create-MessageData $C_CREATION_STATUS)

# Register Event-Listeners for creation / deletion events
Register $C_CREATION_EVENT_SI $C_CREATION_QUERY $C_CREATION_STATUS
Register $C_DELETION_EVENT_SI $C_DELETION_QUERY $C_DELETION_STATUS

#Block the script on waiting for non-exsistent event
Wait-Event -SourceIdentifier $C_NON_EXISTENT_EVENT_SI