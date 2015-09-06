package com.canigenus.common.model;

public interface Tenantable<T> {
	public void setTenantId(T t);
	public T getTenantId();
	
}
