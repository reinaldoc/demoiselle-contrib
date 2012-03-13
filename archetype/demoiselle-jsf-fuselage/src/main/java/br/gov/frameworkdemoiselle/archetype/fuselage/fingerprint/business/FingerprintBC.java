package br.gov.frameworkdemoiselle.archetype.fuselage.fingerprint.business;

import java.util.List;


import br.gov.frameworkdemoiselle.annotation.Startup;
import br.gov.frameworkdemoiselle.archetype.fuselage.fingerprint.domain.Fingerprint;
import br.gov.frameworkdemoiselle.archetype.fuselage.fingerprint.persistence.FingerprintDAO;
import br.gov.frameworkdemoiselle.enumeration.contrib.Comparison;
import br.gov.frameworkdemoiselle.enumeration.contrib.Logic;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.stereotype.BusinessController;
import br.gov.frameworkdemoiselle.template.contrib.DelegateCrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

@BusinessController
public class FingerprintBC extends DelegateCrud<Fingerprint, Long, FingerprintDAO> {

	private static final long serialVersionUID = 1L;

	@Startup
	@Transactional
	public void load() {
		if (findAll().isEmpty()) {
			insert(new Fingerprint("Internet", "Github", "16:27:ac:a5:76:28:2d:36:63:1b:56:4d:eb:df:a6:48"));
			insert(new Fingerprint("Internet", "Sourceforge FRS", "b0:a8:eb:30:ce:1a:0e:6a:4d:7a:6b:3a:0a:c6:27:60"));
			insert(new Fingerprint("DMZ", "MTA Server", "d2:03:3c:dc:c3:fb:4e:f0:c3:42:0d:8c:e7:5d:2b:72"));
			insert(new Fingerprint("DMZ", "LDAP Server", "b1:53:fd:21:74:b6:2c:b2:10:07:b6:ec:e0:06:98:f8"));
			insert(new Fingerprint("Interno", "JBoss Server", "0f:b7:3a:dd:4e:a5:a1:55:7c:74:0c:ad:93:f8:3e:74"));
			insert(new Fingerprint("Interno", "Glassfish Server", "31:30:33:ff:af:39:10:64:6f:b2:4a:de:30:3f:ae:d1"));
			insert(new Fingerprint("Desenvolvimento", "JBoss Server", "f4:b8:58:86:d9:7c:8a:c6:18:16:90:ba:da:97:4f:ec"));
			insert(new Fingerprint("Desenvolvimento", "Glassfish Server", "18:58:f8:86:91:d0:48:d8:47:b1:ca:39:2b:41:24:10"));
		}
	}

	public List<Fingerprint> find(String search) {
		if (Strings.isNotBlank(search) && !"Todos".equals(getSelectedMenu()))
			return getDelegate().findByCategory(getSelectedMenu(), search);

		QueryConfig<Fingerprint> queryConfig = getQueryConfig();
		if (Strings.isBlank(search)) {
			if (!"Todos".equals(getSelectedMenu()))
				queryConfig.getFilter().put("category", getSelectedMenu());
			return findAll();
		}

		queryConfig.getFilter().put("category", search);
		queryConfig.getFilter().put("serverName", search);
		queryConfig.getFilter().put("fingerprint", search);
		queryConfig.setFilterComparison(Comparison.CONTAINS);
		queryConfig.setFilterLogic(Logic.OR);
		queryConfig.setFilterCaseInsensitive(true);
		return findAll();
	}

}
