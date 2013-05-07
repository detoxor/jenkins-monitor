package cz.derhaa.jenkins.skyper.build;

/**
 * @author derhaa
 *
 */
public interface BuildListener {
	void notify(String message);
}
