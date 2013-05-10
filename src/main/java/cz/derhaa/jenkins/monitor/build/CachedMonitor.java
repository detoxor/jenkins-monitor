package cz.derhaa.jenkins.monitor.build;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cz.derhaa.jenkins.monitor.resource.Resource;
import cz.derhaa.jenkins.monitor.sender.Notice;
import cz.derhaa.jenkins.monitor.sender.SendListener;
import cz.derhaa.jenkins.monitor.util.Tool;

/**
 * @author derhaa
 * 
 */
public class CachedMonitor implements Monitor {

	private static final Logger LOGGER = LoggerFactory.getLogger(CachedMonitor.class);
	private final List<String> jobs;
	private final Map<String, Build> cache;

	public CachedMonitor(final String jobs) {
		this.jobs = Tool.parseString(jobs);
		this.cache = new HashMap<String, Build>();
	}

	@Override
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
							throw new MessengerException("Loop failed", e);
						}
					}
				}
			}).start();
		}
	}

	public final String getStatusMessage(final Status newStatus, final Status oldStatus) {
		final String foo = newStatus.getKey().toLowerCase(Locale.ENGLISH);
		final String hoo = oldStatus == null ? null : oldStatus.getKey().toLowerCase(Locale.ENGLISH);
		String retval = "N/A";
		if (hoo == null) {
			if (foo.equals(Tool.FAIL)) {
				retval = Tool.FAIL_MESSAGE;
			} else if (foo.equals(Tool.UNSTABLE)) {
				retval = Tool.UNSTABLE_MESSAGE;
			} else {
				retval = Tool.SUCCESS_MESSAGE;
			}
		} else {
			if (hoo.equals(Tool.FAIL) && foo.equals(Tool.UNSTABLE)) {// fail->unstable
				retval = Tool.UNSTABLE_MESSAGE;
			} else if (hoo.equals(Tool.FAIL) && foo.equals(Tool.SUCCESS)) {// fail->success
				retval = Tool.FIXED_MESSAGE;
			} else if (hoo.equals(Tool.UNSTABLE) && foo.equals(Tool.SUCCESS)) {// unstable->success
				retval = Tool.STABLE_MESSAGE;
			} else if (hoo.equals(Tool.SUCCESS) && foo.equals(Tool.FAIL)) {// unstable->success
				retval = Tool.FAIL_MESSAGE;
			}  else if (hoo.equals(Tool.SUCCESS) && foo.equals(Tool.UNSTABLE)) {// unstable->success
				retval = Tool.UNSTABLE_MESSAGE;
			} else {
				retval = Tool.SUCCESS_MESSAGE;
			}
		}
		return retval;
	}

	@Override
	public final Map<String, Build> fetchBuilds(List<String> jobs) {
		final Map<String, Build> retval = new HashMap<String, Build>();
		final Set<Build> builds = resource.getBuilds();
		for (Build build : builds) {
			String name = build.getName();
			if(!jobs.contains(name)) continue;
			retval.put(name, build);
		}
		return retval;
	}
	
	// --- private methods
	private final void checkNewBuilds() {
		final Map<String, Build> builds = fetchBuilds(jobs);
		final List<Notice> notices = new ArrayList<Notice>();
		for (Entry<String, Build> entry : builds.entrySet()) {
			final Build build = entry.getValue();
			final String name = build.getName();
			final Status status = build.getLastBuildStatus();
			final Build actual = cache.get(name);
			if (actual == null) {// not exist in cache
				notices.add(new Notice(build, getStatusMessage(status, null)));
			} else if (!actual.getLastBuildStatus().equals(status)) {
				notices.add(new Notice(build, getStatusMessage(status, actual.getLastBuildStatus())));
			} else if (actual.getLastBuildStatus().equals(status) && Tool.FAIL.equals(status)) {
				notices.add(new Notice(build, Tool.FAIL_STILL_MESSAGE));
			}
		}
		listener.sendNotices(notices);
		cache.clear();// = new HashMap<String, Build>(builds);
		cache.putAll(builds);
	}	
	
	// --- IoC
	private Resource resource;
	private SendListener listener;
	
	public void setResource(final Resource resource) {
		this.resource = resource;
	}
	
	public void setSender(final SendListener listener) {
		this.listener = listener;
	}
}
