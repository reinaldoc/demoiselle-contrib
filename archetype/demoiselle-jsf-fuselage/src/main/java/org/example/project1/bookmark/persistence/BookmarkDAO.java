package org.example.project1.bookmark.persistence;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;

import org.example.project1.bookmark.domain.Bookmark;
import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.stereotype.PersistenceController;
import br.gov.frameworkdemoiselle.template.contrib.JPACrud;

@PersistenceController
public class BookmarkDAO extends JPACrud<Bookmark, Long> {

	private static final long serialVersionUID = 1L;

	@Inject
	@SuppressWarnings("unused")
	private Logger logger;

	@SuppressWarnings("unchecked")
	public List<Bookmark> findByCategory(String category, String search) {

		String q = "from Bookmark as b where lower(b.category) = lower(:category) and"
				+ "(lower(b.description) like lower(:search) or lower(b.link) like lower(:search)) ";
		Query query = createQuery(q);
		query.setParameter("category", category);
		query.setParameter("search", "%" + search + "%");

		return query.getResultList();
	}

}
