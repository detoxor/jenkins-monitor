/**
 * 
 */
package cz.derhaa.jenkins.messenger.resource;

import java.util.Set;

import cz.derhaa.jenkins.messenger.build.Build;

/**
 * @author derhaa
 *
 */
public interface Resource {

	Set<Build> getBuilds();
}
