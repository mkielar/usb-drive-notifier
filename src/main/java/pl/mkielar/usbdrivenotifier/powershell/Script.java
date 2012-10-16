package pl.mkielar.usbdrivenotifier.powershell;

import java.io.File;

import pl.mkielar.usbdrivenotifier.tools.Resource;

public enum Script implements Resource {
	CURRENT_DECIVES("c_all.ps1", "pl/mkielar/usbdrivenotifier/powershell/scripts/c_all.ps1"),
	LISTEN("c_loop.ps1", "pl/mkielar/usbdrivenotifier/powershell/scripts/c_loop.ps1"),
	COMMONS("commons.ps1", "pl/mkielar/usbdrivenotifier/powershell/scripts/commons.ps1");
	
	private File file;
	private String resourcePath;
	
	private Script(String fileName, String resourcePath) {
		this.file = new File(fileName);
		this.resourcePath = resourcePath;
	}
	
	public File getFile() {
		return file;
	}
	
	public String getResourcePath() {
		return resourcePath;
	}
}
