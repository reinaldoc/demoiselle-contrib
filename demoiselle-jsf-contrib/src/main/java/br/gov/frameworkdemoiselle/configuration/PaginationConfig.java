package br.gov.frameworkdemoiselle.configuration;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ApplicationScoped
public class PaginationConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private br.gov.frameworkdemoiselle.internal.configuration.PaginationConfig paginationConfig;

	public int getPageSize() {
		return paginationConfig.getPageSize();
	}

	public int getMaxPageLinks() {
		return paginationConfig.getMaxPageLinks();
	}

}
