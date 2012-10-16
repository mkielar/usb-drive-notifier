package pl.mkielar.usbdrivenotifier.model;

/**
 * Removable drive event metadata.
 * 
 * @author marcin.kielar
 *
 */
public class USBDriveInfo {

	/** Drive status. */
	private final USBDriveStatus status;
	
	/** Drive letter. */
	private final String driveLetter;
	
	/** Drive label. */
	private final String label;
	
	/** Drive name as returned by the driver. */
	private final String name;
	
	/** Drive serial number. */
	private final String serialNumber;
	
	/**
	 * Constructor.
	 * 
	 * @param status
	 *            drive status
	 * @param driveLetter
	 *            drive letter
	 * @param label
	 *            drive label
	 * @param name
	 *            drive name as returned by the driver
	 * @param serialNumber
	 *            drive serial number
	 */
	public USBDriveInfo(USBDriveStatus status, String driveLetter, String label, String name, String serialNumber) {
		this.status = status;
		this.driveLetter = driveLetter;
		this.label = label;
		this.name = name;
		this.serialNumber = serialNumber;
	}

	/**
	 * Returns drive status.
	 * 
	 * @return drive status
	 */
	public USBDriveStatus getStatus() {
		return status;
	}

	/**
	 * Returns drive letter.
	 * 
	 * @return drive letter
	 */
	public String getDriveLetter() {
		return driveLetter;
	}

	/**
	 * Returns drive label.
	 * 
	 * @return drive label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * Returns drive name (as returned by the driver)
	 * 
	 * @return drive name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns drive serial number.
	 * 
	 * @return drive serial number
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	@Override
	public String toString() {
		return "USBDriveInfo [status=" + status + ", driveLetter="
				+ driveLetter + ", label=" + label + ", name=" + name
				+ ", serialNumber=" + serialNumber + "]";
	}

}
