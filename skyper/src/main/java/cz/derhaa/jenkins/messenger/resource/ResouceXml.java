package cz.derhaa.jenkins.messenger.resource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
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

import cz.derhaa.jenkins.messenger.build.Build;
import cz.derhaa.jenkins.messenger.build.MessengerException;

/**
 * @author derhaa
 * 
 */
public class ResouceXml extends ResourceBase {

	private final URL url;
	private static final Logger LOGGER = LoggerFactory.getLogger(ResouceXml.class);
	private final StringBuilder stringBuilder;
	private final DocumentBuilderFactory dbf;
	private final DocumentBuilder docb;

	public ResouceXml(final String jenkinsUrl) {
		super(jenkinsUrl);
		try {
			this.url = new URL(jenkinsUrl+"/cc.xml");
			this.stringBuilder = new StringBuilder();
			this.dbf = DocumentBuilderFactory.newInstance();
			this.docb = dbf.newDocumentBuilder();
		} catch (MalformedURLException e) {
			throw new MessengerException("Fail load jenkins job metadata", e);
		} catch (ParserConfigurationException e) {
			throw new MessengerException("Document building failed", e);
		}
	}

	public final Set<Build> getBuilds() {
		final Set<Build> retval = new HashSet<Build>();
		try {
			final URLConnection connection = url.openConnection();
			connection.connect();
			final BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			stringBuilder.setLength(0);
			String line = "";
			while ((line = reader.readLine()) != null) {
				stringBuilder.append(line);
			}
			final String xml = stringBuilder.toString().replaceAll("&nbsp;", " ");
			final ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());
			final Document document = docb.parse(bais);

			final NodeList projects = document.getElementsByTagName("Project");
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
				String dateString = lastBuildTime.replace("Z", "+00:00");
				try {
					dateString = dateString.substring(0, 22) + dateString.substring(23);
				} catch (IndexOutOfBoundsException e) {
					throw new MessengerException("Invalid length", e);
				}
				final Date date = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.ENGLISH).parse(dateString);
				final Build build = new Build(name, webUrl, Integer.valueOf(lastBuildLabel), date, lastBuildStatus, activity);
				LOGGER.info("Build: "+build.getName()+ " has been parsed");
				retval.add(build);
			}
			reader.close();
			bais.close();
		} catch (ConnectException e) {
			throw new MessengerException("Could not connect", e);
		} catch (SAXException e) {
			LOGGER.error("Parsing file failed", e);
		} catch (ParseException e) {
			LOGGER.error("Date convert failed", e);
		} catch (IOException e) { // openConnection() failed
			LOGGER.error("Read resouce failed", e);			
		} 
		return retval;
	}
}
