package cz.derhaa.jenkins.monitor.sender;

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
