package cz.derhaa.jenkins.messenger.sender;

/**
 * 
 * @author derhaa
 *
 */
public interface Sender {
	/**
	 * 
	 * @param buildName
	 * @param lastBuildLabel
	 * @param lastBuildLabel
	 * @param message
	 */
	void notify(String buildName, String lastBuildLabel, String lastBuildStatus, String message);
}
