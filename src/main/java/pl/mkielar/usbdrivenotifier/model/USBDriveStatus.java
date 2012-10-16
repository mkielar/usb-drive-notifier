package pl.mkielar.usbdrivenotifier.model;

/**
<<<<<<< HEAD
 * Status of the removable drive, as returned by the {@link pl.mkielar.usbdrivenotifier.API} methods.
=======
 * Status of the removable drive, as returned by the {@link pl.mkielar.usbdrivenotifier.API) methods.
>>>>>>> 67a02091c5bc37c2666f907b93e9db8881b935c3
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
