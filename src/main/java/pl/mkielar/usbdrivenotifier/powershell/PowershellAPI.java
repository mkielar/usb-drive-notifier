package pl.mkielar.usbdrivenotifier.powershell;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import pl.mkielar.usbdrivenotifier.API;
import pl.mkielar.usbdrivenotifier.USBDriveInfoListener;
import pl.mkielar.usbdrivenotifier.model.USBDriveInfo;
import pl.mkielar.usbdrivenotifier.tools.Extractor;
import pl.mkielar.usbdrivenotifier.tools.IOTools;
import pl.mkielar.usbdrivenotifier.tools.ProcessTools;
import pl.mkielar.usbdrivenotifier.tools.ServiceLookup;
import pl.mkielar.usbdrivenotifier.tools.TempFileExtractor;

public class PowershellAPI implements API {

	/** Command template for running Powershell scripts. */
	private static final String POWERSHELL_COMMAND_TEMPLATE = "powershell -ExecutionPolicy Unrestricted -File {0}";
	
	/** Resource extractor. */
	private static final Extractor EXTRACTOR = ServiceLookup.lookup(Extractor.class, TempFileExtractor.class);

	/** Root of the extracted script files - this will be needed to execute Powershell scripts. */
	private static final File EXTRACTED_FILES_ROOT;
	
	static {
		try {
			EXTRACTED_FILES_ROOT = EXTRACTOR.extract(Script.values());
		} catch (IOException ex) {
			throw new Error("Error extracting Powershell scripts.", ex);
		}
	}
	
	@Override
	public List<USBDriveInfo> getCurrentDrives() throws IOException {
		
		Process process = createScriptProcess(Script.CURRENT_DECIVES);
		ProcessTools.ignoreStream(process.getErrorStream());
		ProcessTools.ignoreStream(process.getOutputStream());
		
		try {
			return PowershellUSBDriveInfoFactory.parse(process.getInputStream());
		} finally {
			IOTools.closeQuietly(process.getInputStream());
			
		}
		
	}

	@Override
	public Process listen(USBDriveInfoListener usbDriveInfoListener) throws IOException {
		
		Process process = createScriptProcess(Script.LISTEN);
		ProcessTools.ignoreStream(process.getErrorStream());
		ProcessTools.ignoreStream(process.getOutputStream());
		
		PowershellUSBDriveInfoFactory.parseAsync(process.getInputStream(), usbDriveInfoListener);
		
		return process;
		
	}
	
	private Process createScriptProcess(Script script) throws IOException {
		
		File scriptFile = new File(EXTRACTED_FILES_ROOT, script.getFile().getName());
		
		String command = MessageFormat.format(POWERSHELL_COMMAND_TEMPLATE, scriptFile.getAbsolutePath());
		System.out.println("Running: " + command);
		return Runtime.getRuntime().exec(command);
		
	}

}
