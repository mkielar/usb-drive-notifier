package pl.mkielar.usbdrivenotifier.model;

/**
 * Status of the removable drive, as returned by the {@link pl.mkielar.usbdrivenotifier.API) methods.
 * 
 * @author marcin.kielar
 *
 */
public enum USBDriveStatus {
	
	/** The removable drive has been attached to the workstation. */
	CREATION,
	
	/** The removable drive has been detached from the workstation. */
	DELETION;
}
