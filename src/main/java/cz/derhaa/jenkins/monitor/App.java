package cz.derhaa.jenkins.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import cz.derhaa.jenkins.monitor.build.BuildMonitor;
import cz.derhaa.jenkins.monitor.build.MessengerException;
import cz.derhaa.jenkins.monitor.resource.ResouceXml;
import cz.derhaa.jenkins.monitor.sender.SenderGMailSSL;
import cz.derhaa.jenkins.monitor.util.Tool;

/**
 * @author derhaa
 * 
 */
public class App { // NOPMD by tocecz on 8.5.13 7:35

	public static void main(final String[] args) {
		//load program properties
		final Properties props = new Properties();
		FileInputStream fis = null;
		try {
			String filePath = args[0];
			fis = new FileInputStream(new File(filePath));
			props.load(fis);
		} catch (IOException e) {
			throw new MessengerException("Fail load properties", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					throw new MessengerException("Fail close properties file", e);
				}
			}
		}
		
		final String intr = props.getProperty("interval");
		final Integer interval = intr == null ? null : Integer.valueOf(intr);
		final String url = props.getProperty(Tool.JENKINS_URL);
		final String jobs = props.getProperty("jobs");
		final BuildMonitor monitor = new BuildMonitor(jobs);
		monitor.setResource(new ResouceXml(url));
		monitor.setSender(new SenderGMailSSL(props));//TODO tohle udelat na parametr -sender=[skype | gmailssl]
		monitor.run(interval);
	}
}
