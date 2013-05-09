package cz.derhaa.jenkins.monitor.sender;

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
import cz.derhaa.jenkins.monitor.build.Build;
import cz.derhaa.jenkins.monitor.build.MessengerException;
import cz.derhaa.jenkins.monitor.util.Tool;
/**
 * Basic implementation of sending messages via ssl based gmail smtp server.
 * @author derhaa
 */
public class SenderGMailSSL extends SenderBase {

	private static final Logger LOGGER = LoggerFactory.getLogger(SenderGMailSSL.class);
	private InternetAddress[] addresses;
	private final StringBuilder stringBuilder = new StringBuilder();
	private MimeMessage mimeMsg;
	/**
	 * Send given contacts messages about jenkins build statuses
	 * @param contacts emails of peoples
	 * @param properties
	 */
	public SenderGMailSSL(final Properties properties) {
		super(properties);
		try {
			this.addresses = InternetAddress.parse(properties.getProperty(Tool.CONTACTS));
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
						return new PasswordAuthentication(properties.getProperty(Tool.SENDER_MAIL),properties.getProperty(Tool.SENDER_EMAIL_PASS));//""
					}
				}
			);
			mimeMsg = new MimeMessage(session);
			mimeMsg.setFrom(new InternetAddress(properties.getProperty(Tool.SENDER_MAIL)));
			mimeMsg.setRecipients(Message.RecipientType.TO,	addresses);
			mimeMsg.setSubject("Jenkins - build statuses");
		} catch (AddressException e) {
			LOGGER.error("Error during parsing emails", e);
			throw new MessengerException(e);
		} catch (MessagingException e) {
			LOGGER.error("Settings of mail failed", e);
			throw new MessengerException(e);
		}
	}
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void sendNotices(List<Notice> notices) {
		stringBuilder.setLength(0);
		for (Notice notice : notices) {
			Build build = notice.getBuild();
			String buildName = build.getName();
			String lastBuildLabel = build.getLastBuildLabel().toString();
			String message = notice.getMessage();
			stringBuilder.append("[Jenkins]")
				.append(" [").append(buildName).append("] ")
				.append(message).append(" - ").append(jenkinsUrl).append("/job/")
				.append(buildName).append("/")
				.append(lastBuildLabel).append("\n\n");			
		}
		try {
			mimeMsg.setText(stringBuilder.toString());
			Transport.send(mimeMsg);
			LOGGER.info("Message has been sended to "+addresses.toString()+". Content of message: "+stringBuilder.toString());
		} catch (MessagingException e) {
			LOGGER.error("Send of mail failed", e);
			throw new MessengerException(e);
		}		
	}
}
