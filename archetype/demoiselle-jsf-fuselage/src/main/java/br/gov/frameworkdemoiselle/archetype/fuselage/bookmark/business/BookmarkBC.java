package br.gov.frameworkdemoiselle.archetype.fuselage.bookmark.business;

import java.util.List;


import br.gov.frameworkdemoiselle.annotation.Startup;
import br.gov.frameworkdemoiselle.archetype.fuselage.bookmark.domain.Bookmark;
import br.gov.frameworkdemoiselle.archetype.fuselage.bookmark.persistence.BookmarkDAO;
import br.gov.frameworkdemoiselle.enumeration.contrib.Comparison;
import br.gov.frameworkdemoiselle.enumeration.contrib.Logic;
import br.gov.frameworkdemoiselle.query.contrib.QueryConfig;
import br.gov.frameworkdemoiselle.stereotype.BusinessController;
import br.gov.frameworkdemoiselle.template.contrib.DelegateCrud;
import br.gov.frameworkdemoiselle.transaction.Transactional;
import br.gov.frameworkdemoiselle.util.contrib.Strings;

@BusinessController
public class BookmarkBC extends DelegateCrud<Bookmark, Long, BookmarkDAO> {

	private static final long serialVersionUID = 1L;

	@Startup
	@Transactional
	public void load() {
		if (findAll().isEmpty()) {
			insert(new Bookmark("Desenvolvimento", "Demoiselle Portal", "http://www.frameworkdemoiselle.gov.br"));
			insert(new Bookmark(
					"Desenvolvimento",
					"PrimeFaces QuickStart",
					"http://java.dzone.com/articles/primefaces-quickstart-tutorial?utm_source=feedburner&utm_medium=feed&utm_campaign=Feed%3A+javalobby%2Ffrontpage+(Javalobby+%2F+Java+Zone)"));
			insert(new Bookmark("Desenvolvimento", "Custom converter in JSF 2.0", "http://www.mkyong.com/jsf2/custom-converter-in-jsf-2-0/"));
			insert(new Bookmark("Software Livre", "XEN convert HVM to PVM", "http://www.asplund.nu/xencluster/xen-convert-hvm-to-pvm.html"));
			insert(new Bookmark("Software Livre", "Tutorial Marca D'Água - GIMP", "http://www.flickr.com/photos/xandelisk/2951933349/"));
			insert(new Bookmark("Software Livre", "How To Shrink Your Virtualbox VM",
					"http://maketecheasier.com/shrink-your-virtualbox-vm/2009/04/06"));
			insert(new Bookmark("Software Livre", "Replicated Failover using LDAP",
					"http://wiki.samba.org/index.php/Replicated_Failover_Domain_Controller_and_file_server_using_LDAP"));
			insert(new Bookmark("Produtividade", "Gizmodo Brasil", "http://www.gizmodo.com.br/"));
			insert(new Bookmark("Produtividade", "Danosse.COM", "http://www.danosse.com/"));
			insert(new Bookmark("Produtividade", "Bule Voador", "http://bulevoador.haaan.com/"));
		}
	}

	public List<Bookmark> find(String search) {
		if (Strings.isNotBlank(search) && !"Todos".equals(getSelectedMenu()))
			return getDelegate().findByCategory(getSelectedMenu(), search);

		QueryConfig<Bookmark> queryConfig = getQueryConfig();
		if (Strings.isBlank(search)) {
			if (!"Todos".equals(getSelectedMenu()))
				queryConfig.getFilter().put("category", getSelectedMenu());
			return findAll();
		}

		queryConfig.getFilter().put("category", search);
		queryConfig.getFilter().put("description", search);
		queryConfig.getFilter().put("link", search);
		queryConfig.setFilterComparison(Comparison.CONTAINS);
		queryConfig.setFilterLogic(Logic.OR);
		queryConfig.setFilterCaseInsensitive(true);
		return findAll();
	}

}
