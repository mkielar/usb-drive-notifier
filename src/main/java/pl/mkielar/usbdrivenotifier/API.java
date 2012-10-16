package pl.mkielar.usbdrivenotifier;

import java.io.IOException;
import java.util.List;

import pl.mkielar.usbdrivenotifier.model.USBDriveInfo;

public interface API {

	List<USBDriveInfo> getCurrentDrives() throws IOException;
	
	Process listen(USBDriveInfoListener usbDriveInfoListener) throws IOException;
	
}
