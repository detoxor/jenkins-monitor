package cz.derhaa.jenkins.skyper.build;

/**
 * @author derhaa
 *
 */
public interface BuildListener {
	/**
	 * @param message
	 */
	void notify(String message);
}
