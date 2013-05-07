package cz.derhaa.skyper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cz.derhaa.jenkins.skyper.build.BuildListener;
import cz.derhaa.jenkins.skyper.build.BuildMonitor;

/**
 * @author derhaa
 *
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(BuildMonitor.class)
public class BuildMonitorTest {

	private BuildMonitor builderMonitor;
	
	private static final String URL = "http://localhost:8080/jenkins/cc.xml";

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		builderMonitor = new BuildMonitor(new BuildListener() {
			public void notify(String message) {
				// do nothing...
			}
		}, URL, 6000);
	}

	@Test
	public final void testFetchBuilds() {
		builderMonitor.fetchBuilds();
	}
}
