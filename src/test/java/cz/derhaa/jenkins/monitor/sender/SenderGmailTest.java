package cz.derhaa.jenkins.monitor.sender;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import cz.derhaa.jenkins.monitor.BaseTest;
import cz.derhaa.jenkins.monitor.build.Build;
import cz.derhaa.jenkins.monitor.util.Tool;

@RunWith(PowerMockRunner.class) 
@PrepareForTest(SenderGMailSSL.class)
public class SenderGmailTest extends BaseTest {

	private SenderGMailSSL testedObject;

	@Before
	public void setUp() throws Exception {
		properties.setProperty(Tool.CONTACTS, "spam.cejkatomas@gmail.com");
		testedObject = new SenderGMailSSL(properties);
	}

	@Test
	public void testSendNotice() throws Exception {
		Date date = new Date();
		Build b1 = new Build("Build_B1", "B1_url_test", 1, date, Tool.FAIL, "someActivity");
		Build b2 = new Build("Build_B2", "B2_url_test", 2, date, Tool.SUCCESS, "someActivity");
		Build b3 = new Build("Build_B3", "B3_url_test", 3, date, Tool.UNSTABLE, "someActivity");
		List<Notice> notices = Arrays.asList(
			new Notice(b1, Tool.FAIL_MESSAGE),
			new Notice(b2, Tool.SUCCESS_MESSAGE),
			new Notice(b3, Tool.UNSTABLE_MESSAGE)
        );
		
		//Mock address
		InternetAddress[] addresses = new InternetAddress[1];
		addresses[0] = new InternetAddress("spam.cejkatomas@gmail.com");
		EasyMock.expect(InternetAddress.parse("spam.cejkatomas@gmail.com")).andReturn(addresses);
		EasyMock.expectLastCall();
		// Mock mail.session
		EasyMock.expect(Session.getDefaultInstance(null)).andReturn(Session.getDefaultInstance(new Properties(), new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(properties.getProperty(Tool.SENDER_MAIL), properties
							.getProperty(Tool.SENDER_EMAIL_PASS));// ""
				}
			}));
		// Mock mime-message
		MimeMessage ms = PowerMock.createMockAndExpectNew(MimeMessage.class);
		
		EasyMock.replay(ms);
		//test only if code works without output errors
		testedObject.sendNotices(notices);
		EasyMock.verify(ms);
	}

}
