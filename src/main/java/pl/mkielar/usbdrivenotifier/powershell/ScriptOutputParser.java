package pl.mkielar.usbdrivenotifier.powershell;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import pl.mkielar.usbdrivenotifier.USBDriveInfoListener;
import pl.mkielar.usbdrivenotifier.model.USBDriveInfo;
import pl.mkielar.usbdrivenotifier.model.USBDriveStatus;

/**
 * This class parses output of {@code .ps1} scripts in form of:
 * 
 * <pre>
 * {@code
 * INFO-START
 *   STATUS       : CREATION
 *   DRIVE        : G:
 *   LABEL        : KINGSTON
 *   NAME         : Kingston DataTraveler 2.0 USB Device
 *   SERIALNUMBER : 7F9FF92B
 * INFO-STOP
 * }
 * </pre>
 * 
 * @author Marcin Kielar
 *
 */
public class ScriptOutputParser {

	/**
	 * Asynchronous parser.
	 * 
	 * @author marcin.kielar
     *
	 */
	private static class AsyncParser implements Runnable {

		/** Input stream to parse from. */
		private InputStream inputStream;
		
		/** Callback to notify when drive metadata is fully parsed. */
		private USBDriveInfoListener callback;

		/**
		 * Constructor.
		 * 
		 * @param inputStream
		 *            input stream to parse from
		 * @param callback
		 *            callback to notify when drive metadata is fully parsed
		 */
		public AsyncParser(InputStream inputStream, USBDriveInfoListener callback) {
			this.inputStream = inputStream;
			this.callback = callback;
		}
		
		@Override
		public void run() {
			
			try (Scanner scanner = new Scanner(inputStream)) {
			
				USBDriveInfo info = null;
				
				do {
					
					info = readInfo(scanner);
					
					if (info != null) {
						callback.onEvent(info);
					}
					
				} while (info != null);
			}
			
		}
		
	}

	/**
	 * Synchronously parses the {@code inputStream}. This method assumes that
	 * the {@code inputStream} will at some point end, i.e. that the powershell
	 * process will end, allowing this method to finish.
	 * 
	 * @param inputStream
	 *            powershell's process input stream to read from
	 * @return list of metadata for currently attached removable drives
	 * @throws IOException
	 *             when an IO error occurs accessing the stream
	 */
	public static List<USBDriveInfo> parse(InputStream inputStream) throws IOException {

		USBDriveInfo info = null;
		List<USBDriveInfo> list = new ArrayList<>();
		
		try (Scanner scanner = new Scanner(inputStream)) {
		
			do {
				
				info = readInfo(scanner);
				
				if (info != null) {
					list.add(info);
				}
				
			} while (info != null);
			
			return list;
			
		}
		
	}

	/**
	 * Starts new {@link Thread} which asynchronously parses the
	 * {@code inputStream} and notifier the {@code callback}, when it finds
	 * metadata concerning some removable drive.
	 * 
	 * This method does not assume anything (like {@link #parse(InputStream)})
	 * and will return immediately after starting new thread.
	 * 
	 * @param inputStream
	 *            inputStream powershell's process input stream to read from
	 * 
	 * @param callback
	 */
	public static void parseAsync(InputStream inputStream, USBDriveInfoListener callback) {

		new Thread(new AsyncParser(inputStream, callback)).start();
	
	}

	/**
	 * Parses single {@link USBDriveInfo} metadata entity from powershell input
	 * stream. The input stream is wrapped in the {@link Scanner} instance
	 * before this method invocation.
	 * 
	 * This method returns null in two cases:
	 * 
	 * <ol>
	 * <li>When it cannot parse the stream any more (stream has been read to an end)</li>
	 * <li>When the stream contains unexpected data</li>
	 * </ul>
	 * 
	 * @param scanner
	 *            scanner to read from
	 * @return single entity of {@link USBDriveInfo} metadata, or null
	 */
	private static USBDriveInfo readInfo(Scanner scanner) {
		
			
		while (scanner.hasNextLine()) {
			
			String startLine = scanner.nextLine();
			System.out.println("Start line: " + startLine);
			
			if ("INFO-START".equals(startLine)) {
				
				String status = parse("^  STATUS       : (.*)$", scanner.nextLine());
				String driveLetter = parse("^  DRIVE        : (.*)$", scanner.nextLine());
				String label = parse("^  LABEL        : (.*)$", scanner.nextLine());
			    String name = parse("^  NAME         : (.*)$", scanner.nextLine());
				String serialNumber = parse("^  SERIALNUMBER : (.*)$", scanner.nextLine());
				
				String endLine = scanner.nextLine();
				if ("INFO-STOP".equals(endLine)) {
					return new USBDriveInfo(USBDriveStatus.valueOf(status), driveLetter, label, name, serialNumber);
				}
			}
		}
			
		return null;
	}

	/**
	 * Parses single line of input from the powershell process' input stream
	 * 
	 * @param regex
	 *            regular expression telling how to extract value from the line
	 * @param line
	 *            the line
	 * @return the value parsed from the line
	 */
	private static String parse(String regex, String line) {
		Matcher matcher = Pattern.compile(regex, Pattern.MULTILINE).matcher(line);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}
	
}
