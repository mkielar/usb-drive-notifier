package pl.mkielar.usbdrivenotifier.tools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

/**
 * Default {@link Extractor} implementation. This class uses a
 * {@link Files#createTempDirectory(String, java.nio.file.attribute.FileAttribute...)}
 * to create a temporary folder into which resources are extracted.
 * 
 * @author marcin.kielar
 * 
 */
public class TempFileExtractor implements Extractor {

	@Override
	public File extract(Resource[] resources) throws IOException {
		
		File root = createDestinationDirectory();
		
		for (Resource resource : resources) {
		
			InputStream resourceStream = null;
			OutputStream destinationStream = null;
			
			try {
				
				resourceStream = getClass().getClassLoader().getResourceAsStream(resource.getResourcePath());
				destinationStream = new FileOutputStream(new File(root, resource.getFile().getPath()));
				
				IOTools.rewrite(resourceStream, destinationStream);
				
			} finally {
				
				IOTools.closeQuietly(resourceStream);
				IOTools.closeQuietly(destinationStream);
				
			}
		}

		return root;
	}

	/**
	 * Creates destiation directory.
	 * 
	 * @return root directory for extracted resources
	 * @throws IOException
	 *             when an IO error occurs accessing temporary directory
	 */
	private File createDestinationDirectory() throws IOException {
		
		File tempDir = Files.createTempDirectory("usbdrivenotifier").toFile();
		return tempDir;
		
	}

}
