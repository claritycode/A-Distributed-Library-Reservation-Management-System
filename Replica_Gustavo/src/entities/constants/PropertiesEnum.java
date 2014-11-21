package entities.constants;

public enum PropertiesEnum {
	LIBRARIES("libraries"),
	ORB_INITIAL_PORT("orb.initial.port"),
	ORB_INITIAL_HOST("orb.initial.host"),
	UDP_INITIAL_HOST("udp.initial.host"),
	UDP_MSG_SPLIT("udp.msg.split"),
	RESULT_MSG_SPLIT("result.msg.split"),
	LIBRARY_UDP_PORT("udp.port"),
	LIBRARY_STUDENTS_FILE("students"),
	LIBRARY_BOOKS_FILE("books");
	
	private String value;
		
	PropertiesEnum(String value) {
		this.value = value;
	}

	public String val() {
		return value;
	}
}
