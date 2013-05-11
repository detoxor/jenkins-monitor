package cz.derhaa.jenkins.monitor.sender;

import java.util.List;
import java.util.Properties;

import cz.derhaa.jenkins.monitor.util.Tool;
/**
 * Common implementation which keeps data as:
 * <ol>
 * <li>jenkins url</li>
 * <li>list contacts of people (email, skype etc... depends on concrete sender implementation)</li>
 * <li>properties (outer configuration for each sender)</li>
 * </ol>
 * @author derhaa
 */
abstract public class SenderBase implements SendListener {

	protected String jenkinsUrl;
	protected List<String> contacts;
	protected final Properties properties;
	/**
	 * Basic interface for sending notices about jenkins builds
	 * @param properties configuration properties
	 */
	public SenderBase(Properties properties) {
		this.properties = properties;
		this.jenkinsUrl = properties.getProperty(Tool.JENKINS_URL);
		this.contacts = Tool.parseString(properties.getProperty(Tool.CONTACTS));
	}
}
