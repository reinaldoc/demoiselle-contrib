package br.gov.frameworkdemoiselle.archetype.fuselage.bookmark.view.list;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.gov.frameworkdemoiselle.archetype.fuselage.bookmark.business.BookmarkBC;
import br.gov.frameworkdemoiselle.archetype.fuselage.bookmark.domain.Bookmark;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.stereotype.ViewController;
import br.gov.frameworkdemoiselle.template.contrib.AbstractListPageBean;

@ViewController
public class BookmarkListMB extends AbstractListPageBean<Bookmark, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	private BookmarkBC bc;

	private List<String> categories;

	@Override
	protected List<Bookmark> handleResultList(QueryConfig<Bookmark> queryConfig) {
		return bc.find(getResultFilter(), getSelectedMenu());
	}

	public List<String> getCategories() {
		if (categories == null) {
			categories = new ArrayList<String>();
			categories.add("Desenvolvimento");
			categories.add("Produtividade");
			categories.add("Software Livre");
		}
		return categories;
	}

}
