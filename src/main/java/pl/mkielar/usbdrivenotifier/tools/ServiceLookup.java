package pl.mkielar.usbdrivenotifier.tools;

import java.util.ServiceLoader;

public class ServiceLookup {

	/**
	 * Looks up an implementation of given interface
	 * 
	 * @param clazz
	 *            class to lookup implemetation for
	 * @param defaultImplementation
	 *            default implementation of the interface
	 * @return implenetation
	 */
	public static <T> T lookup(Class<T> clazz, Class<? extends T> defaultImplementation) {
		ServiceLoader<T> loader = ServiceLoader.load(clazz);
		
		for (T implementation : loader) {
			
            // We are only expecting one
            return implementation;
        }
		
		// If ServiceLoader did not return any implementation, create default
		try {
			return defaultImplementation.newInstance();
		} catch (InstantiationException | IllegalAccessException ex) {
			throw new Error("Error creating instance of default implementation " + clazz.getName(), ex);
		}
	}
}
