package cz.derhaa.jenkins.messenger.sender;

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
	private final StringBuilder stringBuilder = new StringBuilder();
	private MimeMessage mimeMsg;
	
	public SenderGMail(final List<String> contacts, final Properties properties) {
		super(contacts, properties);
		for (String string : contacts) {
			stringBuilder.append(string).append(",");
		}
		try {
			this.addresses = InternetAddress.parse(stringBuilder.toString());
			stringBuilder.setLength(0);
			Properties props = new Properties();
			props.put("mail.smtp.host", "smtp.gmail.com");
			props.put("mail.smtp.socketFactory.port", "465");
			props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
			props.put("mail.smtp.auth", "true");
			props.put("mail.smtp.port", "465");
			Session session = Session.getDefaultInstance(props,
				new javax.mail.Authenticator() {
					protected PasswordAuthentication getPasswordAuthentication() {
						return new PasswordAuthentication(properties.getProperty("user.mail"),properties.getProperty("user.pass"));//""
					}
				}
			);
			mimeMsg = new MimeMessage(session);
			mimeMsg.setFrom(new InternetAddress(properties.getProperty("user.mail")));
			mimeMsg.setRecipients(Message.RecipientType.TO,	addresses);
			mimeMsg.setSubject("Jenkins - build statuses");
		} catch (AddressException e) {
			LOGGER.error("Error during parsing emails", e);
		} catch (MessagingException e) {
			LOGGER.error("Settings of mail failed", e);
		}
	}

	@Override
	public void notify(String buildName, String lastBuildLabel, String lastBuildStatus, String message) {
		stringBuilder.setLength(0);
		stringBuilder.append("[Jenkins]")
			.append(" [").append(buildName).append("] ")
			.append(message).append(" - ").append(jenkinsUrl).append("/job/")
			.append(buildName).append("/")
			.append(lastBuildLabel).append("\n");
		try {
			mimeMsg.setText(stringBuilder.toString());
			Transport.send(mimeMsg);
			LOGGER.info("Message has been sended to "+addresses.toString()+". Content of message: "+stringBuilder.toString());
			stringBuilder.setLength(0);
		} catch (MessagingException e) {
			LOGGER.error("Send of mail failed", e);
		}
	}
}
