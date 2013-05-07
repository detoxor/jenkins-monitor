/**
 * 
 */
package cz.derhaa.jenkins.skyper;

import com.skype.ContactList;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;

import cz.derhaa.jenkins.skyper.build.BuildListener;
import cz.derhaa.jenkins.skyper.build.BuildMonitor;

/**
 * @author derhaa
 *
 */
public class Skyper {

	private String jenkinsUrl = "http://127.0.0.1:8080/jenkins/cc.xml";
	private int interval = 6000; //10 seconds in miliseconds
	
	public final void run() throws SkypeException {
		final ContactList list = Skype.getContactList();
		final Friend friend = list.getFriend("karkulka.blatna");
		new BuildMonitor(new BuildListener() {
			public void notify(String message) {
				try {
					friend.send(message);
				} catch (SkypeException e) {
					throw new RuntimeException(e);
				}
			}
		}, jenkinsUrl, interval).loop();
	}
	
	
	
}
