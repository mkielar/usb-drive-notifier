package pl.mkielar.usbdrivenotifier;

import pl.mkielar.usbdrivenotifier.model.USBDriveInfo;

/**
 * Callback / listener interface for listening on removable drive events.
 * 
 * @author marcin.kielar
 * 
 */
public interface USBDriveInfoListener {

	/**
	 * Event handler fired by the {@link API} upon removable drive changes.
	 * 
	 * @param info
	 *            removable drive metadata
	 */
	void onEvent(USBDriveInfo info);
	
}
