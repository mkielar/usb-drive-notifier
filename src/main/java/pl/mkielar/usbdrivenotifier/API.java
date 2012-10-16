package pl.mkielar.usbdrivenotifier;

import java.io.IOException;
import java.util.List;

import pl.mkielar.usbdrivenotifier.model.USBDriveInfo;

/**
 * An Interface for USB related API - in case someone, someday would want to
 * write something else than the default implementation.
 * 
 * @author marcin.kielar
 * 
 * @see pl.mkielar.usbdrivenotifier.powershell.PowershellAPI
 */
public interface API {

	/**
	 * Returns list of currently attached removable drives metadata. This method
	 * should return an empty list if there is no removable drives attached.
	 * 
	 * 
	 * @return list of currently attached removable drives metadata
	 * @throws IOException
	 *             when an IO error occurs accessing drive info
	 */
	List<USBDriveInfo> getCurrentDrives() throws IOException;

	/**
	 * Starts a listener, which <em>listens</em> for removable drive events from
	 * underlying implementation. Once event is detected, the
	 * {@code USBDriveInfoListener} is notified. The {@link Listener}
	 * instance is returned in order to allow terminating execution of the
	 * listener.
	 * 
	 * @param usbDriveInfoListener
	 *            listener to notify about drive events
	 * @return {@link Listener} instance of the listener
	 * @throws IOException
	 *             when an IO error occurs accessing drive info
	 */
	Listener listen(USBDriveInfoListener usbDriveInfoListener)
			throws IOException;

}
