package cz.derhaa.jenkins.skyper.resource;
/**
 * @author derhaa
 *
 */
abstract public class ResourceBase implements Resource {
	
	protected final String jenkinsUrl;

	public ResourceBase(String jenkinsUrl) {
		this.jenkinsUrl = jenkinsUrl;
	}

}
