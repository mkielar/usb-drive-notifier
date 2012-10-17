Set-Variable C_CREATION_QUERY -Option Constant -Value "Select * FROM __InstanceCreationEvent WITHIN 1 WHERE TargetInstance ISA 'Win32_LogicalDisk' And TargetInstance.DriveType = '2'"
Set-Variable C_CREATION_EVENT_SI -Option Constant -Value "LD_CREATION-22190001293"
Set-Variable C_CREATION_STATUS -Option Constant -Value "CREATION"

Set-Variable C_DELETION_QUERY -Option Constant -Value "Select * FROM __InstanceDeletionEvent WITHIN 1 WHERE TargetInstance ISA 'Win32_LogicalDisk' And TargetInstance.DriveType = '2'"
Set-Variable C_DELETION_EVENT_SI -Option Constant -Value "LD_DELETION-22190001293"
Set-Variable C_DELETION_STATUS -Option Constant -Value "DELETION"

Set-Variable C_NON_EXISTENT_EVENT_SI -Option Constant -Value "NOT_EXISTING-22190001293"

# Creates a MetaData object
# $Status - Notification status
Function Create-MessageData($Status) {

	$MessageData = New-Object PSObject -Property @{
		DeviceStatus = $Status
	}
	
	$MessageData
}

# Outputs MetaData for given drive
# $LogicalDisk - WMI object representing logical disk
# $MessageData - Drive metadata
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

# Outputs MetaData for all currently attached drives
# $MessageData - Drive metadata
Function WriteInfoAll($MessageData) {
	
	$LogicalDisks = @(Get-WmiObject -Query "Select * From Win32_LogicalDisk Where DriveType='2'")
	
	ForEach ($LogicalDisk in $LogicalDisks) {
		WriteInfo $LogicalDisk $MessageData
	}
}

# Unregisters Event-Listener
# $SourceIdentifier - Source identifier given when registering
Function Unregister($SourceIdentifier) {

	$ExistingSubscriber = Get-EventSubscriber -SourceIdentifier $SourceIdentifier -WarningAction:SilentlyContinue -ErrorAction:SilentlyContinue
	If ($ExistingSubscriber -ne $Null) {
		$ExistingSubscriber | Unregister-Event
	}

}

# Registers an Event-Listener with given identifier and query
# $SourceIdentifier - Source Identifier 
# $query - WMI query
# $prefix - Prefix fot console output
Function Register($SourceIdentifier, $Query, $Status) {
	
	$MessageData = Create-MessageData($Status)

	$Dummy = Register-WMIEvent -Query $Query -SourceIdentifier $SourceIdentifier -MessageData $MessageData -Action { 
		
		$LogicalDisk = $Event.SourceEventArgs.NewEvent.TargetInstance
		$MessageData = $Event.MessageData
		
		WriteInfo $LogicalDisk $MessageData
	}
}