# usb-drive-notifier

USB Drive Notifier - A PoC using Java and Powershell WMI Queries

### Example usage

```java
Notifier notifier = new Notifier();
notifier.addListener(new USBDriveInfoListener() {

    @Override
	public void onEvent(USBDriveInfo info) {
	    System.out.println(info);
	}
	
});

notifier.startListening();
```