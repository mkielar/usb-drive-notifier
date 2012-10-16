Set-Variable C_CREATION_QUERY -Option Constant -Value "Select * FROM __InstanceCreationEvent WITHIN 1 WHERE TargetInstance ISA 'Win32_LogicalDisk' And TargetInstance.DriveType = '2'"
Set-Variable C_CREATION_EVENT_SI -Option Constant -Value "LD_CREATION-22190001293"
Set-Variable C_CREATION_STATUS -Option Constant -Value "CREATION"

Set-Variable C_DELETION_QUERY -Option Constant -Value "Select * FROM __InstanceDeletionEvent WITHIN 1 WHERE TargetInstance ISA 'Win32_LogicalDisk' And TargetInstance.DriveType = '2'"
Set-Variable C_DELETION_EVENT_SI -Option Constant -Value "LD_DELETION-22190001293"
Set-Variable C_DELETION_STATUS -Option Constant -Value "DELETION"

Set-Variable C_NON_EXISTENT_EVENT_SI -Option Constant -Value "NOT_EXISTING-22190001293"

# Funkcja tworzy obiekt MessageData
# $Status status powiadomienia
Function Create-MessageData($Status) {

	$MessageData = New-Object PSObject -Property @{
		DeviceStatus = $Status
	}
	
	$MessageData
}

# Funkcja wypisuje INFO dla podanego dysku i danych
# $LogicalDisk - dysk lokalny
# $MessageData - dane wiadomoœci
Function Global:WriteInfo($LogicalDisk, $MessageData) {

	if ($LogicalDisk -ne $Null) {

		$Partition = $(Get-WmiObject -Query "Associators of {Win32_LogicalDisk.DeviceID='$($LogicalDisk.DeviceID)'} WHERE ResultClass=Win32_DiskPartition")
		$Drive = $(Get-WmiObject -Query "Associators of {Win32_DiskPartition.DeviceID='$($Partition.DeviceID)'} WHERE ResultClass=Win32_DiskDrive")
		
		Write-Host "INFO-START"
		Write-Host "  STATUS       : $($MessageData.DeviceStatus)"
		Write-Host "  DRIVE        : $($LogicalDisk.DeviceID)"
		Write-Host "  LABEL        : $($LogicalDisk.VolumeName)"
		Write-Host "  NAME         : $($Drive.Caption)"
		Write-Host "  SERIALNUMBER : $($LogicalDisk.VolumeSerialNumber)"
		Write-Host "INFO-STOP"	
	}
}

# Funkcja wypisuje INFO dla wszystkich aktualnie dostêpnych
# $MessageData dane wiadomoœci
Function WriteInfoAll($MessageData) {
	
	$LogicalDisks = @(Get-WmiObject -Query "Select * From Win32_LogicalDisk Where DriveType='2'")
	
	ForEach ($LogicalDisk in $LogicalDisks) {
		WriteInfo $LogicalDisk $MessageData
	}
}



# Funkcja wyrejestrowuje dany Event-Listener
# $si - SourceIdentifier podany przy wywo³aniu RegisterWMIEvent
Function Unregister($si) {

	$ExistingSubscriber = Get-EventSubscriber -SourceIdentifier $si -WarningAction:SilentlyContinue -ErrorAction:SilentlyContinue
	If ($ExistingSubscriber -ne $Null) {
		$ExistingSubscriber | Unregister-Event
	}

}

# Funckja rejestruje Event-Listener o danym identyfikatorze, dla przekazanego zapytania
# $si - SourceIdentifier 
# $query - Zapytanie
# $prefix - Prefix do wyrzucania na konsolê
Function Register($SourceIdentifier, $Query, $Status) {
	
	$MessageData = Create-MessageData($Status)

	$Dummy = Register-WMIEvent -Query $Query -SourceIdentifier $SourceIdentifier -MessageData $MessageData -Action { 
		
		$LogicalDisk = $Event.SourceEventArgs.NewEvent.TargetInstance
		$MessageData = $Event.MessageData
		
		WriteInfo $LogicalDisk $MessageData
	}
}