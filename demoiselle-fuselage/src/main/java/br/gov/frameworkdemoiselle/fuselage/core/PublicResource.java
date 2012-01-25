package br.gov.frameworkdemoiselle.fuselage.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.ResourceBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityResource;

@Alternative
public class PublicResource implements br.gov.frameworkdemoiselle.security.PublicResource {

	private static final long serialVersionUID = 1L;

	@Inject
	private ResourceBC resourceBC;

	private Map<String, List<String>> publicResourceMap = new HashMap<String, List<String>>();

	private List<String> publicResourceLoaded = new ArrayList<String>();

	@Override
	public List<String> getResources(String resourceName) {
		if (!publicResourceLoaded.contains(resourceName)) {
			publicResourceLoaded.add(resourceName);
			List<String> resourceList = new ArrayList<String>();
			List<SecurityResource> resultList = resourceBC.findByExample(new SecurityResource(resourceName), true, 0);
			if (resultList != null) {
				ListIterator<SecurityResource> iter = resultList.listIterator();
				while (iter.hasNext()) {
					resourceList.add(iter.next().getValue());
				}
			}
			publicResourceMap.put(resourceName, resourceList);
		}
		return publicResourceMap.get(resourceName);
	}

}
