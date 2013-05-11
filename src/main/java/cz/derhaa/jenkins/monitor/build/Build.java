package cz.derhaa.jenkins.monitor.build;

import java.util.Date;

/**
 * Basic imutable pojo for jenkins build object
 * @author derhaa
 */
public class Build {

	private final String name;
	private final String webUrl;
	private final Integer lastBuildLabel;
	private final Date lastBuildTime;
	private final String lastBuildStatus;
	private final String activity;
	private final StringBuilder stringBuilder;
	/**
	 * Complete describe of jenkins build
	 * @param name
	 * @param webUrl
	 * @param lastBuildLabel
	 * @param lastBuildTime
	 * @param lastBuildStatus
	 * @param activity
	 */
	public Build(final String name, final String webUrl, final Integer lastBuildLabel, final Date lastBuildTime, final String lastBuildStatus, final String activity) {
		this.name = name;
		this.webUrl = webUrl;
		this.lastBuildLabel = lastBuildLabel;
		this.lastBuildTime = lastBuildTime;
		this.lastBuildStatus = lastBuildStatus;
		this.activity = activity;
		this.stringBuilder = new StringBuilder();
	}

	public String getName() {
		return name;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public Integer getLastBuildLabel() {
		return lastBuildLabel;
	}

	public Date getLastBuildTime() {
		return lastBuildTime;
	}

	public Status getLastBuildStatus() {
		for (Status status : Status.values()) {
			if(status.getKey().equals(lastBuildStatus)) return status;
		}
		return null;
	}

	public String getActivity() {
		return activity;
	}

	@Override
	public String toString() {
		stringBuilder.append("Jenkins Build:").append("\n");
		stringBuilder.append("Name: ").append(name).append("\n");
		stringBuilder.append("WebUrl: ").append(webUrl).append("\n");
		stringBuilder.append("BuildLabel: ").append(lastBuildLabel).append("\n");
		stringBuilder.append("BuildTime: ").append(lastBuildTime).append("\n");
		stringBuilder.append("BuildStatus: ").append(lastBuildStatus).append("\n");
		stringBuilder.append("Activity: ").append(activity);
		return stringBuilder.toString();
	}
}
