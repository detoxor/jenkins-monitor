package cz.derhaa.jenkins.messenger.sender;

import java.util.List;
import java.util.Properties;

abstract public class SenderBase implements Sender {

	protected String jenkinsUrl;
	protected List<String> contacts;
	protected final Properties properties;
	
	public SenderBase(List<String> contacts, Properties properties) {
		this.properties = properties;
		this.jenkinsUrl = properties.getProperty("jenkins.url");
		this.contacts = contacts;
	}
}
