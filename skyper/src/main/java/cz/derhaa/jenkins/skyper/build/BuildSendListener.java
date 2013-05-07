package cz.derhaa.jenkins.skyper.build;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skype.Friend;
import com.skype.SkypeException;

/**
 * @author derhaa
 *
 */
public class BuildSendListener implements BuildListener {

	private final Friend friend;
	private static final Logger logger = LoggerFactory.getLogger(BuildSendListener.class);
	
	public BuildSendListener(Friend friend) {
		this.friend = friend;
	}

	public void notify(String message) {
		try {
			friend.send(message);
			logger.info("Message has been sended to "+friend.getFullName()+". Content of message: "+message);
		} catch (SkypeException e) {
			throw new RuntimeException(e);
		}
	}

}
