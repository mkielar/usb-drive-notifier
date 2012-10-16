package pl.mkielar.usbdrivenotifier.model;

public class USBDriveInfo {

	private final USBDriveStatus status;
	private final String driveLetter;
	private final String label;
	private final String name;
	private final String serialNumber;
	
	public USBDriveInfo(USBDriveStatus status, String driveLetter, String label, String name, String serialNumber) {
		this.status = status;
		this.driveLetter = driveLetter;
		this.label = label;
		this.name = name;
		this.serialNumber = serialNumber;
	}

	public USBDriveStatus getStatus() {
		return status;
	}
	public String getDriveLetter() {
		return driveLetter;
	}
	public String getLabel() {
		return label;
	}
	public String getName() {
		return name;
	}
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
