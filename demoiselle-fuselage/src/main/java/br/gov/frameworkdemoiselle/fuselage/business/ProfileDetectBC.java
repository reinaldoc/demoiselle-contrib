package br.gov.frameworkdemoiselle.fuselage.business;

import java.util.List;

import br.gov.frameworkdemoiselle.annotation.Startup;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfileDetect;
import br.gov.frameworkdemoiselle.fuselage.persistence.ProfileDetectDAO;
import br.gov.frameworkdemoiselle.template.DelegateCrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;

public class ProfileDetectBC extends DelegateCrud<SecurityProfileDetect, Long, ProfileDetectDAO> {
	private static final long serialVersionUID = 1L;

	@Transactional
	@Startup
	public void startup() {
		if (findAll().isEmpty()) {
			//insert(new SecurityProfileDefault());
		}
	}

	public List<SecurityProfileDetect> findByImplementation(String implementation) {
		SecurityProfileDetect conditionLoad = new SecurityProfileDetect();
		conditionLoad.setImplementation(implementation);
		return getDelegate().findByExample(conditionLoad);
	}

/*	public SecurityProfileDetect loadByCondition(String condition) {
		List<SecurityProfileDetect> conditionList = findByCondition(condition);
		if (conditionList.size() != 1) {
			return new SecurityProfileDetect();
		} else {
			return conditionList.get(0);
		}
	}
*/

}
