package br.gov.frameworkdemoiselle.util.configuration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import br.gov.frameworkdemoiselle.annotation.Ignore;
import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.configuration.Configuration;

@Configuration(resource = "demoiselle", prefix = "util.menucontext")
public class MenuContextConfig implements Serializable {

	private static final long serialVersionUID = 1L;

	@Name("selectedStyleClass")
	private String selectedStyleClass;

	@Name("selectItems")
	private List<String> selectItems;

	@Ignore
	private List<String[]> selectItemsList;

	@Name("singleSelect")
	private boolean singleSelect = true;

	@Name("permitUnselect")
	private boolean permitUnselect = false;

	public String getSelectedStyleClass() {
		return selectedStyleClass;
	}

	/**
	 * The list of menu items that will be seleteds on startup
	 * For each selectItems property like
	 * 		menuName:menuItem:true:false
	 * 
	 * The return is a String[] with four elements:
	 *		[1] menuName
	 *		[2] menuItem
	 *		[3] singleSelect - default is true
	 *		[4] permitUnselect - default is false
	 * 
	 * @return util.menucontext.selectItems property;
	 */
	public List<String[]> getSelectItems() {
		if (selectItemsList == null && selectItems != null) {
			selectItemsList = new ArrayList<String[]>();
			for (String item : selectItems) {
				String[] itemArray = item.split(":");
				if (itemArray.length > 1) {
					String[] resultArray = new String[] { itemArray[0], itemArray[1], "true", "false" };
					if (itemArray.length > 2 && itemArray[2].toLowerCase().equals("false"))
						resultArray[2] = "false";
					if (itemArray.length > 3 && itemArray[3].toLowerCase().equals("true"))
						resultArray[3] = "true";
					selectItemsList.add(resultArray);
				}
			}
		}
		return selectItemsList;
	}

	/**
	 * The default behavior for multiselect support
	 * 
	 * @return util.menucontext.singleSelect property;
	 */
	public boolean isSingleSelect() {
		return singleSelect;
	}

	/**
	 * The default behavior for unselect support
	 * 
	 * @return util.menucontext.permitUnselect property;
	 */
	public boolean isPermitedUnselect() {
		return permitUnselect;
	}

}
