package cz.derhaa.jenkins.skyper.sender;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skype.ContactList;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;

import cz.derhaa.jenkins.skyper.build.BuildListener;

public class SenderSkype extends SenderBase implements BuildListener {

	private static final Logger LOGGER = LoggerFactory.getLogger(SenderSkype.class);
	private ArrayList<Friend> forMessage;
	private StringBuilder stringBuilder;

	public SenderSkype(final List<String> skypeIds, final String jenkinsUrl) {
		super(skypeIds, jenkinsUrl);
		try {
			final ContactList list = Skype.getContactList();
			this.stringBuilder = new StringBuilder();
			if(skypeIds.size() == 1) {
				forMessage.add(list.getFriend(skypeIds.get(0)));
			} else {
				for (String skypeId : skypeIds) {
					Friend friend = list.getFriend(skypeId);
					if(friend == null) {
						LOGGER.debug("User with skypeId: "+skypeId+" has been not found");
					} else {
						forMessage.add(friend);
					}
				}
			}			
		} catch (SkypeException e) {
			LOGGER.error("Skype connector fails", e);
		}
	}

	@Override
	public void send(String buildName, String lastBuildLabel, String message) {
		try {
			for (Friend friend : forMessage) {
				stringBuilder.setLength(0);
				stringBuilder.append("[Jenkins]")
					.append(" [").append(buildName).append("] ")
					.append(message).append(" - ").append(jenkinsUrl).append("/job/")
					.append(buildName).append("/")
					.append(lastBuildLabel);
				friend.send(stringBuilder.toString());
				String fullname = Normalizer.normalize(friend.getFullName(), Form.NFD).replaceAll("[^\\p{ASCII}]","");
				LOGGER.info("Message has been sended to "+fullname+". Content of message: "+message);
			}
		} catch (SkypeException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void notify(String buildName, String lastBuildLabel, String message) {
		send(buildName, lastBuildLabel, message);
	}

}
