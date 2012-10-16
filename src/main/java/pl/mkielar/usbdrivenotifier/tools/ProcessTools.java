package pl.mkielar.usbdrivenotifier.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class ProcessTools {

	private static class DevNull implements Runnable {
		
		private InputStream inputStream;
		
		public DevNull(InputStream inputStream) {
			this.inputStream = inputStream;
		}
		
		@Override
		public void run() {
			
			try {
				while (inputStream.read() != -1);
			} catch (IOException e) {
				;
			} finally {
				IOTools.closeQuietly(inputStream);
			}
		}
	}
	
	private ProcessTools() { }
	
	public static void ignoreStream(InputStream inputStream) {
		new Thread(new DevNull(inputStream)).start();
	}

	public static void ignoreStream(OutputStream outputStream) {
		IOTools.closeQuietly(outputStream);
	}
	
	public static void destroyQuietly(Process process) {

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
