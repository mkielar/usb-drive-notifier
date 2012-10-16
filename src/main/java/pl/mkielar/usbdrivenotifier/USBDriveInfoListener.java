package pl.mkielar.usbdrivenotifier;

import pl.mkielar.usbdrivenotifier.model.USBDriveInfo;

public interface USBDriveInfoListener {
	
	void onEvent(USBDriveInfo info);
	
}
