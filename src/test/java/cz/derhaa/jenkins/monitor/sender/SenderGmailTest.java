package cz.derhaa.jenkins.monitor.sender;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import cz.derhaa.jenkins.monitor.BaseTest;
import cz.derhaa.jenkins.monitor.build.Build;
import cz.derhaa.jenkins.monitor.sender.Notice;
import cz.derhaa.jenkins.monitor.sender.SenderGMailSSL;
import cz.derhaa.jenkins.monitor.util.Tool;

public class SenderGmailTest extends BaseTest {

	private SenderGMailSSL testedObject;

	@Before
	public void setUp() throws Exception {
		properties.setProperty(Tool.CONTACTS, "spam.cejkatomas@gmail.com");
		testedObject = new SenderGMailSSL(properties);	
	}

	@Test
	public void testSendNotice() {
		Date date = new Date();
		Build b1 = new Build("Build_B1", "B1_url_test", 1, date, Tool.FAIL, "someActivity");
		Build b2 = new Build("Build_B2", "B2_url_test", 2, date, Tool.SUCCESS, "someActivity");
		Build b3 = new Build("Build_B3", "B3_url_test", 3, date, Tool.UNSTABLE, "someActivity");
		List<Notice> notices = Arrays.asList(
			new Notice(b1, Tool.FAIL_MESSAGE),
			new Notice(b2, Tool.SUCCESS_MESSAGE),
			new Notice(b3, Tool.UNSTABLE_MESSAGE)
        );
		//test only if code works without output errors
		testedObject.sendNotices(notices);
	}

}
