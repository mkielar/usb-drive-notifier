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
public class PowershellUSBDriveInfoFactory {

	private static class AsyncParser implements Runnable {

		private InputStream inputStream;
		private USBDriveInfoListener callback;

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

	public static void parseAsync(InputStream inputStream, USBDriveInfoListener callback) {

		new Thread(new AsyncParser(inputStream, callback)).start();
	
	}

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
	
	private static String parse(String regex, String line) {
		Matcher matcher = Pattern.compile(regex, Pattern.MULTILINE).matcher(line);
		if (matcher.find()) {
			return matcher.group(1);
		} else {
			return null;
		}
	}
	
}
