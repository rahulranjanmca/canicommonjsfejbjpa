package com.canigenus.common.controller;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

public class ElasticSearchSingleton {
	
	static TransportClient transportClient;
	
	static String indexName="";
	
	public static void init(){
		Settings settings = ImmutableSettings.settingsBuilder().put("cluster.name", "elasticsearch").build();
		transportClient = new TransportClient(settings);
		transportClient = transportClient.addTransportAddress(new InetSocketTransportAddress("localhost", 9300));
		indexName=EntityMangerFactorySingleton.getEMF().getProperties().get("hibernate.ogm.datastore.database").toString();
	}
	
	public static Client getClient(){
		return transportClient;
	}
	
	public static String getIndexName(){
		return indexName;
	}
	
	
	

}
