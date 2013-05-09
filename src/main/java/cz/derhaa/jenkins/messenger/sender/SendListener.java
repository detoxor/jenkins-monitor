package cz.derhaa.jenkins.messenger.sender;

import java.util.List;
/**
 * 
 * @author derhaa
 *
 */
public interface SendListener {
	/**
	 * @param notices
	 */
	void sendNotices(List<Notice> notices);
}
