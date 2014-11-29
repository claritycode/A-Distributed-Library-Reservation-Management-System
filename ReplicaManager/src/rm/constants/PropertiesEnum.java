package rm.constants;

public enum PropertiesEnum {
	LIBRARIES("libraries"),
	ORB_INITIAL_PORT("orb.initial.port"),
	ORB_INITIAL_HOST("orb.initial.host"),
	UDP_INITIAL_HOST("udp.initial.host"),
	UDP_MSG_SPLIT("udp.msg.split");
	
	private String value;
		
	PropertiesEnum(String value) {
		this.value = value;
	}

	public String val() {
		return value;
	}
}
