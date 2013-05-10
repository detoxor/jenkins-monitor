package cz.derhaa.jenkins.monitor.build;

public enum Status {

	FAIL("failure"),
	UNSTABLE("unstable"),
	STABLE("stable"),
	SUCCESS("success"),
	;
	private String key;
	
	private Status(String key) {
		this.key = key;
	}
	
	public String getKey() {
		return key;
	}
}
