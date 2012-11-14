package pl.mkielar.usbdrivenotifier.powershell;

import java.io.File;

import pl.mkielar.usbdrivenotifier.tools.Resource;

/**
 * Definition of resources for powershell api.
 * 
 * @author marcin.kielar
 *
 */
public enum Script implements Resource {
	
	GET_PARENT_DEVICE("get-parent-device.exe", "pl/mkielar/usbdrivenotifier/powershell/scripts/get-parent-device.exe"),
	CURRENT_DECIVES("c_all.ps1", "pl/mkielar/usbdrivenotifier/powershell/scripts/c_all.ps1"),
	LISTEN("c_loop.ps1", "pl/mkielar/usbdrivenotifier/powershell/scripts/c_loop.ps1"),
	COMMONS("commons.ps1", "pl/mkielar/usbdrivenotifier/powershell/scripts/commons.ps1");
	
	/** Relative path to file after extraction. */
	private File file;
	
	/** Path to resource in classpath. */
	private String resourcePath;

	/**
	 * Constructor.
	 * 
	 * @param fileName
	 *            relative path to file after extraction
	 * @param resourcePath
	 *            path to resource in classpath
	 */
	private Script(String fileName, String resourcePath) {
		this.file = new File(fileName);
		this.resourcePath = resourcePath;
	}

	/**
	 * Returns relative file location after extraction.
	 * 
	 * @return relative file location after extraction.
	 */
	public File getFile() {
		return file;
	}
	
	/**
	 * Returns the path to resource in classpath.
	 * 
	 * @return path to resource in classpath
	 */
	public String getResourcePath() {
		return resourcePath;
	}
}
