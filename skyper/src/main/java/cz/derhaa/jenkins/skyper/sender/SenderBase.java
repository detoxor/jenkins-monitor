package cz.derhaa.jenkins.skyper.sender;

import java.util.List;

abstract public class SenderBase implements Sender {

	protected String jenkinsUrl;
	protected List<String> contacts;
	
	public SenderBase(List<String> contacts, String jenkinsUrl) {
		this.jenkinsUrl = jenkinsUrl;
		this.contacts = contacts;
	}
}
