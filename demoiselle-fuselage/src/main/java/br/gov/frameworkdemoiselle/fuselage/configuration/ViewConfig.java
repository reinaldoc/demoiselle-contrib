package br.gov.frameworkdemoiselle.fuselage.configuration;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.gov.frameworkdemoiselle.annotation.Ignore;
import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(resource = "fuselage", prefix = "fuselage.view")
public class ViewConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Name("resource.namesuggestions")
	private List<String> namesuggestions;

	@Name("profiledetect.implementations")
	private List<String> implementationList;

	@Ignore
	private Map<String, String> implementations;

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
