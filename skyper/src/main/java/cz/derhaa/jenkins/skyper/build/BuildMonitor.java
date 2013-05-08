package cz.derhaa.jenkins.skyper.build;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author derhaa
 * 
 */
public class BuildMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(BuildMonitor.class);

	private static final String FAIL = "failure";
	private static final String SUCCESS = "success";
	private static final String UNSTABLE = "unstable";
	private Map<String, Build> cache = new HashMap<String, Build>();
	
	private final String jenkinsUrl;
	private final int interval;
	private final StringBuilder stringBuilder;
	private static final String PREFIX = "[Jenkins]";
	

	public BuildMonitor(final String jenkinsUrl, final int interval) {
		this.jenkinsUrl = jenkinsUrl;
		this.interval = interval;
		this.stringBuilder = new StringBuilder();
	}

	public void loop() {
		new Thread(new Runnable() {
			public void run() {
				while (true) {
					LOGGER.info("Loop run");
					checkNewBuilds();
					try {
						Thread.sleep(interval);
					} catch (InterruptedException e) {
						throw new SkyperException("Loop failed", e);
					}
				}
			}
		}).start();
	}

	private final void checkNewBuilds() {
		final Map<String, Build> builds = fetchBuilds();
		for (Entry<String, Build> entry : builds.entrySet()) {
			final Build build = entry.getValue();
			final String name = build.getName();
			final String status = build.getLastBuildStatus();
			final Build actual = cache.get(name);
			if (actual == null) {// not exist in cache
				handleNewBuild(build, null);
			} else if (!actual.getLastBuildLabel().equals(status)) {
				handleNewBuild(build, actual.getLastBuildStatus());
			}
		}
		cache = new HashMap<String, Build>(builds);
	}

	private final void handleNewBuild(final Build build, final String oldStatus) {
		final String foo = build.getLastBuildStatus().toLowerCase();
		final String hoo = oldStatus == null ? null : oldStatus.toLowerCase(Locale.ENGLISH);
		String event = "N/A";
		stringBuilder.setLength(0);
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
		stringBuilder.append(PREFIX).append(event).append(" ").append(build.getName()).append(" - ").append(jenkinsUrl).append("/job/")
			.append(build.getName()).append("/")
			.append(build.getLastBuildLabel());
		listener.notify(stringBuilder.toString());
	}

	private final Map<String, Build> fetchBuilds() {
		final Map<String, Build> retval = new HashMap<String, Build>();
		final Set<Build> builds = resource.getBuilds();
		for (Build build : builds) {
			retval.put(build.getName(), build);
		}
		return retval;
	}
	
	
	// --- IoC
	private Resource resource;
	private BuildListener listener;
	
	public void setResource(final Resource resource) {
		this.resource = resource;
	}
	
	public void setListener(final BuildListener listener) {
		this.listener = listener;
	}
}
