package com.canigenus.common.model;

import java.util.ResourceBundle;

public interface Message {
	
	public String getCode();
	
	public String getMessage();
	
	public String getMessage(ResourceBundle resourceBundle);
	
	public  String[] getParameters();
	
	enum Severity{INFO, WARN, ERROR};
	
	public Severity getSeverity();
	
	public String getModuleCode();

}
