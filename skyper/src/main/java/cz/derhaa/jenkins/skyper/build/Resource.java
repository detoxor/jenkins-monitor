/**
 * 
 */
package cz.derhaa.jenkins.skyper.build;

import java.util.Set;

/**
 * @author derhaa
 *
 */
public interface Resource {

	Set<Build> getBuilds();
}
