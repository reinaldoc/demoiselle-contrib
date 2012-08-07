package br.gov.frameworkdemoiselle.util.contrib;

import java.lang.reflect.Array;
import java.util.Collection;

import javax.persistence.Id;

import br.gov.frameworkdemoiselle.util.contrib.Reflections;

public class JPAUtil {

	@SuppressWarnings("unchecked")
	public static <T> T[] getIds(Collection<?> collection, Class<T> T) {
		T[] result = Reflections.getAnnotatedValues(collection, Id.class, T);
		if (result == null)
			return (T[]) Array.newInstance(T, 0);
		return result;
	}

}
