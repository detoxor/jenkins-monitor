package cz.derhaa.jenkins.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import cz.derhaa.jenkins.monitor.build.CachedMonitor;
import cz.derhaa.jenkins.monitor.build.MessengerException;
import cz.derhaa.jenkins.monitor.resource.ResouceXml;
import cz.derhaa.jenkins.monitor.sender.SenderGMailSSL;
import cz.derhaa.jenkins.monitor.sender.SenderSkype;
import cz.derhaa.jenkins.monitor.util.Tool;

/**
 * @author derhaa
 * 
 */
public class App { // NOPMD by tocecz on 8.5.13 7:35
	/**
	 * @param args <ol>
	 * <li>path to configuration file -c=/path/to/cfg.file</li>
	 * <li>choose type of sender -s=[skype,gmail-ssl]</li>
	 * </ol>
	 */
	public static void main(final String[] args) {
		//load program properties
		final Properties props = new Properties();
		FileInputStream fis = null;
		try {
			final String filePath = Tool.parseCommand(args[0]);
			fis = new FileInputStream(new File(filePath));
			props.load(fis);
		} catch (IOException e) {
			throw new MessengerException("Fail load properties", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					//ignore failing error
				}
			}
		}

		final String url = props.getProperty(Tool.JENKINS_URL);
		final String jobs = props.getProperty(Tool.JOBS);
		
		final CachedMonitor monitor = new CachedMonitor(jobs);
		monitor.setResource(new ResouceXml(url));
		final String sender = Tool.parseCommand(args[1]);
		if(SenderGMailSSL.KEY.equals(sender)) {
			monitor.setSender(new SenderGMailSSL(props));	
		} else if (SenderSkype.KEY.equals(sender)) {
			monitor.setSender(new SenderSkype(props));
		} else {
			throw new MessengerException("Unknown sender! Sender implementation cannot be null");
		}

		final String intr = props.getProperty(Tool.INTERVAL);
		final Integer interval = intr == null ? null : Integer.valueOf(intr);
		monitor.run(interval);
	}
}
