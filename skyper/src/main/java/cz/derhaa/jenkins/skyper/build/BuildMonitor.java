package cz.derhaa.jenkins.skyper.build;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author derhaa
 * 
 */
public class BuildMonitor implements IBuildMonitor {

	private static final Logger logger = LoggerFactory.getLogger(BuildMonitor.class);

	private static final String FAIL = "failure";
	private static final String SUCCESS = "success";
	private static final String UNSTABLE = "unstable";
	private Map<String, Build> cache = new HashMap<String, Build>();
	private final BuildListener listener;
	private final String jenkinsUrl;
	private final int interval;
	private StringBuilder sb;
	private static final String PREFIX = "[Jenkins]";
	

	public BuildMonitor(final BuildListener listener, final String jenkinsUrl, final int interval) {
		this.listener = listener;
		this.jenkinsUrl = jenkinsUrl;
		this.interval = interval;
		this.sb = new StringBuilder();
	}

	public void loop() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					logger.info("Loop run");
					checkNewBuilds();
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						throw new RuntimeException("Loop failed", e);
					}
				}
			}
		}).start();
	}

	private final void checkNewBuilds() {
		Map<String, Build> builds = fetchBuilds();
		for (Entry<String, Build> entry : builds.entrySet()) {
			Build build = entry.getValue();
			String name = build.getName();
			String status = build.getLastBuildStatus();
			Build actual = cache.get(name);
			if (actual == null) {// not exist in cache
				handleNewBuild(build, null);
			} else if (!actual.getLastBuildLabel().equals(status)) {
				handleNewBuild(build, actual.getLastBuildStatus());
			}
		}
		cache = new HashMap<String, Build>(builds);
	}

	private final void handleNewBuild(final Build build, final String oldStatus) {
		String foo = build.getLastBuildStatus().toLowerCase();
		String hoo = oldStatus == null ? null : oldStatus.toLowerCase();
		String event = "N/A";
		sb.setLength(0);
		if (hoo == null) {
			if (foo.equals(FAIL)) {
				event = "(rain), Failed";
			} else if (foo.equals(UNSTABLE)) {
				event = "(tumbleweed), Unstable";
			} else {
				event = "(sun), Success";
			}
		} else {
			if (hoo.equals(FAIL) && foo.equals(FAIL)) {// fail->fail
				event = "(rain), Still failing";
			} else if (hoo.equals(FAIL) && foo.equals(UNSTABLE)) {// fail->unstable
				event = "(tumbleweed), Unstable";
			} else if (hoo.equals(FAIL) && foo.equals(SUCCESS)) {// fail->success
				event = "(sun), Fixed";
			} else {
				event = "(sun), Success";
			}
		}
		sb.append(PREFIX).append(event).append(" ").append(build.getName()).append(" - ").append(jenkinsUrl).append("/job/")
			.append(build.getName()).append("/")
			.append(build.getLastBuildLabel());
		listener.notify(sb.toString());
	}

	private final Map<String, Build> fetchBuilds() {
		Map<String, Build> retval = new HashMap<String, Build>();
		Set<Build> builds = resource.getBuilds();
		for (Build build : builds) {
			retval.put(build.getName(), build);
		}
		return retval;
	}
	
	
	// --- IoC
	private ResourceStrategy resource;
	
	public void setResource(ResourceStrategy resource) {
		this.resource = resource;
	}
}
