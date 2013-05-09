package cz.derhaa.jenkins.monitor.build;

@SuppressWarnings("serial")
public class MessengerException extends RuntimeException {

	public MessengerException(String message) {
		super(message);
	}

	public MessengerException(final Throwable cause) {
		super(cause);
	}

	public MessengerException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
