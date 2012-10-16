$dp0 = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
. "$dp0/commons.ps1"

# Wyrejestrowanie Event-Listener�w
Unregister $C_CREATION_EVENT_SI
Unregister $C_DELETION_EVENT_SI

# Udajemy, �e aktualnie pod��czone dyski "w�a�nie si� pojawi�y" i wypisujemy je przy starcie skryptu
# Robimy to przed zarejestrowaniem Event-Listener�w, �eby nie zg�asza� tych samych dysk�w dwukrotnie
$MessageData = $(Create-MessageData $C_CREATION_STATUS)

# Rejestrujemy Event-Listener powiadamiaj�cy o zdarzeniach dodania / usuni�cia wymienialnych dysk�w logicznych 
Register $C_CREATION_EVENT_SI $C_CREATION_QUERY $C_CREATION_STATUS
Register $C_DELETION_EVENT_SI $C_DELETION_QUERY $C_DELETION_STATUS


# Blokujemy skrypt na oczekiwaniu na nieistniej�ce zdarzenie
Wait-Event -SourceIdentifier $C_NON_EXISTENT_EVENT_SI