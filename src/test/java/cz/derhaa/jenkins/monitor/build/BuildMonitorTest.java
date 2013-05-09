package cz.derhaa.jenkins.monitor.build;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import cz.derhaa.jenkins.monitor.build.Build;
import cz.derhaa.jenkins.monitor.build.BuildMonitor;
import cz.derhaa.jenkins.monitor.resource.ResourceBase;
import cz.derhaa.jenkins.monitor.util.Tool;

public class BuildMonitorTest {

	private BuildMonitorStub testedObject;
	private Set<Build> builds;

	@Before
	public void setUp() throws Exception {
		testedObject = new BuildMonitorStub();
		builds = new HashSet<Build>();
	}

	@Test
	public void test() {
		Date date = new Date();
		Build b1 = new Build("Build_B1", "B1_url_test", 1, date, Tool.FAIL, "someActivity");
		builds.add(b1);
		Build b2 = new Build("Build_B2", "B2_url_test", 2, date, Tool.SUCCESS, "someActivity");
		builds.add(b2);
		Build b3 = new Build("Build_B3", "B3_url_test", 3, date, Tool.UNSTABLE, "someActivity");
		builds.add(b3);
		testedObject.setResource(new ResourceStub(builds));
	}

	// --------------------- Stubs -------------------------
	private static class BuildMonitorStub extends BuildMonitor {
		public BuildMonitorStub() {
			super("");
		}
	}
	
	private static class ResourceStub extends ResourceBase {

		private Set<Build> builds;

		public ResourceStub(Set<Build> builds) {
			super("http://jenkinsurl");
			this.builds = builds;
		}

		@Override
		public Set<Build> getBuilds() {
			return builds;
		}
		
	}
}
