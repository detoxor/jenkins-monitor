package cz.derhaa.jenkins.messenger.sender;

import cz.derhaa.jenkins.messenger.build.Build;
/**
 * Basic imutable pojo to describe build and message
 * @author derhaa
 */
public class Notice {

	private final Build build;
	private final String message;
	/**
	 * Crate for build and message describing build
	 * @param build
	 * @param message
	 */
	public Notice(final Build build, final String message) {
		this.build = build;
		this.message = message;
	}
	/**
	 * @return kept build instance
	 */
	public Build getBuild() {
		return build;
	}
	/**
	 * @return kept message
	 */
	public String getMessage() {
		return message;
	}
	
}
