package pl.mkielar.usbdrivenotifier.tools;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Utility class for IO handling.
 * 
 * @author marcin.kielar
 *
 */
public final class IOTools {

	/** Default buffer size for stream IO. */
	private static final int DEFAULT_BUFFER_SIZE = 64 * 1024; 
	
	/** Constructor. */
	private IOTools() { }

	/**
	 * Quietly closes the {@code closeable}.
	 * 
	 * @param closeable
	 *            resource to close
	 */
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				;
			}
		}
	}

	/**
	 * Copies contents of the {@code resourceStream} to the {@code destinationStream}.
	 * 
	 * @param resourceStream input stream
	 * @param destinationStream destination stream
	 * @throws IOException when an IO error occurs during read / write
	 */
	public static void rewrite(InputStream resourceStream, OutputStream destinationStream) throws IOException {
		
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int cnt = 0;
		
		while ((cnt = resourceStream.read(buffer)) != -1) {
			destinationStream.write(buffer, 0, cnt);
		}
	}
}
