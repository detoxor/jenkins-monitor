package cz.derhaa.jenkins.monitor.build;

import java.util.List;
import java.util.Map;

public interface Monitor {
	/**
	 * Can be null and monitor invoke request only one
	 * @param interval it allow to define interval to request server
	 */
	public void run(final Integer interval);
	/**
	 * @param jobs filter, work only with given jobs, if null, work with all
	 * @return map of jenkins builds by job
	 */
	public Map<String, Build> fetchBuilds(List<String> jobs);
	/**
	 * Each monitor can define own message strategy
	 * @param newStatus
	 * @param oldStatus
	 * @return final message by given statuses
	 */
	public String getStatusMessage(final Status newStatus, final Status oldStatus);
}
