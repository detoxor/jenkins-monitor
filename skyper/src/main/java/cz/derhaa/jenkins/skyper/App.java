package cz.derhaa.jenkins.skyper;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import com.skype.SkypeException;

/**
 * @author derhaa
 *
 */
public class App {

	public static void main(String[] args) throws SkypeException {
		Properties props = new Properties();
		URL url = ClassLoader.getSystemResource("config.properties");
        try {
			props.load(url.openStream());
			new Skyper(props).run();
		} catch (IOException e) {
			throw new RuntimeException("Fail load properties", e);
		}	
	}	
}
