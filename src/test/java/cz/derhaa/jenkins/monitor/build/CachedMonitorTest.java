package cz.derhaa.jenkins.monitor.build;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import cz.derhaa.jenkins.monitor.build.Build;
import cz.derhaa.jenkins.monitor.build.CachedMonitor;
import cz.derhaa.jenkins.monitor.resource.ResourceBase;
import cz.derhaa.jenkins.monitor.util.Tool;

public class CachedMonitorTest {

	private Set<Build> builds;

	@Before
	public void setUp() throws Exception {
		builds = new HashSet<Build>();
		Date date = new Date();
		Build b1 = new Build("Ninja", "ninja_url", 1, date, Tool.FAIL, "someActivity");
		builds.add(b1);
		Build b2 = new Build("Judo", "judo_url", 2, date, Tool.SUCCESS, "someActivity");
		builds.add(b2);
		Build b3 = new Build("Karate", "karate_url", 3, date, Tool.UNSTABLE, "someActivity");
		builds.add(b3);		
	}

	@Test
	public void testFilteringJobs() {
		String filterCriterium = "Ninja";
		CachedMonitorStub stub = new CachedMonitorStub();
		stub.setResource(new ResourceStub(builds));
		
		Map<String, Build> items = stub.fetchBuilds(Tool.parseString(filterCriterium));
		Assert.assertNotNull(items);
		Assert.assertFalse(items.isEmpty());
		Assert.assertEquals(1, items.size());
		Assert.assertTrue(filterCriterium.equals(items.get(filterCriterium).getName()));
	}
	
	@Test
	public void testStatusMessages() {
		CachedMonitorStub stub = new CachedMonitorStub();
		String success2fail = stub.getStatusMessage(Status.FAIL, Status.SUCCESS);
		Assert.assertEquals(Tool.FAIL_MESSAGE, success2fail);
	}

	// --------------------- Stubs -------------------------
	private static class CachedMonitorStub extends CachedMonitor {
		public CachedMonitorStub() {
			super("");
		}
	}
	
	private static class ResourceStub extends ResourceBase {

		private Set<Build> builds;

		public ResourceStub(Set<Build> builds) {
			super("");
			this.builds = builds;
		}

		@Override
		public Set<Build> getBuilds() {
			return builds;
		}
		
	}
}
