package br.gov.frameworkdemoiselle.archetype.fuselage.fingerprint.view.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;


import br.gov.frameworkdemoiselle.archetype.fuselage.fingerprint.business.FingerprintBC;
import br.gov.frameworkdemoiselle.archetype.fuselage.fingerprint.domain.Fingerprint;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.contrib.AbstractListPageBean;

@ViewController
public class FingerprintListMB extends AbstractListPageBean<Fingerprint, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private FingerprintBC bc;

	private List<String> categories;

	@Override
	protected List<Fingerprint> handleResultList(QueryConfig<Fingerprint> queryConfig) {
		return bc.find(getResultFilter());
	}

	public void deleteSelection() {
		boolean delete;
		for (Iterator<Long> iter = getSelection().keySet().iterator(); iter.hasNext();) {
			Long id = iter.next();
			delete = getSelection().get(id);

			if (delete) {
				bc.delete(id);
				iter.remove();
			}
		}

	}

	public List<String> getCategories() {
		if (categories == null) {
			categories = new ArrayList<String>();
			categories.add("Internet");
			categories.add("DMZ");
			categories.add("Interno");
			categories.add("Desenvolvimento");
		}
		return categories;
	}

}
