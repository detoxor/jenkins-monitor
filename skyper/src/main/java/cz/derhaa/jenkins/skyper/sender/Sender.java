package cz.derhaa.jenkins.skyper.sender;

import cz.derhaa.jenkins.skyper.build.BuildListener;
/**
 * 
 * @author derhaa
 *
 */
public interface Sender extends BuildListener {
	/**
	 * 
	 * @param buildName
	 * @param lastBuildLabel
	 * @param message
	 */
	void send(String buildName, String lastBuildLabel, String message);
}
