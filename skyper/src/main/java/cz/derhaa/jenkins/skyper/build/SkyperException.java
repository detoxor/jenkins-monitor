package cz.derhaa.jenkins.skyper.build;

@SuppressWarnings("serial")
public class SkyperException extends RuntimeException {

	public SkyperException(String message) {
		super(message);
	}

	public SkyperException(final Throwable cause) {
		super(cause);
	}

	public SkyperException(final String message, final Throwable cause) {
		super(message, cause);
	}

}
