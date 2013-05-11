package cz.derhaa.jenkins.monitor.resource;
/**
 * Only keeps jenkins url
 * @author derhaa
 */
abstract public class ResourceBase implements Resource {
	
	protected final String jenkinsUrl;
	/**
	 * @param jenkinsUrl url of server where jenkins works
	 */
	public ResourceBase(String jenkinsUrl) {
		this.jenkinsUrl = jenkinsUrl;
	}

}
