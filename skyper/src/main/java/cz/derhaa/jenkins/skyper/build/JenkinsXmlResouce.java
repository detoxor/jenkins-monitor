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
import java.util.HashSet;
import java.util.Set;

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
public class JenkinsXmlResouce implements ResourceStrategy {

	private final URL url;
	private static final Logger logger = LoggerFactory.getLogger(JenkinsXmlResouce.class);

	public JenkinsXmlResouce(String jenkinsUrl) {
		try {
			this.url = new URL(jenkinsUrl);
		} catch (MalformedURLException e) {
			throw new RuntimeException("Fail load jenkins job metadata", e);
		}
	}

	public Set<Build> getBuilds() {
		Set<Build> retval = new HashSet<Build>();
		try {
			URLConnection connection = url.openConnection();
			connection.connect();
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line = "";
			while ((line = reader.readLine()) != null) {
				sb.append(line);
			}
			String xml = sb.toString().replaceAll("&nbsp;", " ");
			ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());

			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(bais);

			NodeList projects = doc.getElementsByTagName("Project");
			final int count = projects.getLength();
			for (int x = 0; x < count; x++) {
				final Node project = projects.item(x);
				final NamedNodeMap attrs = project.getAttributes();
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
				logger.info("Build: "+build.getName()+ " has been parsed");
				retval.add(build);
			}
			reader.close();
			bais.close();
		} catch (ParserConfigurationException e) {
			logger.error("Document building failed", e);			
		} catch (SAXException e) {
			logger.error("Parsing file failed", e);
		} catch (ParseException e) {
			logger.error("Date convert failed", e);
		} catch (IOException e) { // openConnection() failed
			logger.error("Read resouce failed", e);			
		} 
		return retval;
	}

}
