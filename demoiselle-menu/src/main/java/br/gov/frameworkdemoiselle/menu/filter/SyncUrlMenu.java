package br.gov.frameworkdemoiselle.menu.filter;

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

import br.gov.frameworkdemoiselle.menu.configuration.MenuContextConfig;
import br.gov.frameworkdemoiselle.menu.core.MenuContext;

@WebFilter(urlPatterns = { "/*" }, dispatcherTypes = { DispatcherType.FORWARD, DispatcherType.REQUEST })
public class SyncUrlMenu implements Filter {

	@Inject
	private MenuContext menuContext;

	@Inject
	private MenuContextConfig config;

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void destroy() {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		if (config.isFilterEnabled()) {
			String url = ((HttpServletRequest) request).getRequestURI().replaceAll("^/.+?/", "/");
			String itemName = config.getFilterMenuItem(url);
			if (itemName != null)
				menuContext.select(config.getFilterMenuName(), itemName);
		}
		chain.doFilter(request, response);
	}

}
