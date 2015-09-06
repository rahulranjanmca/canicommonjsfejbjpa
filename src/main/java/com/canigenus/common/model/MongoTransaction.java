package com.canigenus.common.model;

import java.util.HashMap;
import java.util.Map;

public class MongoTransaction {
	
	String id;
	
	String state;
	
	String lastModified;
	
	String type;
	
	Map<String,String> informations= new HashMap<>();
	
	

}
