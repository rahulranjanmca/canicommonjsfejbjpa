package com.canigenus.common.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

@ApplicationScoped
@Named
public class CommonApplicationScoped {
	
	public <T>  List<T> convertToList(T [] array)
	{
		if(array==null)
			return new ArrayList<>();
		return Arrays.asList(array);
	}
	
	public Object[] createArray(int size) {
        return new Object[size];
    }


}
