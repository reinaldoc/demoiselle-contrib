package br.gov.frameworkdemoiselle.fuselage.filter;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.enumeration.contrib.Comparison;
import br.gov.frameworkdemoiselle.fuselage.configuration.WebfilterConfig;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.internal.producer.LoggerProducer;
import br.gov.frameworkdemoiselle.security.SecurityContext;

@WebFilter(urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.FORWARD, DispatcherType.REQUEST })
public class AuthorizerURL implements Filter {

	private Logger logger = LoggerProducer.create(AuthorizerURL.class);

	@Inject
	private SecurityContext securityContext;

	@Inject
	private PublicResources publicResources;

	@Inject
	private WebfilterConfig config;

	private HttpServletRequest request;

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (!config.isWebfilterEnabled()) {
			chain.doFilter(request, response);
			return;
		}

		this.request = (HttpServletRequest) request;
		String url = this.request.getRequestURI().replaceAll("^/.+?/", "/");

		if (isPublicURL(url) || hasPermission(url))
			chain.doFilter(request, response);
		else
			redirect(response, getContext() + config.getLoginPage());
	}

	private boolean isPublicURL(String url) {
		if (publicResources.hasPermission("public_url", url, Comparison.EQUALS) || url.equals(config.getLoginPage())) {
			info("permitted by public_url", url);
			return true;
		} else if (publicResources.hasPermission("public_url_startswith", url, Comparison.STARTSWITH)) {
			info("permitted by public_url_startswith", url);
			return true;
		}
		return false;
	}

	private boolean hasPermission(String url) {
		if (!securityContext.isLoggedIn()) {
			info("denied by not logged in, redirect to login page", url);
			return false;
		}

		if (securityContext.hasPermission("private_url", url)) {
			info("permitted by resource", url);
			return true;
		} else {
			info("denied by resource, redirect to welcome page", url);
			return false;
		}
	}

	private void redirect(ServletResponse response, String url) throws IOException {
		((HttpServletResponse) response).sendRedirect(url);
	}

	private void info(String message, String url) {
		logger.info(message + ": " + getUsername() + "@" + getSource() + getContext() + url);
	}

	private String getUsername() {
		try {
			if (securityContext.isLoggedIn())
				return ((SecurityUser) securityContext.getUser().getAttribute("user")).getLogin();
		} catch (Exception e) {
			// Ignore
		}
		return null;
	}

	private String getContext() {
		if (request.getServletContext() == null)
			return null;
		return request.getServletContext().getContextPath();
	}

	private String getSource() {
		return request.getRemoteHost() + ":" + request.getRemotePort();
	}
}
