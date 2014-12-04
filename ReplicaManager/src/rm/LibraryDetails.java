package rm;

import DRMSServices.LibraryInterface;

public class LibraryDetails {

	private final String name;
	private final int udpPort;
	private final LibraryInterface corbaLibrary;
	private int failures;
	
	public LibraryDetails(final String libraryName, final int udpPort, final LibraryInterface corbaLibrary) {
		this.name = libraryName;
		this.udpPort = udpPort;
		this.corbaLibrary = corbaLibrary;
	}

	public String getName() {
		return name;
	}

	public int getUdpPort() {
		return udpPort;
	}

	public LibraryInterface getCorbaLibrary() {
		return corbaLibrary;
	}

	public int getFailures() {
		return failures;
	}

	public void setFailures(int failures) {
		this.failures = failures;
	}
	
}
