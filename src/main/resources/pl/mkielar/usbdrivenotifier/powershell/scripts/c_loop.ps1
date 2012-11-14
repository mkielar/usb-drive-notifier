$dp0 = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
. "$dp0/commons.ps1"

# Unregistering Event-Listeners - in case there are any
Unregister $C_CREATION_EVENT_SI
Unregister $C_DELETION_EVENT_SI

# Pretend, that the drives "just appeared" and output them first.
## We do it before registering Event-Listeners, so they do not catch that
[void](WriteInfoAll $(Create-MessageData $C_CREATION_STATUS) $True)

# Register Event-Listeners for creation / deletion events
[void](Register-Create)
[void](Register-Remove)

#Block the script on waiting for non-exsistent event
Wait-Event -SourceIdentifier $C_NON_EXISTENT_EVENT_SI