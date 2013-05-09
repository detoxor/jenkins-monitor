/**
 * 
 */
package cz.derhaa.jenkins.messenger.resource;

import java.util.Set;

import cz.derhaa.jenkins.messenger.build.Build;

/**
 * Implementation of resource must specify how to mining metadata about builds
 * @author derhaa
 */
public interface Resource {
	/**
	 * @return list builds given jenkins server
	 */
	Set<Build> getBuilds();
}
