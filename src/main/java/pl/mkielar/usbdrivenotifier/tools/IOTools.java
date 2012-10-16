package pl.mkielar.usbdrivenotifier.tools;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class IOTools {

	private static final int DEFAULT_BUFFER_SIZE = 64 * 1024; 
	
	private IOTools() { }
	
	public static void closeQuietly(Closeable closeable) {
		if (closeable != null) {
			try {
				closeable.close();
			} catch (IOException e) {
				;
			}
		}
	}

	public static void rewrite(InputStream resourceStream, OutputStream destinationStream) throws IOException {
		
		byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
		int cnt = 0;
		
		while ((cnt = resourceStream.read(buffer)) != -1) {
			destinationStream.write(buffer, 0, cnt);
		}
		
	}
}
