package br.gov.frameworkdemoiselle.fuselage.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.fuselage.business.ResourceBC;
import br.gov.frameworkdemoiselle.fuselage.configuration.FuselageConfig;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;
import br.gov.frameworkdemoiselle.internal.producer.LoggerProducer;

@SessionScoped
public class PublicResources implements Serializable {

	private static final long serialVersionUID = 1L;

	private Logger logger = LoggerProducer.create(PublicResources.class);

	@Inject
	private FuselageConfig config;

	@Inject
	private ResourceBC bc;

	Map<String, List<String>> resourceMap = new HashMap<String, List<String>>();

	@PostConstruct
	protected void init() {
		List<String> urls = config.getUrlsEquals();

		List<SecurityResource> resources;
		try {
			resources = bc.findResourceByName("public_url");
		} catch (RuntimeException e) {
			resources = new ArrayList<SecurityResource>();
			logger.warn("RuntimeException retrieving public_url resources");
		}
		for (SecurityResource resource : resources)
			urls.add(resource.getValue());
		resourceMap.put("public_url", urls);

		urls = config.getUrlsStartswith();
		try {
			resources = bc.findResourceByName("public_url_startswith");
		} catch (RuntimeException e) {
			resources = new ArrayList<SecurityResource>();
			logger.warn("RuntimeException retrieving public_url_startswith resources");
		}
		for (SecurityResource resource : resources)
			urls.add(resource.getValue());
		resourceMap.put("public_url_startswith", urls);
	}

	public boolean hasPermissionEquals(String resourceValue) {
		if (resourceMap.get("public_url").contains(resourceValue))
			return true;
		return false;
	}

	public boolean hasPermissionStartswith(String resourceValue) {
		for (String url : resourceMap.get("public_url_startswith")) {
			if (url != null && !url.trim().isEmpty() && resourceValue.startsWith(url))
				return true;
		}
		return false;
	}

}
