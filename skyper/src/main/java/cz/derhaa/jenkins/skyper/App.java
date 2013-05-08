package cz.derhaa.jenkins.skyper;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import com.skype.SkypeException;

import cz.derhaa.jenkins.skyper.build.SkyperException;

/**
 * @author derhaa
 *
 */
public class App { // NOPMD by tocecz on 8.5.13 7:35

	public static void main(final String[] args) throws SkypeException {
		final Properties props = new Properties();
		final URL url = ClassLoader.getSystemResource("config.properties");
        try {
			props.load(url.openStream());
			new Skyper(props).run();
		} catch (IOException e) {
			throw new SkyperException("Fail load properties", e);
		}	
	}	
}
