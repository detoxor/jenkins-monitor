package cz.derhaa.jenkins.monitor;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import cz.derhaa.jenkins.monitor.build.MessengerException;
/**
 * Only load configuration properties
 * @author derhaa
 */
public class BaseTest {

	protected Properties properties;

	public BaseTest() {
		//load program properties
		this.properties = new Properties();
		FileInputStream fis = null;
		try {
			String filePath = "src/test/resources/config.properties";
			fis = new FileInputStream(new File(filePath));
			properties.load(fis);
		} catch (IOException e) {
			throw new MessengerException("Fail load properties", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
				} catch (IOException e) {
					//ignore close failing
				}
			}
		}
	}
}
