package br.gov.frameworkdemoiselle.fuselage.filter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.enumeration.contrib.Comparison;
import br.gov.frameworkdemoiselle.fuselage.business.ResourceBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;

@SessionScoped
public class PublicResources implements Serializable {

	private static final long serialVersionUID = 1L;

	@Inject
	private ResourceBC bc;

	Map<String, List<String>> resourceMap = new HashMap<String, List<String>>();

	@SuppressWarnings("unused")
	@PostConstruct
	private void init() {
		List<String> urls = new ArrayList<String>();
		List<SecurityResource> resources = bc.findResourceByName("public_url");
		for (SecurityResource resource : resources)
			urls.add(resource.getValue());
		resourceMap.put("public_url", urls);

		urls = new ArrayList<String>();
		resources = bc.findResourceByName("public_url_startswith");
		for (SecurityResource resource : resources)
			urls.add(resource.getValue());
		resourceMap.put("public_url_startswith", urls);
	}

	public boolean hasPermission(String resourceName, String resourceValue, Comparison comparison) {
		if (comparison == Comparison.EQUALS && resourceMap.get(resourceName).contains(resourceValue))
			return true;
		if (comparison == Comparison.STARTSWITH) {
			for (String url : resourceMap.get(resourceName))
				if (resourceValue.startsWith(url))
					return true;
		}
		return false;
	}
}
