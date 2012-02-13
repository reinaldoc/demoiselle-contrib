package org.example.project1.bookmark.view.list;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import org.example.project1.bookmark.business.BookmarkBC;
import org.example.project1.bookmark.domain.Bookmark;

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
			categories.add("Desenvolvimento");
			categories.add("Produtividade");
			categories.add("Software Livre");
		}
		return categories;
	}

}
