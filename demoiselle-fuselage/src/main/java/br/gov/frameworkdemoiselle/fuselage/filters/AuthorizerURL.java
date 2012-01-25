package br.gov.frameworkdemoiselle.fuselage.filters;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
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

import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.internal.configuration.JsfSecurityConfig;
import br.gov.frameworkdemoiselle.internal.producer.LoggerProducer;
import br.gov.frameworkdemoiselle.security.SecurityContext;

@WebFilter(urlPatterns = { "/*" })
public class AuthorizerURL implements Filter {

	private Logger logger = LoggerProducer.create(AuthorizerURL.class);

	@Inject
	private SecurityContext securityContext;

	@Inject
	private JsfSecurityConfig config;

	private HttpServletRequest request;

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		this.request = (HttpServletRequest) request;
		String url = this.request.getRequestURI().replaceAll("^/.+?/", "/");

		List<String> publicurls = securityContext.getPublicResources("public_url_startswith");
		if (publicurls != null) {
			for (String publicurl : publicurls) {
				if (url.startsWith(publicurl)) {
					info("permitted by public resource", url);
					chain.doFilter(request, response);
					return;
				}
			}
		}

		if (securityContext.getPublicResources("public_url").contains(url) || url.equals(config.getLoginPage())) {
			info("permitted by public resource", url);
			chain.doFilter(request, response);
			return;
		}

		if (!securityContext.isLoggedIn()) {
			info("denied by not logged in, redirect to login page", url);
			redirect(response, getContext() + config.getLoginPage());
			chain.doFilter(request, response);
			return;
		}

		if (securityContext.hasPermission("private_url", url)) {
			info("permitted by resource", url);
		} else {
			info("denied by resource, redirect to welcome page", url);
			redirect(response, getContext() + config.getLoginPage());
		}
		chain.doFilter(request, response);
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
