/**
 * Copyright (c) 2012 - Reinaldo de Carvalho <reinaldoc@gmail.com>
 * 
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *  
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details. 
 * 
 */

package br.gov.frameworkdemoiselle.ldap.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.annotation.Ignore;
import br.gov.frameworkdemoiselle.annotation.Name;
import br.gov.frameworkdemoiselle.internal.producer.LoggerProducer;
import br.gov.frameworkdemoiselle.ldap.annotation.DistinguishedName;
import br.gov.frameworkdemoiselle.ldap.annotation.Id;
import br.gov.frameworkdemoiselle.ldap.annotation.LDAPEntry;
import br.gov.frameworkdemoiselle.ldap.exception.EntryException;
import br.gov.frameworkdemoiselle.util.Beans;
import br.gov.frameworkdemoiselle.util.Reflections;

public class ClazzUtils {

	private static Logger logger = LoggerProducer.create(ClazzUtils.class);

	/**
	 * Get @DistinguishedName value of a @LDAPEntry annotated object
	 * 
	 * @param entry
	 * @return Distinguished Name
	 */
	public static String getDistinguishedName(Object entry) {
		requireAnnotation(entry.getClass(), LDAPEntry.class);
		return (String) getRequiredAnnotatedValue(entry, DistinguishedName.class);
	}

	/**
	 * Convert @LDAPEntry annotated object to Map<String, Object>. The valid
	 * types for Object value is String, String[] or byte[]
	 * 
	 * @param entry
	 * @return Entry Map
	 */
	public static Map<String, Object> getObjectMap(Object entry) {
		requireAnnotation(entry.getClass(), LDAPEntry.class);
		Map<String, Object> map = new HashMap<String, Object>();

		Field[] fields = getSuperClassesFields(entry.getClass());
		for (Field field : fields) {
			if (field.isAnnotationPresent(DistinguishedName.class) || field.isAnnotationPresent(Ignore.class))
				continue;
			if (map.containsKey(field.getName()))
				continue;
			Object value = Reflections.getFieldValue(field, entry);
			if (value == null)
				continue;
			map.put(field.getName(), getObjectAsSupportedType(value));
		}
		return map;
	}

	public static Object getObjectAsSupportedType(Object obj) {
		if (obj instanceof String || obj instanceof String[] || obj instanceof byte[])
			return obj;
		else if (isAnnotationPresent(obj.getClass(), LDAPEntry.class))
			return getRequiredAnnotatedValue(obj, Id.class).toString();
		else if (obj.getClass().isArray())
			return convertToStringArrayFromArray(obj);
		else if (isCollection(obj.getClass()))
			return convertToStringArrayFromCollection(obj);
		else
			return obj.toString();
	}

	public static boolean isCollection(Class<?> clazz) {
		for (Class<?> claz : clazz.getInterfaces())
			if (claz == Collection.class)
				return true;
		return false;
	}

	public static String[] convertToStringArrayFromArray(Object array) {
		return new String[] { "Future implementation" };
	}

	public static String[] convertToStringArrayFromCollection(Object array) {
		return new String[] { "Future implementation" };
	}

	/**
	 * Convert a Map<dn, Map<attr, value[]>> to @LDAPEntry annotated object
	 * 
	 * @param entry
	 * @return Entry Map
	 */
	public static <T> List<T> getEntryObjectList(Map<String, Map<String, Object>> entryMap, Class<T> clazz) {
		if (entryMap == null)
			return null;
		requireAnnotation(clazz, LDAPEntry.class);
		List<T> resultList = new ArrayList<T>();
		for (Map.Entry<String, Map<String, Object>> mapEntry : entryMap.entrySet())
			resultList.add(getEntryObject(mapEntry.getKey(), mapEntry.getValue(), clazz));
		return resultList;
	}

	public static <T> T getEntryObject(String dn, Map<String, Object> map, Class<T> clazz) {
		T entry = Beans.getReference(clazz);
		Field[] fields = getSuperClassesFields(entry.getClass());
		for (Field field : fields) {
			if (field.isAnnotationPresent(Ignore.class))
				continue;
			if (field.isAnnotationPresent(DistinguishedName.class))
				setFieldValue(field, entry, new String[] { dn });
			else {
				String fieldName = getFieldName(field);
				if (map.containsKey(fieldName))
					setFieldValue(field, entry, map.get(fieldName));
			}
		}
		return entry;
	}

	/**
	 * Feeling a Class for a String Representation of Search Filters (RFC 4515)
	 * and throw EntryException when can't find a @LDAPEntry object
	 * 
	 * @param searchFilter
	 * @return Class
	 */
	public static Class<?> getRequiredClassForSearchFilter(String searchFilter, List<String> packageNames) {
		Class<?> clazz = getClassForSearchFilter(searchFilter, packageNames);
		if (clazz == null)
			throw new EntryException("Can't find a @LDAPEntry object for search filter " + searchFilter);
		return clazz;
	}

	/**
	 * Feeling a Class or return null for a String Representation of Search
	 * Filters (RFC 4515)
	 * 
	 * @param searchFilter
	 * @return Class
	 */
	public static Class<?> getClassForSearchFilter(String searchFilter, List<String> packageNames) {
		if (searchFilter == null || packageNames == null || packageNames.size() == 0)
			return null;
		String patternStr = "objectClass=(.*?)(\\)|$)";
		Matcher matcher = Pattern.compile(patternStr).matcher(searchFilter);
		boolean matchFound = matcher.find();
		if (matchFound && matcher.groupCount() > 1)
			for (String packageName : packageNames)
				try {
					return Class.forName(packageName + "." + Character.toUpperCase(matcher.group(1).charAt(0)) + matcher.group(1).substring(1));
				} catch (Exception e) {
					return null;
				}
		return null;
	}

	/**
	 * Build a super classes List<Class<? extends Object>>
	 * 
	 * @param entry
	 * @param onlySuperClasses
	 * @return List of Super Classes
	 */
	public static List<Class<? extends Object>> getSuperClasses(Class<?> entryClass) {
		List<Class<? extends Object>> list = new ArrayList<Class<? extends Object>>();
		Class<? extends Object> superClazz = entryClass.getSuperclass();
		while (superClazz != null) {
			list.add(superClazz);
			superClazz = superClazz.getSuperclass();
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
	public static Field[] getSuperClassesFields(Class<?> entryClass) {
		Field[] fieldArray = null;
		fieldArray = (Field[]) ArrayUtils.addAll(fieldArray, entryClass.getDeclaredFields());
		Class<?> superClazz = entryClass.getSuperclass();
		while (superClazz != null && !"java.lang.Object".equals(superClazz.getName())) {
			fieldArray = (Field[]) ArrayUtils.addAll(fieldArray, superClazz.getDeclaredFields());
			superClazz = superClazz.getSuperclass();
		}
		return fieldArray;
	}

	/**
	 * Get annotated value and when null throw EntryException
	 * 
	 * @param entry
	 * @param clazz
	 * @return
	 */
	public static Object getRequiredAnnotatedValue(Object entry, Class<? extends Annotation> clazz) {
		Object value = getAnnotatedValue(entry, clazz);
		if (value != null && !value.toString().trim().isEmpty()) {
			return value;
		}
		throw new EntryException("Class " + entry.getClass().getSimpleName() + " doesn't have a valid value for @" + clazz.getSimpleName());
	}

	/**
	 * Get annotated value
	 * 
	 * @param entry
	 */
	private static Object getAnnotatedValue(Object entry, Class<? extends Annotation> clazz) {
		Field field = getFieldAnnotatedAs(entry.getClass(), clazz);
		if (field != null)
			return Reflections.getFieldValue(field, entry);
		return null;
	}

	public static Field getRequiredFieldAnnotatedAs(Class<?> claz, Class<? extends Annotation> clazz) {
		Field field = getFieldAnnotatedAs(claz, clazz);
		if (field == null)
			throw new EntryException("Field with @" + clazz.getSimpleName() + " not found on class " + claz.getSimpleName());
		return field;
	}

	public static Field getFieldAnnotatedAs(Class<?> claz, Class<? extends Annotation> clazz) {
		Field[] fields = getSuperClassesFields(claz);
		for (Field field : fields)
			if (field.isAnnotationPresent(clazz))
				return field;
		return null;
	}

	/**
	 * Value may be 'String', 'String[]' or Object Annotated with @LDAPEntry
	 * 
	 * @param field
	 * @param entry
	 * @param value
	 */
	public static void setFieldValue(Field field, Object entry, Object value) {
		Reflections.setFieldValue(field, entry, getValueAsFieldType(field, value));
	}

	public static Object getValueAsFieldType(Field field, Object values) {
		if (values instanceof String[]) {
			String[] valueArray = (String[]) values;

			if (valueArray == null || valueArray.length == 0)
				return null;

			if (field.getType().isAssignableFrom(String.class))
				return valueArray[0];

			if (field.getType().isArray())
				if (field.getType().getComponentType().isAssignableFrom(String.class))
					return valueArray;

			if (field.getType().isPrimitive()) {

				if (field.getType().isAssignableFrom(int.class))
					return Integer.valueOf(valueArray[0]);

				if (field.getType().isAssignableFrom(long.class))
					return Long.valueOf(valueArray[0]);

				if (field.getType().isAssignableFrom(double.class))
					return Double.valueOf(valueArray[0]);

				if (field.getType().isAssignableFrom(float.class))
					return Float.valueOf(valueArray[0]);

				if (field.getType().isAssignableFrom(short.class))
					return Short.valueOf(valueArray[0]);

				if (field.getType().isAssignableFrom(byte.class))
					return Byte.valueOf(valueArray[0]);

			}

			if (isAnnotationPresent(field.getType(), LDAPEntry.class))
				return getMappedEntryObject(field.getType(), valueArray);

			if (field.getType().isAssignableFrom(ArrayList.class))
				if (String.class.isAssignableFrom(Reflections.getGenericTypeArgument(field.getType(), 0)))
					return new ArrayList<String>(Arrays.asList(valueArray));

			if (field.getType().isAssignableFrom(Integer.class))
				return Integer.valueOf(valueArray[0]);

			if (field.getType().isAssignableFrom(Long.class))
				return Long.valueOf(valueArray[0]);

			if (field.getType().isAssignableFrom(Double.class))
				return Double.valueOf(valueArray[0]);

			if (field.getType().isAssignableFrom(Float.class))
				return Float.valueOf(valueArray[0]);

			if (field.getType().isAssignableFrom(Short.class))
				return Short.valueOf(valueArray[0]);

			if (field.getType().isAssignableFrom(Byte.class))
				return Byte.valueOf(valueArray[0]);

			logger.error("Handling not implemented for field " + field.getName() + " with type " + field.getType().getSimpleName());

		} else if (values instanceof byte[][]) {

			if (field.getType().isAssignableFrom(byte[][].class))
				return values;

			if (field.getType().isAssignableFrom(byte[].class))
				return ((byte[][]) values)[0];

			logger.error("Binary data from LDAP can't be set in " + field.getName() + " with type " + field.getType().getSimpleName());

		}

		logger.error("Object value should be String[] or byte[][]. The value type " + values.getClass() + " can't be set in " + field.getName()
				+ " with type " + field.getType().getSimpleName());
		return null;
	}

	public static Object getMappedEntryObject(Class<?> clazz, String[] value) {
		Object entry = Beans.getReference(clazz);
		setAnnotatedFieldValueAs(entry, Id.class, value);
		return entry;
	}

	/**
	 * Set annotated value
	 * 
	 * @param entry
	 */
	public static void setAnnotatedFieldValueAs(Object entry, Class<? extends Annotation> clazz, String[] value) {
		Field field = getFieldAnnotatedAs(entry.getClass(), clazz);
		setFieldValue(field, entry, value);
	}

	/**
	 * If @Name present returns field.getAnnotation(Name.class).value(),
	 * otherwise field.getName();
	 * 
	 * @param field
	 * @return @Name annotation value or object attribute name;
	 */
	public static String getFieldName(Field field) {
		if (field.isAnnotationPresent(Name.class)) {
			String name = field.getAnnotation(Name.class).value();
			if (name == null || name.trim().isEmpty())
				throw new EntryException("Annotation @Name must have a value");
			return name;
		} else
			return field.getName();
	}

	/**
	 * Verify if annotation is present
	 * 
	 * @param entry
	 * @param clazz
	 */
	public static boolean isAnnotationPresent(Class<?> entryClass, Class<? extends Annotation> clazz) {
		for (Class<?> claz : getSuperClasses(entryClass))
			if (claz.isAnnotationPresent(clazz))
				return true;
		return false;
	}

	/**
	 * Verify if annotation is present on entry and when false throw
	 * EntryException
	 * 
	 * @param entry
	 * @param clazz
	 */
	public static void requireAnnotation(Class<?> entryClass, Class<? extends Annotation> clazz) {
		if (!isAnnotationPresent(entryClass, clazz))
			throw new EntryException("Entry " + entryClass.getSimpleName() + " doesn't have @" + clazz.getName());
	}

}
