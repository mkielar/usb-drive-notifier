package pl.mkielar.usbdrivenotifier.powershell.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import pl.mkielar.usbdrivenotifier.tools.IOTools;

/**
 * Utility class for handling {@link Process} communication.
 * 
 * @author marcin.kielar
 *
 */
public final class ProcessTools {

	/**
	 * Implementation of a {@link InputStream} reader, which ignores all the
	 * content. This class main reason of existence is to consume input from
	 * {@link Process}` input / error stream in order to make sure, the process
	 * would not block / deadlock.
	 * 
	 * @author marcin.kielar
	 * 
	 */
	private static class DevNull implements Runnable {
		
		/** The stream to consume. */
		private InputStream inputStream;
		
		/**
		 * Constructor.
		 * 
		 * @param inputStream
		 *            the stream to consume
		 */
		public DevNull(InputStream inputStream) {
			this.inputStream = inputStream;
		}
		
		@Override
		public void run() {
			
			try {
				
				// Read the stream until it's closed.
				while (inputStream.read() != -1);
				
			} catch (IOException e) {
				; // Ignore errors
			} finally {
				IOTools.closeQuietly(inputStream);
			}
		}
	}
	
	/** Private constructor. */
	private ProcessTools() { }
	
	/**
	 * Starts new {@link Thread} which uses a {@link ProcessTools.DevNull}
	 * instance to consume all data in the {@code inputStream}.
	 * 
	 * @param inputStream
	 *            the stream to consume
	 */
	public static void ignoreStream(InputStream inputStream) {
		new Thread(new DevNull(inputStream)).start();
	}

	/**
	 * Quietly closes the output stream.
	 * 
	 * @param outputStream
	 *            output stream to close
	 */
	public static void ignoreStream(OutputStream outputStream) {
		IOTools.closeQuietly(outputStream);
	}
	
}
