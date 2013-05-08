package cz.derhaa.jenkins.skyper.build;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.derhaa.jenkins.skyper.resource.Resource;

/**
 * @author derhaa
 * 
 */
public class BuildMonitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(BuildMonitor.class);

	private static final String FAIL = "failure";
	private static final String FAIL_MESSAGE = "(rain) Failed";
	private static final String FAIL_STILL_MESSAGE = "(rain) Still failing";
	private static final String SUCCESS = "success";
	private static final String SUCCESS_MESSAGE = "(sun) Success";
	private static final String FIXED_MESSAGE = "(sun) Fixed";
	private static final String UNSTABLE = "unstable";
	private static final String UNSTABLE_MESSAGE = "(tumbleweed) Unstable";
	private static final String STABLE_MESSAGE = "(sun) Return to Success";
	private Map<String, Build> cache = new HashMap<String, Build>();
	
	private final StringBuilder stringBuilder;
	private List<String> excludeJobs;

	public BuildMonitor(final List<String> excludeJobs) {
		this.stringBuilder = new StringBuilder();
		this.excludeJobs = excludeJobs;
	}

	public final void run(final Integer interval) {
		if (interval == null) {
			checkNewBuilds();
		} else {
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
	}

	private final void checkNewBuilds() {
		final Map<String, Build> builds = fetchBuilds();
		for (Entry<String, Build> entry : builds.entrySet()) {
			final Build build = entry.getValue();
			final String name = build.getName();
			final String status = build.getLastBuildStatus();
			final Build actual = cache.get(name);
			if (actual == null) {// not exist in cache
				listener.notify(build.getName(), build.getLastBuildLabel().toString(), handleStatus(status, null));
			} else if (!actual.getLastBuildStatus().equals(status)) {
				listener.notify(build.getName(), build.getLastBuildLabel().toString(), handleStatus(status, actual.getLastBuildStatus()));
			} else if (actual.getLastBuildStatus().equals(status) && FAIL.equals(status)) {
				listener.notify(name, build.getLastBuildLabel().toString(), FAIL_STILL_MESSAGE);				
			}
		}
		cache = new HashMap<String, Build>(builds);
	}

	private final String handleStatus(final String newStatus, final String oldStatus) {
		final String foo = newStatus.toLowerCase(Locale.ENGLISH);
		final String hoo = oldStatus == null ? null : oldStatus.toLowerCase(Locale.ENGLISH);
		String retval = "N/A";
		stringBuilder.setLength(0);
		if (hoo == null) {
			if (foo.equals(FAIL)) {
				retval = FAIL_MESSAGE;
			} else if (foo.equals(UNSTABLE)) {
				retval = UNSTABLE_MESSAGE;
			} else {
				retval = SUCCESS_MESSAGE;
			}
		} else {
			if (hoo.equals(FAIL) && foo.equals(UNSTABLE)) {// fail->unstable
				retval = UNSTABLE_MESSAGE;
			} else if (hoo.equals(FAIL) && foo.equals(SUCCESS)) {// fail->success
				retval = FIXED_MESSAGE;
			} else if (hoo.equals(UNSTABLE) && foo.equals(SUCCESS)) {// unstable->success
				retval = STABLE_MESSAGE;
			} else if (hoo.equals(SUCCESS) && foo.equals(FAIL)) {// unstable->success
				retval = FAIL_MESSAGE;
			}  else if (hoo.equals(SUCCESS) && foo.equals(UNSTABLE)) {// unstable->success
				retval = UNSTABLE_MESSAGE;
			} else {
				retval = SUCCESS_MESSAGE;
			}
		}
		return retval;
	}

	private final Map<String, Build> fetchBuilds() {
		final Map<String, Build> retval = new HashMap<String, Build>();
		final Set<Build> builds = resource.getBuilds();
		for (Build build : builds) {
			String name = build.getName();
			if(excludeJobs.contains(name)) continue;
			retval.put(name, build);
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
