$Global:dp0 = Split-Path -Path $MyInvocation.MyCommand.Definition -Parent

Set-Variable C_CREATION_QUERY -Option Constant -Value "Select * FROM __InstanceCreationEvent WITHIN 1 WHERE TargetInstance ISA 'Win32_LogicalDisk' And TargetInstance.DriveType = '2'"
Set-Variable C_CREATION_EVENT_SI -Option Constant -Value "LD_CREATION-22190001293"
Set-Variable C_CREATION_STATUS -Option Constant -Value "CREATION"

Set-Variable C_DELETION_QUERY -Option Constant -Value "Select * FROM __InstanceDeletionEvent WITHIN 1 WHERE TargetInstance ISA 'Win32_LogicalDisk' And TargetInstance.DriveType = '2'"
Set-Variable C_DELETION_EVENT_SI -Option Constant -Value "LD_DELETION-22190001293"
Set-Variable C_DELETION_STATUS -Option Constant -Value "DELETION"

Set-Variable C_NON_EXISTENT_EVENT_SI -Option Constant -Value "NOT_EXISTING-22190001293"

Set-Variable C_USB_DEVICE_ID_PATTERN_EXT -Option Constant -Value "USB\\VID_(\w+)&PID_(\w+)\\(?!.*[&_].*)(\w+)"
Set-Variable C_USB_DEVICE_ID_PATTERN_INT -Option Constant -Value "USB\\VID_(.+)&PID_(.+)\\(.+)"

# Global array of currently attached drives
$Global:DRIVES = @{}

# Creates a MetaData object
# $Status - Notification status
Function Create-MessageData($Status) {

	$MessageData = New-Object PSObject -Property @{
		DeviceStatus = $Status
	}
	
	$MessageData
}

# Creates Notification object
# $MessageData - message data
# $DiskInfo - disk Info
Function Global:Create-Notification($MessageData, $DiskInfo) {

	$Notification = New-Object PSObject -Property @{
		MessageData = $MessageData
		DiskInfo = $DiskInfo
	}
	
	$Notification
}

# Creates USB disk information record
# $LogicalDisk - Local disk
Function Global:Create-DiskInfo($LogicalDisk) {

	Try {
	
		$Partition = $(Get-WmiObject -ErrorAction SilentlyContinue -Query "Associators of {Win32_LogicalDisk.DeviceID='$($LogicalDisk.DeviceID)'} WHERE Role=Dependent AssocClass=Win32_LogicalDiskToPartition ResultClass=Win32_DiskPartition")
		$Drive = $(Get-WmiObject -ErrorAction SilentlyContinue -Query "Associators of {Win32_DiskPartition.DeviceID='$($Partition.DeviceID)'} WHERE Role=Dependent AssocClass=Win32_DiskDriveToDiskPartition ResultClass=Win32_DiskDrive")
		$PnpEntity = $(Get-WmiObject -ErrorAction SilentlyContinue -Query "Associators of {Win32_DiskDrive.DeviceID='$($Drive.DeviceID)'} WHERE ResultRole=SystemElement AssocClass=Win32_PnpDevice ResultClass=Win32_PnPEntity")
		
		# FIXED: We don't need this.
		# With two Pendrive connected, there are two USBSTOR objects under the $UsbController - there's no way to match $PnpEntity to $Usb using just WMI.
		# $UsbController = $(Get-WmiObject -Query "Associators of {Win32_PnPEntity.DeviceID='$($PnpEntity.DeviceID)'} WHERE Role=Dependent AssocClass=Win32_UsbControllerDevice ResultClass=Win32_UsbController")
		# $Usb = $(Get-WmiObject -Query "Associators of {Win32_UsbController.DeviceID='$($UsbController.DeviceID)'} WHERE ResultRole=Dependent AssocClass=Win32_UsbControllerDevice ResultClass=Win32_PnPEntity" | where {$_.service -eq 'USBSTOR'})

		If ($PnpEntity -ne $Null) {
		
			# Instead, we use an external tool written specifically for this task - see https://github.com/mkielar/get-parent-device
			$Command =  '"' + $Global:dp0 + '\get-parent-device.exe" "' + $PnpEntity.DeviceID + '" "' + $C_USB_DEVICE_ID_PATTERN_EXT + '"'
			
			# Invoke the command and get output - external tool returns the Device Instance ID of matching PnpEntity - exactly what we need to get Vendor ID / Product ID / Serial Number
			$UsbDeviceID = cmd.exe /c `"$Command`"
			
			# Clear match result
			$Matches = $Null
			
			# Match Device Instance ID
			$Found = $UsbDeviceID -match $C_USB_DEVICE_ID_PATTERN_INT
			
			If ($Found) {
				$VendorId = $Matches[1]
				$ProductId = $Matches[2]
				$SerialNumber = $Matches[3]
			}

			# Win32_DiskDrive can have multiple partitions, and thus multiple logical disk might exist.
			# However, Windows can only recognize the first partition on each removable drive, and that guarantees we'll have only one Win32_LogicalDisk entry per USB Drive.
			# If this ever changes, major changes to the script will be needed, or it will report multiple records for a single USB drive.
			
			$DiskInfo = New-Object PSObject -Property @{
				DeviceID = $UsbDeviceID
				VendorID = $VendorID
				ProductID = $ProductID
				SerialNumber = $SerialNumber
				Name = $Drive.Caption
				MountPoint = $LogicalDisk.DeviceID
				Label = $LogicalDisk.VolumeName
			}

			$DiskInfo
		} Else {
			$Null
		}
		
	} Catch {
		
		Write-Host "ERROR: Error creating disk info: $($error[0])"
		
		$Null
		
	}
}

# Outputs MetaData for given drive
# $LogicalDisk - WMI object representing logical disk
# $MessageData - Drive metadata
Function Global:WriteInfo($Notification) {

	If ($Notification -ne $Null) {

		$MessageData = $Notification.MessageData
		$DiskInfo = $Notification.DiskInfo
		
		Write-Host "INFO-START"
		Write-Host "  STATUS       : $($MessageData.DeviceStatus)"
		Write-Host "  DEVICEID     : $($DiskInfo.DeviceID)"
		Write-Host "  VENDORID     : $($DiskInfo.VendorId)"
		Write-Host "  PRODUCTID    : $($DiskInfo.ProductId)"
		Write-Host "  SERIALNUMBER : $($DiskInfo.SerialNumber)"
		Write-Host "  NAME         : $($DiskInfo.Name)"
		Write-Host "  MOUNTPOINT   : $($DiskInfo.MountPoint)"	
		Write-Host "  LABEL        : $($DiskInfo.Label)"	
		Write-Host "INFO-STOP"	
		
	}
}

# Outputs MetaData for all currently attached drives
# $MessageData - Drive metadata
Function WriteInfoAll($MessageData, $RegisterRemoveEvents) {
	
	$LogicalDisks = @(Get-WmiObject -Query "Select * From Win32_LogicalDisk Where DriveType='2'")
	
	ForEach ($LogicalDisk in $LogicalDisks) {
	
		$DiskInfo = Create-DiskInfo $LogicalDisk
		
		If ($DiskInfo -ne $Null) {
			$Notification = Create-Notification $MessageData $DiskInfo
		
			WriteInfo $Notification
			
			If ($RegisterRemoveEvents -eq $True) {
			
				$DRIVES[$LogicalDisk.DeviceID] = $Notification
			
			}
		}
	}
}

# Unregisters Event-Listener
# $SourceIdentifier - Source identifier given when registering
Function Global:Unregister($SourceIdentifier) {

	$ExistingSubscriber = Get-EventSubscriber -SourceIdentifier $SourceIdentifier -WarningAction:SilentlyContinue -ErrorAction:SilentlyContinue
	If ($ExistingSubscriber -ne $Null) {
		$ExistingSubscriber | Unregister-Event
	}

}

# Registers an Event-Listener with given identifier and query
# $SourceIdentifier - Source Identifier 
Function Register-Create {
	
	$MessageData = Create-MessageData $C_CREATION_STATUS

	$Dummy = Register-WMIEvent -Query $C_CREATION_QUERY -SourceIdentifier $C_CREATION_EVENT_SI -MessageData $MessageData -Action { 
		
		Try {
		
			$MessageData = $Event.MessageData
			
			$LogicalDisk = $Event.SourceEventArgs.NewEvent.TargetInstance
			$DiskInfo = Create-DiskInfo($LogicalDisk)

			If ($DiskInfo -ne $Null) {

				$Notification = Create-Notification $MessageData $DiskInfo
				
				WriteInfo $Notification
				
				# Add drive to storage
				$DRIVES[$LogicalDisk.DeviceID] = $Notification
			}
			
		} Catch {
			Write-Host "ERROR: Error handling creation event: $($error[0])"
		}
	}
}

Function Global:Register-Remove {

	$MessageData = Create-MessageData $C_DELETION_STATUS
	
	Register-WMIEvent -Query $C_DELETION_QUERY -SourceIdentifier $C_DELETION_EVENT_SI -MessageData $MessageData -Action {

		Try {
			
			$MessageData = $Event.MessageData
			
			$LogicalDisk = $Event.SourceEventArgs.NewEvent.TargetInstance
			
			# Get notification from hash
			$Notification = $DRIVES[$LogicalDisk.DeviceID]
			
			If ($Notification -ne $Null) {
			
				# Switch notification status to DELETION
				$Notification.MessageData = $MessageData
				
				WriteInfo $Notification
				
				# Remove drive from storage
				$DRIVES.Remove($LogicalDisk.DeviceID)
			}
			
		} Catch {
			Write-Host "ERROR: Error handling removal event: $($error[0])"
		}
	}

}
