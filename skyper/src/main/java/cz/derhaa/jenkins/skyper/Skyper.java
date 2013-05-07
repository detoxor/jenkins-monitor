package cz.derhaa.jenkins.skyper;

import java.util.Properties;

import com.skype.ContactList;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;

import cz.derhaa.jenkins.skyper.build.BuildMonitor;
import cz.derhaa.jenkins.skyper.build.BuildSendListener;
import cz.derhaa.jenkins.skyper.build.JenkinsXmlResouce;

/**
 * @author derhaa
 *
 */
public class Skyper {

	private final String url;
	private final int interval;
	private final Properties prop;

	public Skyper(final Properties properties) {
		this.prop = properties;
		this.url = prop.getProperty("jenkins.url");
		this.interval = Integer.valueOf(prop.getProperty("interval"));
	}

	public final void run() throws SkypeException {
		final ContactList list = Skype.getContactList();
		final Friend friend = list.getFriend("karkulka.blatna");
		BuildMonitor monitor = new BuildMonitor(new BuildSendListener(friend), url, interval);
		monitor.setResource(new JenkinsXmlResouce(url));
		monitor.loop();
	}	
}
