package br.gov.frameworkdemoiselle.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class AbstractMenuContext implements Serializable {

	private static final long serialVersionUID = 1L;

	public final static String KEY_CONFIG = "CONFIG_";

	public final static String KEY_PERMIT_UNSELECT = "UNSELECT_";

	public final static String PERMIT_UNSELECT_TRUE = "TRUE_";

	public final static String PERMIT_UNSELECT_FALSE = "FALSE_";

	public final static String KEY_SELECTION_MODE = "MODE_";

	public final static String SELECTION_MODE_SINGLE = "SINGLE_";

	public final static String SELECTION_MODE_MULTI = "MULTI_";

	public final static String KEY_STYLECLASS = "Style";

	public final static String KEY_COUNTER = "Counter";

	private Map<String, Map<String, Map<String, String>>> menu = new HashMap<String, Map<String, Map<String, String>>>();

	protected abstract String getSelectedStyleClass();

	protected abstract boolean isSingleSelection();

	protected abstract boolean isPermitedUnselect();

	/**
	 * Retrive menu from XHTML;
	 * 
	 * @return
	 */
	public Map<String, Map<String, Map<String, String>>> getMenu() {
		return menu;
	}

	private void setValue(String menuName, String itemName, String keyName, String value) {
		Map<String, Map<String, String>> items = new HashMap<String, Map<String, String>>();
		if (menu.containsKey(menuName)) {
			items = menu.get(menuName);
		}

		Map<String, String> values = new HashMap<String, String>();
		if (items.containsKey(itemName)) {
			values = items.get(itemName);
		}

		if (KEY_STYLECLASS.equals(keyName)) {
			if (isPermitUnselect(menuName) && value.equals(values.get(keyName))) {
				values.put(keyName, null);
			} else {
				values.put(keyName, value);
			}
		} else {
			values.put(keyName, value);
		}

		items.put(itemName, values);
		menu.put(menuName, items);
	}

	private String getValue(String menuName, String itemName, String keyName) {
		try {
			return menu.get(menuName).get(KEY_CONFIG).get(KEY_SELECTION_MODE);
		} catch (Exception e) {
			return null;
		}
	}

	private void unselectAll(String menuName) {
		if (SELECTION_MODE_SINGLE.equals(getSelectionMode(menuName))) {
			Map<String, Map<String, String>> items = new HashMap<String, Map<String, String>>();
			if (menu.containsKey(menuName)) {
				items = menu.get(menuName);
				Iterator<String> iterItems = items.keySet().iterator();
				while (iterItems.hasNext()) {
					setValue(menuName, iterItems.next(), KEY_STYLECLASS, null);
				}
			}
		}
	}

	public void select(String menuName, String itemName) {
		unselectAll(menuName);
		setValue(menuName, itemName, KEY_STYLECLASS, getSelectedStyleClass());
	}

	public void select(String menuName, String itemName, String styleClass) {
		unselectAll(menuName);
		setValue(menuName, itemName, KEY_STYLECLASS, styleClass);
	}

	public boolean isSelected(String menuName, String itemName) {
		try {
			if (menu.get(menuName).get(itemName).get(KEY_STYLECLASS) != null) {
				return true;
			}
		} catch (Exception e) {
			// Ignore
		}
		return false;
	}

	public boolean isSelected(String menuName, String itemName, String styleClass) {
		try {
			if (styleClass.equals(menu.get(menuName).get(itemName).get(KEY_STYLECLASS))) {
				return true;
			}
		} catch (Exception e) {
			// Ignore
		}
		return false;
	}

	public String getSelected(String menuName) {
		try {
			for (String itemName : menu.get(menuName).keySet()) {
				if (isSelected(menuName, itemName))
					return itemName;
			}
		} catch (Exception e) {
			// Ignore
		}
		return "";
	}

	public String getSelected(String menuName, String styleClass) {
		try {
			for (String itemName : menu.get(menuName).keySet()) {
				if (isSelected(menuName, itemName, styleClass))
					return itemName;
			}
		} catch (Exception e) {
			// Ignore
		}
		return "";
	}

	public List<String> getSelecteds(String menuName) {
		List<String> selectedList = new ArrayList<String>();
		try {
			for (String itemName : menu.get(menuName).keySet()) {
				if (isSelected(menuName, itemName))
					selectedList.add(itemName);
			}
		} catch (Exception e) {
			// Ignore
		}
		return selectedList;
	}

	public List<String> getSelecteds(String menuName, String styleClass) {
		List<String> selectedList = new ArrayList<String>();
		try {
			for (String itemName : menu.get(menuName).keySet()) {
				if (isSelected(menuName, itemName, styleClass))
					selectedList.add(itemName);
			}
		} catch (Exception e) {
			// Ignore
		}
		return selectedList;
	}

	public int getCounter(String menuName, String itemName) {
		String count = getValue(menuName, KEY_CONFIG, KEY_SELECTION_MODE);
		if (count == null)
			return 0;
		else
			return new Long(count).intValue();
	}

	public void setCounter(String menuName, String itemName, int count) {
		setValue(menuName, itemName, KEY_COUNTER, String.valueOf(count));
	}

	public void incrementCounter(String menuName, String itemName, int value) {
		setValue(menuName, itemName, KEY_COUNTER, String.valueOf(getCounter(menuName, itemName) + value));
	}

	public void decrementCounter(String menuName, String itemName, int value) {
		setValue(menuName, itemName, KEY_COUNTER, String.valueOf(getCounter(menuName, itemName) - value));
	}

	public String getSelectionMode(String menuName) {
		String selectionMode = getValue(menuName, KEY_CONFIG, KEY_SELECTION_MODE);
		if (selectionMode == null) {
			if (isSingleSelection())
				selectionMode = SELECTION_MODE_SINGLE;
			else
				selectionMode = SELECTION_MODE_MULTI;
		}
		return selectionMode;
	}

	public void setSelectionSingleMode(String menuName) {
		setValue(menuName, KEY_CONFIG, KEY_SELECTION_MODE, SELECTION_MODE_SINGLE);
	}

	public void setSelectionMultiMode(String menuName) {
		setValue(menuName, KEY_CONFIG, KEY_SELECTION_MODE, SELECTION_MODE_MULTI);
	}

	public boolean isPermitUnselect(String menuName) {
		boolean permitUnselect = isPermitedUnselect();
		String permitUnselectStr = getValue(menuName, KEY_CONFIG, KEY_PERMIT_UNSELECT);
		if (PERMIT_UNSELECT_TRUE.equals(permitUnselectStr))
			permitUnselect = true;
		else if (PERMIT_UNSELECT_FALSE.equals(permitUnselectStr))
			permitUnselect = false;
		return permitUnselect;
	}

	public void permitUnselect(String menuName, boolean permitUnselect) {
		if (permitUnselect)
			setValue(menuName, KEY_CONFIG, KEY_PERMIT_UNSELECT, PERMIT_UNSELECT_TRUE);
		else
			setValue(menuName, KEY_CONFIG, KEY_PERMIT_UNSELECT, PERMIT_UNSELECT_FALSE);
	}

}
