package br.gov.frameworkdemoiselle.ldap.internal;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.annotation.Ignore;
import br.gov.frameworkdemoiselle.internal.producer.LoggerProducer;
import br.gov.frameworkdemoiselle.ldap.annotation.DistinguishedName;
import br.gov.frameworkdemoiselle.ldap.annotation.LDAPEntry;
import br.gov.frameworkdemoiselle.ldap.exception.EntryException;

public class EntryCore implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Logger logger = LoggerProducer.create(EntryCore.class);

	@Inject
	private EntryCoreMap coreMap;

	public void persist(Object entry) {
		coreMap.persist(getStringsMap(entry), getDistinguishedName(entry));
	}

	public void merge(Object entry) {
		coreMap.merge(getStringsMap(entry), getDistinguishedName(entry));
	}

	public void update(Object entry) {
		coreMap.update(getStringsMap(entry), getDistinguishedName(entry));
	}

	public void remove(Object entry) {
		coreMap.remove(getDistinguishedName(entry));
	}

	public <T> T find(Class<T> entryClass, Object searchFilter) {
		T entry = null;
		try {
			entry = entryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return entry;
	}

	public <T> T getReference(Class<T> entryClass, Object dn) {
		T entry = null;
		try {
			entry = entryClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return entry;
	}

	public String findReference(Object searchFilter) {
		return coreMap.findReference((String) searchFilter);
	}

	public boolean isVerbose() {
		return coreMap.isVerbose();
	}

	public void setVerbose(boolean verbose) {
		coreMap.setVerbose(verbose);
	}

	/**
	 * Build a super classes List<Class<? extends Object>>
	 * 
	 * @param entry
	 * @param onlySuperClasses
	 * @return List of Super Classes
	 */
	private static List<Class<? extends Object>> getSuperClasses(Object entry, boolean onlySuperClasses) {
		List<Class<? extends Object>> list = new ArrayList<Class<? extends Object>>();
		if (entry != null) {
			if (!onlySuperClasses)
				list.add(entry.getClass());
			Class<? extends Object> superClazz = entry.getClass().getSuperclass();
			while (superClazz != null) {
				list.add(superClazz);
				superClazz = superClazz.getSuperclass();
			}
		}
		return list;
	}

	/**
	 * Build a array of super classes fields
	 * 
	 * @param entry
	 * @param onlySuperClasses
	 * @return Array of Super Classes Fields
	 */
	private static Field[] getSuperClassesFields(Object entry, boolean onlySuperClasses) {
		Field[] fieldArray = null;
		if (entry != null) {
			if (!onlySuperClasses)
				fieldArray = (Field[]) ArrayUtils.addAll(fieldArray, entry.getClass().getDeclaredFields());
			Class<? extends Object> superClazz = entry.getClass().getSuperclass();
			while (superClazz != null && !"java.lang.Object".equals(superClazz.getName())) {
				fieldArray = (Field[]) ArrayUtils.addAll(fieldArray, superClazz.getDeclaredFields());
				superClazz = superClazz.getSuperclass();
			}
		}
		return fieldArray;
	}

	/**
	 * Verify if annotation is present on entry or throw EntryException
	 * 
	 * @param entry
	 * @param clazz
	 */
	private static void verifyAnnotation(Object entry, Class<? extends Annotation> clazz) {
		boolean annotationPresent = false;
		String entryClazzName = null;
		if (entry != null) {
			entryClazzName = entry.getClass().getName();
			for (Class<? extends Object> claz : getSuperClasses(entry, false))
				if (claz.isAnnotationPresent(clazz))
					annotationPresent = true;
		}
		if (!annotationPresent) {
			logger.error("Bean " + entryClazzName + " hasn't annotation " + clazz.getName());
			throw new EntryException();
		}
	}

	/**
	 * Get @DistinguishedName value of a @LDAPEntry annotated object
	 * 
	 * @param entry
	 * @return Distinguished Name
	 */
	private static String getDistinguishedName(Object entry) {
		verifyAnnotation(entry, LDAPEntry.class);
		Field[] fields = getSuperClassesFields(entry, false);
		for (Field field : fields) {
			if (!field.isAnnotationPresent(DistinguishedName.class))
				continue;
			Object value = null;
			field.setAccessible(true);
			try {
				value = field.get(entry);
			} catch (Exception e) {
				continue;
			}
			if (value == null || value.toString().trim().isEmpty()) {
				logger.error("@DistinguishedName " + field.getName() + "can't be empty");
				throw new EntryException();
			} else {
				return value.toString();
			}
		}
		logger.error("@DistinguishedName must be present");
		throw new EntryException();
	}

	/**
	 * Convert @LDAPEntry annotated object to Map<String, String[]>
	 * 
	 * @param entry
	 * @return Entry Map
	 */
	private static Map<String, String[]> getStringsMap(Object entry) {
		Map<String, String[]> map = new HashMap<String, String[]>();
		verifyAnnotation(entry, LDAPEntry.class);

		Field[] fields = getSuperClassesFields(entry, false);
		for (Field field : fields) {
			if (field.isAnnotationPresent(DistinguishedName.class) || field.isAnnotationPresent(Ignore.class))
				continue;
			Object value = null;
			field.setAccessible(true);
			try {
				value = field.get(entry);
			} catch (Exception e) {
				continue;
			}
			if (value == null)
				continue;
			if (value instanceof String[]) {
				if (!map.containsKey(field.getName()))
					map.put(field.getName(), (String[]) value);
			} else
				map.put(field.getName(), new String[] { value.toString() });
		}
		return map;
	}

}