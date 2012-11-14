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
	
	/** Device ID. */
	private final String deviceId;
	
	/** Drive Vendor ID. */
	private final String vendorId;
	
	/** Drive Product ID. */
	private final String productId;

	/** Drive serial number. */
	private final String serialNumber;
	
	/** Drive name as returned by the driver. */
	private final String name;
	
	/** Mount point / drive letter. */
	private final String mountPoint;
	
	/** Drive label. */
	private final String label;
	
	/**
	 * Constructor.
	 * 
	 * @param status
	 *            drive status
	 * @param deviceId
	 *            drive device id
	 * @param vendorId
	 *            drive vendor id
	 * @param product
	 *            id drive product id
	 * @param serialNumber
	 *            drive serial number
	 * @param name
	 *            drive name as returned by the driver
	 * @param mountPoint
	 *            mount point / drive letter
	 * @param label
	 *            drive label
	 */
	public USBDriveInfo(USBDriveStatus status, String deviceId, String vendorId, String productId, String serialNumber, String name, String mountPoint, String label) {
		this.status = status;
		this.deviceId = deviceId;
		this.vendorId = vendorId;
		this.productId = productId;
		this.serialNumber = serialNumber;
		this.name = name;
		this.mountPoint = mountPoint;
		this.label = label;
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
	 * Redurns drive deviceId.
	 * 
	 * @return reviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * Returns drive vendorId.
	 * 
	 * @return vendorId
	 */
	public String getVendorId() {
		return vendorId;
	}

	/**
	 * Returns drive productId.
	 * 
	 * @return productId
	 */
	public String getProductId() {
		return productId;
	}

	/**
	 * Returns drive serial number.
	 * 
	 * @return drive serial number
	 */
	public String getSerialNumber() {
		return serialNumber;
	}

	/**
	 * Returns drive name (as returned by the driver).
	 * 
	 * @return drive name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the mount point / drive letter.
	 * 
	 * @return mount point / drive letter
	 */
	public String getMountPoint() {
		return mountPoint;
	}

	/**
	 * Returns drive label.
	 * 
	 * @return drive label
	 */
	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return "USBDriveInfo [status=" + status + ", deviceId=" + deviceId
				+ ", vendorId=" + vendorId + ", productId=" + productId
				+ ", serialNumber=" + serialNumber + ", name=" + name
				+ ", mountPoint=" + mountPoint + ", label=" + label + "]";
	}


}
