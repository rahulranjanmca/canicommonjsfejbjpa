package com.canigenus.common.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.canigenus.common.model.Convertible;
import com.canigenus.common.service.GenericServiceImpl;

public abstract class AbstractIdConverter implements Converter{

	public abstract GenericServiceImpl<?,?> getService();

	public String getName() {
		return "name";
	}
    
	public abstract Class<? extends Convertible<?>> getClassType();

	public Object getAsObject(FacesContext context, UIComponent component,
			String value) {
		return getService().get(getClassType(), getName(), value).getId();
	}

	public String getAsString(FacesContext context, UIComponent component,
			Object value) {
		return ((Convertible<?>) value).getName();
	}
}
