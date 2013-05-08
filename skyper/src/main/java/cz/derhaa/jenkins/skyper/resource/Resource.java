/**
 * 
 */
package cz.derhaa.jenkins.skyper.resource;

import java.util.Set;

import cz.derhaa.jenkins.skyper.build.Build;

/**
 * @author derhaa
 *
 */
public interface Resource {

	Set<Build> getBuilds();
}
