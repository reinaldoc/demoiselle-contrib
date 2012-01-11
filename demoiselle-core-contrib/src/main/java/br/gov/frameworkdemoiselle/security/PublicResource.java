package br.gov.frameworkdemoiselle.security;

import java.io.Serializable;
import java.util.List;

/**
 * Defines the methods that should be implemented by anyone who wants an authorization mechanism.
 * 
 * @author SERPRO
 */
public interface PublicResource extends Serializable {

	List<String> getResources(String resourceType);

}
