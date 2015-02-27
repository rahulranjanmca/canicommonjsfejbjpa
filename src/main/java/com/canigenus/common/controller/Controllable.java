package com.canigenus.common.controller;

import com.canigenus.common.model.Identifiable;
import com.canigenus.common.service.GenericServiceImpl;

public interface Controllable<T extends Identifiable<?>> {
	public abstract GenericServiceImpl<T> getService();

	public abstract Class<T> getClassType();

	public abstract T instantiateEntity();
}
