$dp0 = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent
. "$dp0/commons.ps1"

# Wyrejestrowanie Event-Listenerów
Unregister $C_CREATION_EVENT_SI
Unregister $C_DELETION_EVENT_SI

# Udajemy, ¿e aktualnie pod³¹czone dyski "w³aœnie siê pojawi³y" i wypisujemy je przy starcie skryptu
# Robimy to przed zarejestrowaniem Event-Listenerów, ¿eby nie zg³aszaæ tych samych dysków dwukrotnie
$MessageData = $(Create-MessageData $C_CREATION_STATUS)

# Rejestrujemy Event-Listener powiadamiaj¹cy o zdarzeniach dodania / usuniêcia wymienialnych dysków logicznych 
Register $C_CREATION_EVENT_SI $C_CREATION_QUERY $C_CREATION_STATUS
Register $C_DELETION_EVENT_SI $C_DELETION_QUERY $C_DELETION_STATUS


# Blokujemy skrypt na oczekiwaniu na nieistniej¹ce zdarzenie
Wait-Event -SourceIdentifier $C_NON_EXISTENT_EVENT_SI