package cz.derhaa.jenkins.messenger.sender;

import java.text.Normalizer;
import java.text.Normalizer.Form;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skype.ContactList;
import com.skype.Friend;
import com.skype.Skype;
import com.skype.SkypeException;

import cz.derhaa.jenkins.messenger.build.BuildMonitor;

public class SenderSkype extends SenderBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SenderSkype.class);
	private ArrayList<Friend> forMessage;
	private StringBuilder stringBuilder;

	public SenderSkype(final List<String> skypeIds, final Properties properties) {
		super(skypeIds, properties);
		this.forMessage = new ArrayList<Friend>();
		try {
			final ContactList list = Skype.getContactList();
			this.stringBuilder = new StringBuilder();
			if(skypeIds.size() == 1) {
				String skypeId = skypeIds.get(0);
				Friend friend = list.getFriend(skypeId);
				forMessage.add(friend);
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
	public void notify(String buildName, String lastBuildLabel, String lastBuildStatus, String message) {
		try {
			for (Friend friend : forMessage) {
				stringBuilder.setLength(0);
				String prefix = "n/a";
				String hoo = lastBuildStatus.toLowerCase();
				if(BuildMonitor.FAIL.equals(hoo)) {
					prefix = "(rain)";
				} else if(BuildMonitor.UNSTABLE.equals(hoo)) {
					prefix = "(tumbleweed)";
				} else if (BuildMonitor.SUCCESS.equals(hoo)) {
					prefix = "(sun)";
				}
				stringBuilder.append("[Jenkins]")
					.append(" [").append(buildName).append("] ").append(prefix).append(" ")
					.append(message).append(" - ").append(jenkinsUrl).append("/job/")
					.append(buildName).append("/")
					.append(lastBuildLabel);
				friend.send(stringBuilder.toString());
				String fullname = Normalizer.normalize(friend.getFullName(), Form.NFD).replaceAll("[^\\p{ASCII}]","");
				LOGGER.info("Message has been sended to "+fullname+". Content of message: "+stringBuilder.toString());
			}
		} catch (SkypeException e) {
			throw new RuntimeException(e);
		}
	}

}
