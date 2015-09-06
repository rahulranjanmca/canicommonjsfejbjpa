package com.canigenus.common.model;

public interface Identifiable<T> {
	T getId();
	void setId(T id);
}
