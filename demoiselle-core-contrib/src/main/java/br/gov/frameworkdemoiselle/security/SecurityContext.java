package br.gov.frameworkdemoiselle.security;

import java.io.Serializable;
import java.util.List;


/**
 * Structure used to handle both authentication and authorizations mechanisms.
 * 
 * @author SERPRO
 */
public interface SecurityContext extends Serializable {

	/**
	 * Executes the login of a user to the application.
	 */
	void login();

	/**
	 * Executes the logout of a user.
	 * 
	 * @throws NotLoggedInException
	 *             if there is no user logged in a specific session
	 */
	void logout() throws NotLoggedInException;

	/**
	 * Checks if a specific user is logged in.
	 * 
	 * @return {@code true} if the user is logged in
	 */
	boolean isLoggedIn();

	/**
	 * Checks if the logged user has permission to execute an specific operation on a specific resource.
	 * 
	 * @param resource
	 *            resource to be checked
	 * @param operation
	 *            operation to be checked
	 * @return {@code true} if the user has the permission
	 * @throws NotLoggedInException
	 *             if there is no user logged in a specific session.
	 */
	boolean hasPermission(String resource, String operation) throws NotLoggedInException;

	/**
	 * Checks if the logged user has an specific role
	 * 
	 * @param role
	 *            role to be checked
	 * @return {@code true} if the user has the role
	 * @throws NotLoggedInException
	 *             if there is no user logged in a specific session.
	 */
	boolean hasRole(String role) throws NotLoggedInException;

	/**
	 * Return the user logged in the session.
	 * 
	 * @return the user logged in a specific session. If there is no active session returns {@code null}
	 */
	User getUser();
	
	List<String> getPublicResources(String resourceName);

}
