package cz.derhaa.jenkins.monitor.util;

import java.util.ArrayList;
import java.util.List;

public class Tool {

	private static final String SEPARATOR = ",";
	public static final String JENKINS_URL="jenkins.url";
	public static final String FAIL = "failure";
	public static final String FAIL_MESSAGE = "Failed";
	public static final String FAIL_STILL_MESSAGE = "Still failing";
	public static final String SUCCESS = "success";
	public static final String SUCCESS_MESSAGE = "Success";
	public static final String FIXED_MESSAGE = "Fixed";
	public static final String UNSTABLE = "unstable";
	public static final String UNSTABLE_MESSAGE = "Unstable";
	public static final String STABLE_MESSAGE = "Return to Success";
	public static final String CONTACTS = "contacts";
	public static final String SENDER_MAIL = "user.mail";
	public static final String SENDER_EMAIL_PASS = "user.pass";
	public static final String INTERVAL = "interval";
	public static final String JOBS = "jobs";		
	
	private Tool() {
		//tool class
	}
	/**
	 * Parse string and cut by separator: ','
	 * @param stringForParse
	 * @return
	 */
	public static final List<String> parseString (String stringForParse) {
		List<String> jobs = new ArrayList<String>();
		if (stringForParse != null && stringForParse.indexOf(SEPARATOR) == -1) {
			jobs.add(stringForParse);
		} else if (stringForParse != null) {
			final String[] jbs = stringForParse.split(SEPARATOR);
			for (int i = 0; i < jbs.length; i++) {
				jobs.add(jbs[i]);
			}
		}
		return jobs;
	}
	/**
	 * Find commands and parse them and find value
	 * @param command
	 * @return null if value not found 
	 */
	public static final String parseCommand(String command) {
		String retval = null;
		if(command.indexOf("-c") !=-1) {
			retval = command.split("=")[1];
		} else if(command.indexOf("-s") !=-1) {
			retval = command.split("=")[1];
		}
		return retval;
	}
}
