/**
 * 
 */
package cz.derhaa.jenkins.skyper.build;

import java.util.Date;

/**
 * @author derhaa
 *
 */
public class Build {

	private final String name;
	private final String webUrl;
	private final Integer lastBuildLabel;
	private final Date lastBuildTime;
	private final String lastBuildStatus;
	private final String activity;
	private StringBuilder sb;
	
	public Build(String name, String webUrl, Integer lastBuildLabel, Date lastBuildTime, String lastBuildStatus, String activity) {
		this.name = name;
		this.webUrl = webUrl;
		this.lastBuildLabel = lastBuildLabel;
		this.lastBuildTime = lastBuildTime;
		this.lastBuildStatus = lastBuildStatus;
		this.activity = activity;
		this.sb = new StringBuilder();
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

	public String getLastBuildStatus() {
		return lastBuildStatus;
	}

	public String getActivity() {
		return activity;
	}

	@Override
	public String toString() {
		sb.append("Jenkins Build:").append("\n");
		sb.append("Name: ").append(name).append("\n");
		sb.append("WebUrl: ").append(webUrl).append("\n");
		sb.append("BuildLabel: ").append(lastBuildLabel).append("\n");
		sb.append("BuildTime: ").append(lastBuildTime).append("\n");
		sb.append("BuildStatus: ").append(lastBuildStatus).append("\n");
		sb.append("Activity: ").append(activity);
		return sb.toString();
	}
}
