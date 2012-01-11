package br.gov.frameworkdemoiselle.fuselage.view.list;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.frameworkdemoiselle.fuselage.business.UserBC;
import br.gov.frameworkdemoiselle.fuselage.domain.SecurityUser;
import br.gov.frameworkdemoiselle.report.Report;
import br.gov.frameworkdemoiselle.report.Type;
import br.gov.frameworkdemoiselle.report.annotation.Path;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.AbstractListPageBean;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.FileRenderer;

@ViewController
public class UserListMB extends AbstractListPageBean<SecurityUser, Long> {
	private static final long serialVersionUID = 1L;

	@Inject
	private UserBC bc;

	@Inject
	@Path("reports/SecurityUser.jrxml")
	private Report report;
	
	@Inject
	private FileRenderer renderer;

	@PostConstruct
	public void init() {
		setLazyDataModelInitialSortAttribute("name");
	}

	@Override
	protected List<SecurityUser> handleResultList() {
		return bc.findAll();
	}

	@Transactional
	public String deleteSelection() {
		bc.delete(getSelectedList());
		clearSelection();
		return null;
	}

	public String print() {
		Map<String, Object> param = new HashMap<String, Object>();
		byte[] buffer = report.export(getResultList(), param, Type.PDF);
		this.renderer.render(buffer, FileRenderer.ContentType.PDF, "relatorio.pdf");
		return null;
	}

}
