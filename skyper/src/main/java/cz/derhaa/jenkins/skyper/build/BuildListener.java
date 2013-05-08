package cz.derhaa.jenkins.skyper.build;

/**
 * @author derhaa
 *
 */
public interface BuildListener {
	/**
	 * 
	 * @param buildName
	 * @param lastBuildLabel
	 * @param message
	 */
	void notify(String buildName, String lastBuildLabel, String message);
}
