package pl.mkielar.usbdrivenotifier.tools;

import java.io.File;

/**
 * Resource interface used with {@link Extractor}.
 * 
 * @author marcin.kielar
 *
 */
public interface Resource {

	/**
	 * Returns the path to resource in classpath.
	 * 
	 * @return the path to resource in classpath
	 */
	String getResourcePath();

	/**
	 * Returns the path (relative to extraction root) to resource after it is
	 * extracted
	 * 
	 * @return relative path to resource
	 */
	File getFile();
	
}
