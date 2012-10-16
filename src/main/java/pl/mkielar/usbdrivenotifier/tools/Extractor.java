package pl.mkielar.usbdrivenotifier.tools;

import java.io.File;
import java.io.IOException;

public interface Extractor {
	
	/**
	 * Extracts resources from classpath and returns root path of the directory,
	 * where files are extracted.
	 * 
	 * @param resources
	 *            list of resources to extract
	 * @return root path of extracted resources
	 * @throws IOException
	 *             in case of read / write error
	 */
	File extract(Resource[] resources) throws IOException;
	
}
