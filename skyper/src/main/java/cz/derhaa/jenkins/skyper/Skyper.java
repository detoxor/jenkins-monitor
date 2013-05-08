package cz.derhaa.jenkins.skyper;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.skype.ContactList;
import com.skype.Skype;
import com.skype.SkypeException;

import cz.derhaa.jenkins.skyper.build.BuildListener;
import cz.derhaa.jenkins.skyper.build.BuildMonitor;
import cz.derhaa.jenkins.skyper.build.ResouceXml;

/**
 * @author derhaa
 *
 */
public class Skyper {

	private final String url;
	private final int interval;
	private static final Logger LOGGER = LoggerFactory.getLogger(Skyper.class);

	public Skyper(final Properties properties) {
		final Properties prop = properties;
		this.url = prop.getProperty("jenkins.url");
		this.interval = Integer.valueOf(prop.getProperty("interval"));
	}

	public final void run() {
		final BuildMonitor monitor = new BuildMonitor(url, interval);
		monitor.setResource(new ResouceXml(url));
		try {
			final ContactList list = Skype.getContactList();
//			final Friend friend = list.getFriend("xxx.zzz");
//			monitor.setListener(new BuildListener(){
//				@Override
//				public void notify(final String message) {
//					try {
//						friend.send(message);
//						LOGGER.info("Message has been sended to "+friend.getFullName()+". Content of message: "+message);
//					} catch (SkypeException e) {
//						throw new RuntimeException(e);
//					}
//				}
//				
//			});
		} catch (SkypeException e) {
			LOGGER.error("Skype connector fails", e);
		}
		monitor.setListener(new BuildListener(){
			@Override
			public void notify(final String message) {
				LOGGER.info("Send message: "+message);
			}
		});
		monitor.loop();
	}	
}
