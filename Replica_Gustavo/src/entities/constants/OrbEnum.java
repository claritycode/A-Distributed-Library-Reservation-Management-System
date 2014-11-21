package entities.constants;

public enum OrbEnum {
	ORB_INITIAL_PORT_ARG("-ORBInitialPort"),
	ORB_INITIAL_HOST_ARG("-ORBInitialHost");
	
	private String value;
	
	OrbEnum(String value) {
		this.value = value;
	}

	public String val() {
		return value;
	}
}
