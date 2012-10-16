package pl.mkielar.usbdrivenotifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import pl.mkielar.usbdrivenotifier.model.USBDriveInfo;
import pl.mkielar.usbdrivenotifier.powershell.PowershellAPI;
import pl.mkielar.usbdrivenotifier.tools.IOTools;
import pl.mkielar.usbdrivenotifier.tools.ServiceLookup;

/**
 * USB Drive Notifier.
 * 
 * This class is an entry-point of this little framework.
 * 
 * There are two main usages:
 * 
 * <ol>
 * <li>Getting currently attached removable drives - with
 * {@link #getCurrentDrives()},</li>
 * <li>Starting asynchronous listener, that will notify the callback object when
 * removable drive event happen - with {@link #startListening()}.</li>
 * </ol>
 * 
 * In the first case, the {@link API} provider is called every time the
 * {@link #getCurrentDrives()} method is called.
 * 
 * In the second case, a background thread / process / worker is started that
 * listens to system events and transforms them to callback invocations. In this
 * case, there are two ways to stop the listener:
 * 
 * <ol>
 * <li>by calling {@link #stopListening()} on the {@code Notifier} instance,</li>
 * <li>by creating the {@code Notifier} with an {@link #Notifier(boolean)
 * alternative constructor} which causes adding a
 * {@link Runtime#addShutdownHook(Thread)} shutdown hook to the JVM. 
 * </ol>
 * 
 * @author marcin
 * 
 */
public class Notifier {

	/** Notifier state. */
	private static enum State {
		NEW,
		STARTED,
		STOPPED,
		BROKEN;
	}
	
	/** API provider. */
	private API api = ServiceLookup.lookup(API.class, PowershellAPI.class);
	
	/** Listeners. */
	private List<USBDriveInfoListener> usbDriveInfoListeners = Collections.synchronizedList(new ArrayList<USBDriveInfoListener>());

	/** Listener wrapper. */
	private USBDriveInfoListener usbDriveInfoListenersWrapper = new USBDriveInfoListener() {

		@Override
		public void onEvent(USBDriveInfo info) {
			for (USBDriveInfoListener listener : usbDriveInfoListeners) {
				listener.onEvent(info);
			}
		}
	};
	
	/** Shutdown hook. */
	private Thread shutdownHook = null;
	
	/** State of the notifier. */
	private State state = State.NEW;
	
	/** Process listening for USB drive events. */
	private Listener listenProcess;
	
	/** Shoud shutdown hook be added in order to destroy the process if still running. */
	private boolean addShutdownHook;
	
	public Notifier() {
		this(false);
	}
	
	public Notifier(boolean addShutdownHook) {
		this.addShutdownHook = addShutdownHook;
	}
	
	/**
	 * Adds USB drive notification listener.
	 * 
	 * @param listener
	 *            notification listener
	 */
	public void addListener(USBDriveInfoListener listener) {
		usbDriveInfoListeners.add(listener);
	}

	/**
	 * Removes USB drive notification listener.
	 * 
	 * @param listener
	 *            notification listener
	 */
	public void removeListener(USBDriveInfoListener listener) {
		usbDriveInfoListeners.remove(listener);
	}

	public synchronized List<USBDriveInfo> getCurrentDrives() throws IOException {
		return api.getCurrentDrives();
	}

	public synchronized void startListening() throws IOException {
		
		try {
			
			if (state != State.NEW) {
				throw new IllegalStateException(state.name());
			}
			listenProcess = api.listen(usbDriveInfoListenersWrapper);
			
			if (addShutdownHook) {
				createShutdownHook();
			}
			
			state = State.STARTED;
			
		} catch (IOException | RuntimeException ex) {
			
			state = State.BROKEN;
			throw ex;
			
		}
	}
	
	public synchronized void stopListening() {
		
		try {
			
			IOTools.closeQuietly(listenProcess);
			
		} finally {
			
			if (state != State.BROKEN) {
				state = State.STOPPED;
			}
			
		}
	}
	
	private void createShutdownHook() {
		
		this.shutdownHook = new Thread(new Runnable() {
		
			public void run() {
				stopListening();
			}
			
		});
		
		Runtime.getRuntime().addShutdownHook(this.shutdownHook);
		
	}
	
	public static void main(String[] args) throws Exception {
		
		final Notifier notifier = new Notifier(true);
		
		// System.out.println(notifier.getCurrentDrives());
		
		
		notifier.addListener(new USBDriveInfoListener() {

			@Override
			public void onEvent(USBDriveInfo info) {
				System.out.println(info);
			}
			
		});
		notifier.startListening();
		
		System.out.println("Notifier started");
//		new Thread(new Runnable() {
//			public void run() {
//				try {Thread.sleep(5000);} catch(Exception ex) {}
//				notifier.stopListen();
//				System.out.println("Notifier stopped");
//			}
//		}).start();
		
	}
}
