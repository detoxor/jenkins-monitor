package cz.derhaa.jenkins.skyper.build;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * @author derhaa
 * 
 */
public class BuildMonitor implements IBuildMonitor {

	private static final Logger logger = LoggerFactory.getLogger(BuildMonitor.class);

	private static final String FAIL = "failure";
	private static final String SUCCESS = "success";
	private static final String UNSTABLE = "unstable";
	private Map<String, Build> cache = new HashMap<String, Build>();
	private final BuildListener listener;
	private final String jenkinsUrl;
	private final int interval;
	private StringBuilder sb;
	private static final String PREFIX = "[Jenkins]";

	public BuildMonitor(final BuildListener listener, final String jenkinsUrl, final int interval) {
		this.listener = listener;
		this.jenkinsUrl = jenkinsUrl;
		this.interval = interval;
		this.sb = new StringBuilder();
	}

	public void loop() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					checkNewBuilds();
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						throw new RuntimeException("Loop failed", e);
					}
				}
			}
		}).start();
	}

	public final void checkNewBuilds() {
		Map<String, Build> builds = fetchBuilds();
		for (Entry<String, Build> entry : builds.entrySet()) {
			Build build = entry.getValue();
			String name = build.getName();
			String status = build.getLastBuildStatus();
			Build actual = cache.get(name);
			if (actual == null) {// not exist in cache
				handleNewBuild(build, null);
			} else if (!actual.getLastBuildLabel().equals(status)) {
				handleNewBuild(build, actual.getLastBuildStatus());
			}
		}
		cache = new HashMap<String, Build>(builds);
	}

	public final void handleNewBuild(final Build build, final String oldStatus) {
		String foo = build.getLastBuildStatus().toLowerCase();
		String hoo = oldStatus == null ? null : oldStatus.toLowerCase();
		String event = "N/A";
		sb.setLength(0);
		if (hoo == null) {
			if (foo.equals(FAIL)) {
				event = "(rain), Failed";
			} else if (foo.equals(UNSTABLE)) {
				event = "(tumbleweed), Unstable";
			} else {
				event = "(sun), Success";
			}
		} else {
			if (hoo.equals(FAIL) && foo.equals(FAIL)) {// fail->fail
				event = "(rain), Still failing";
			} else if (hoo.equals(FAIL) && foo.equals(UNSTABLE)) {// fail->unstable
				event = "(tumbleweed), Unstable";
			} else if (hoo.equals(FAIL) && foo.equals(SUCCESS)) {// fail->success
				event = "(sun), Fixed";
			} else {
				event = "(sun), Success";
			}
		}
		sb.append(PREFIX).append(event).append(build.getName()).append(" - ").append(jenkinsUrl).append("/job/").append(build.getName()).append("/")
				.append(build.getLastBuildLabel());
		listener.notify(sb.toString());
	}

	public final Map<String, Build> fetchBuilds() {
		Map<String, Build> retval = new HashMap<String, Build>();
		try {
			URL myURL = new URL(jenkinsUrl);
			URLConnection conn = myURL.openConnection();
			conn.connect();

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();

			BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String xml = sb.toString().replaceAll("&nbsp;"," ");
			logger.info("Xml: " +xml);
			Document doc = db.parse(new ByteArrayInputStream(xml.getBytes()));

			NodeList projects = doc.getElementsByTagName("Project");
			final int count = projects.getLength();
			System.out.println(count);
			for (int x = 0; x < count; x++) {
				Node project = projects.item(x);
				NamedNodeMap attrs = project.getAttributes();

				final String name = attrs.getNamedItem("name").getNodeValue();
				final String webUrl = attrs.getNamedItem("webUrl").getNodeValue();
				final String lastBuildLabel = attrs.getNamedItem("lastBuildLabel").getNodeValue();
				final String lastBuildTime = attrs.getNamedItem("lastBuildTime").getNodeValue();
				final String lastBuildStatus = attrs.getNamedItem("lastBuildStatus").getNodeValue();
				final String activity = attrs.getNamedItem("activity").getNodeValue();
				String s = lastBuildTime.replace("Z", "+00:00");
				try {
					s = s.substring(0, 22) + s.substring(23);
				} catch (IndexOutOfBoundsException e) {
					throw new RuntimeException("Invalid length", e);
				}
				Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").parse(s);
				Build build = new Build(name, webUrl, Integer.valueOf(lastBuildLabel), date, lastBuildStatus, activity);
				logger.info("Job Name: " + name + " has been fetched");
				retval.put(build.getName(), build);
			}
		} catch (MalformedURLException e) { // new URL() failed
			logger.info(MalformedURLException.class.getCanonicalName() + " - URL failed");
		} catch (IOException e) { // openConnection() failed
			logger.info(IOException.class.getCanonicalName() + " - connection failed");
		} catch (ParserConfigurationException e) {
			logger.info(ParserConfigurationException.class.getCanonicalName() + " - document builder failed");
		} catch (SAXException e) {
			logger.info(SAXException.class.getCanonicalName() + " - parsing stream failed");
		} catch (ParseException e) {
			logger.info(ParseException.class.getCanonicalName() + " - parsing date of build failed");
		}
		return retval;
	}
}
