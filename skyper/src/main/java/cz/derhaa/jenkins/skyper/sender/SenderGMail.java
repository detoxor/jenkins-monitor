package cz.derhaa.jenkins.skyper.sender;

import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author derhaa
 *
 */
public class SenderGMail extends SenderBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SenderGMail.class);
	private InternetAddress[] addresses;
	private StringBuilder stringBuilder = new StringBuilder();
	
	public SenderGMail(List<String> contacts, String jenkinsUrl) {
		super(contacts, jenkinsUrl);
		for (String string : contacts) {
			stringBuilder.append(string).append(",");
		}
		try {
			this.addresses = InternetAddress.parse(stringBuilder.toString());
			stringBuilder.setLength(0);
		} catch (AddressException e) {
			LOGGER.error("Error during parsing emails", e);
		}
	}

	@Override
	public void send(String buildName, String lastBuildLabel, String message) {
		stringBuilder.setLength(0);
		stringBuilder.append("[Jenkins]")
			.append(" [").append(buildName).append("] ")
			.append(message).append(" - ").append(jenkinsUrl).append("/job/")
			.append(buildName).append("/")
			.append(lastBuildLabel);
		
		Properties props = new Properties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.socketFactory.port", "465");
		props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.port", "465");
 
		Session session = Session.getDefaultInstance(props,
			new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("dev.cejkatomas@gmail.com","mMpPsw3J9%a");
				}
			});
		try {
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("spam.cejkatomas@gmail.com"));
			msg.setRecipients(Message.RecipientType.TO,	addresses);
			msg.setSubject("Jenkins - build statuses");
			msg.setText(stringBuilder.toString());
			Transport.send(msg);
			LOGGER.info("Message has been sended to "+addresses.toString()+". Content of message: "+stringBuilder.toString());
		} catch (MessagingException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void notify(String buildName, String lastBuildLabel, String message) {
		send(buildName, lastBuildLabel, message);
	}

}
