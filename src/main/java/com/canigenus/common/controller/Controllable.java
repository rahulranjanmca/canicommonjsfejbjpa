package com.canigenus.common.controller;

import java.io.Serializable;

import com.canigenus.common.model.Identifiable;
import com.canigenus.common.service.GenericService;

public interface Controllable<T extends Identifiable<?>, U extends T> extends Serializable {
	public abstract GenericService<T, U> getService();

	public abstract T instantiateEntity();
	
	public abstract U instantiateCriteria();
	
	public  Class<T> getEntityClazz();
	
	public  Class<U> getCriteriaClazz();
}
