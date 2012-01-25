package br.gov.frameworkdemoiselle.fuselage.authenticators;

import javax.inject.Inject;

import org.slf4j.Logger;

import br.gov.frameworkdemoiselle.internal.producer.LoggerProducer;
import br.gov.frameworkdemoiselle.util.Reflections;
import br.gov.frameworkdemoiselle.util.ResourceBundle;

public abstract class AbstractAuthenticatorModule<T> implements AuthenticatorModule {

	private Logger logger;

	@Inject
	private ResourceBundle bundle;

	private Class<T> clazz;

	protected Class<T> getClazz() {
		if (clazz == null)
			clazz = Reflections.getGenericTypeArgument(this.getClass(), 0);
		return clazz;
	}

	@Override
	public String getModuleName() {
		return getClazz().getSimpleName();
	}

	@Override
	public boolean authenticate(String username, String password) {
		return false;
	}

	@Override
	public abstract AuthenticatorResults getResults();

	protected Logger getLogger() {
		if (logger == null)
			logger = LoggerProducer.create(getClazz());
		return logger;
	}

	protected ResourceBundle getBundle() {
		return bundle;
	}

}
