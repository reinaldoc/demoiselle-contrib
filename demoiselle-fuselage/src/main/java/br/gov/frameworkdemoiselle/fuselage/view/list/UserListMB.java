package br.gov.frameworkdemoiselle.fuselage.view.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.enumeration.contrib.Comparison;
import br.gov.frameworkdemoiselle.enumeration.contrib.Logic;
import br.gov.frameworkdemoiselle.fuselage.business.UserBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.message.SeverityType;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.report.Report;
import br.gov.frameworkdemoiselle.report.Type;
import br.gov.frameworkdemoiselle.report.annotation.Path;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.contrib.AbstractListPageBean;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.FileRenderer;
import br.gov.frameworkdemoiselle.util.contrib.Faces;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

@ViewController
public class UserListMB extends AbstractListPageBean<SecurityUser, Long> {
	private static final long serialVersionUID = 1L;

	@Inject
	private UserBC bc;

	@Inject
	@Path("reports/SecurityUsers.jasper")
	private Report report;

	@Inject
	private FileRenderer renderer;

	@Override
	protected List<SecurityUser> handleResultList(QueryConfig<SecurityUser> queryConfig) {
		try {
			if (Strings.isNotBlank(getResultFilter())) {
				queryConfig.getFilter().put("login", getResultFilter());
				queryConfig.getFilter().put("name", getResultFilter());
				queryConfig.getFilter().put("orgunit", getResultFilter());
				queryConfig.getFilter().put("description", getResultFilter());
				queryConfig.setFilterComparison(Comparison.CONTAINS);
				queryConfig.setFilterLogic(Logic.OR);
				queryConfig.setFilterCaseInsensitive(true);
			}
			queryConfig.setSorting("name");
			return bc.findAll();
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.generic.business.error", SeverityType.ERROR);
		}
		return new ArrayList<SecurityUser>();
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

	public String print() {
		try {
			Map<String, Object> param = new HashMap<String, Object>();
			byte[] buffer = report.export(getResultList(), param, Type.PDF);
			this.renderer.render(buffer, FileRenderer.ContentType.PDF, "relatorio.pdf");
		} catch (RuntimeException e) {
			Faces.validationFailed();
			Faces.addI18nMessage("fuselage.generic.report.error", SeverityType.ERROR);
		}
		return null;
	}

}
