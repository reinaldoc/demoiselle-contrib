package br.gov.frameworkdemoiselle.fuselage.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.gov.frameworkdemoiselle.annotation.Ignore;
import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(resource = "demoiselle", prefix = "fuselage.")
public class FuselageConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Name("filter.public.urls.equals")
	private List<String> urlsEquals = new ArrayList<String>();

	@Name("filter.public.urls.startswith")
	private List<String> urlsStartswith = new ArrayList<String>();

	@Name("view.resource.namesuggestions")
	private List<String> namesuggestions;

	@Name("view.profiledetect.implementations")
	private List<String> implementationList;

	@Ignore
	private Map<String, String> implementations;

	public List<String> getUrlsEquals() {
		return new ArrayList<String>(urlsEquals);
	}

	public List<String> getUrlsStartswith() {
		return new ArrayList<String>(urlsStartswith);
	}

	public List<String> getNamesuggestions() {
		return namesuggestions;
	}

	public void setNamesuggestions(List<String> namesuggestions) {
		this.namesuggestions = namesuggestions;
	}

	public Map<String, String> getImplementations() {
		if (implementations == null && implementationList != null) {
			implementations = new HashMap<String, String>();
			for (String impl : implementationList) {
				String[] implList = impl.split(":");
				if (implList.length == 1)
					implementations.put(implList[0], null);
				else if (implList.length != 0)
					implementations.put(implList[0], implList[1]);
			}
		}
		return implementations;
	}

	public void setImplementations(Map<String, String> implementations) {
		this.implementations = implementations;
	}

}
