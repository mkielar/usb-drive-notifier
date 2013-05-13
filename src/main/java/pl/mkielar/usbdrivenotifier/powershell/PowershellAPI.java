package pl.mkielar.usbdrivenotifier.powershell;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import pl.mkielar.usbdrivenotifier.API;
import pl.mkielar.usbdrivenotifier.Listener;
import pl.mkielar.usbdrivenotifier.USBDriveInfoListener;
import pl.mkielar.usbdrivenotifier.model.USBDriveInfo;
import pl.mkielar.usbdrivenotifier.powershell.tools.ProcessTools;
import pl.mkielar.usbdrivenotifier.tools.Extractor;
import pl.mkielar.usbdrivenotifier.tools.IOTools;
import pl.mkielar.usbdrivenotifier.tools.ServiceLookup;
import pl.mkielar.usbdrivenotifier.tools.TempFileExtractor;

public class PowershellAPI implements API {

	/** Command template for running Powershell scripts. */
	private static final String POWERSHELL_COMMAND_TEMPLATE = "powershell -ExecutionPolicy Unrestricted -File {0}";
	
	/** Resource extractor. */
	private static final Extractor EXTRACTOR = ServiceLookup.lookup(Extractor.class, TempFileExtractor.class);

	/** Root of the extracted script files - this will be needed to execute Powershell scripts. */
	private static final File EXTRACTED_FILES_ROOT;
	
	/**
	 * Implementation of the {@link Listener} interface for Powershell API.
	 * 
	 * @author marcin.kielar
	 *
	 */
	private static class PowershellListener implements Listener {
		
		/** Process runing the powershell script. */
		private Process process;

		/**
		 * Constructor.
		 * @param process process  runing the powershell script
		 */
		public PowershellListener(Process process) {
			this.process = process;
		}
		
		@Override
		public void close() {
			
			if (process != null) {
				
				try {
					
					process.exitValue();
					
				} catch (IllegalThreadStateException ex) {
					
					// Thread is not yet terminated- forcibly terminate
					process.destroy();
					
				}

				IOTools.closeQuietly(process.getInputStream());
				IOTools.closeQuietly(process.getErrorStream());
				IOTools.closeQuietly(process.getOutputStream());
			}
			
		}
	}
	
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
			return ScriptOutputParser.parse(process.getInputStream());
		} finally {
			IOTools.closeQuietly(process.getInputStream());
		}
		
	}

	@Override
	public Listener listen(USBDriveInfoListener usbDriveInfoListener) throws IOException {
		
		Process process = createScriptProcess(Script.LISTEN);
		ProcessTools.ignoreStream(process.getErrorStream());
		ProcessTools.ignoreStream(process.getOutputStream());
		
		ScriptOutputParser.parseAsync(process.getInputStream(), usbDriveInfoListener);
		
		return new PowershellListener(process);
		
	}

	/**
	 * Creates powershell process for given script.
	 * 
	 * @param script
	 *            script to run
	 * @return {@link Process} instance
	 * @throws IOException
	 *             when an IO error occurs when starting the process
	 */
	private Process createScriptProcess(Script script) throws IOException {
		
		File scriptFile = new File(EXTRACTED_FILES_ROOT, script.getFile().getName());
		
		String command = MessageFormat.format(POWERSHELL_COMMAND_TEMPLATE, scriptFile.getAbsolutePath());
		System.out.println("Running: " + command);
		return Runtime.getRuntime().exec(command);
		
	}

}
