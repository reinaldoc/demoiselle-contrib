package br.gov.frameworkdemoiselle.fuselage.view.list;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.enumeration.contrib.Comparison;
import br.gov.frameworkdemoiselle.enumeration.contrib.Logic;
import br.gov.frameworkdemoiselle.fuselage.business.ProfileBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityProfile;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.contrib.AbstractListPageBean;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.contrib.Faces;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

@ViewController
public class ProfileListMB extends AbstractListPageBean<SecurityProfile, Long> {
	private static final long serialVersionUID = 1L;

	@Inject
	private ProfileBC bc;

	@Override
	protected List<SecurityProfile> handleResultList(QueryConfig<SecurityProfile> queryConfig) {
		try {
			if (Strings.isNotBlank(getResultFilter())) {
				queryConfig.getFilter().put("name", getResultFilter());
				queryConfig.getFilter().put("description", getResultFilter());
				queryConfig.getFilter().put("shortDescription", getResultFilter());
				queryConfig.getFilter().put("welcomePage.value", getResultFilter());
				queryConfig.setFilterComparison(Comparison.CONTAINS);
				queryConfig.setFilterLogic(Logic.OR);
				queryConfig.setFilterCaseInsensitive(true);
			}
			if (!queryConfig.hasSorting())
				queryConfig.setSorting("name");
			return bc.findAll();
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.generic.business.error", SeverityType.ERROR);
		}
		return new ArrayList<SecurityProfile>();
	}

	@Transactional
	public String deleteSelection() {
		try {
			bc.delete(getSelectedList());
			clearSelection();
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.generic.business.error", SeverityType.ERROR);
		}
		return null;
	}

}
