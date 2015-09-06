package com.canigenus.common.controller;

import java.net.UnknownHostException;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import com.canigenus.common.converter.BigDecimalConverter;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

public class MongoClientSigleton {

	static Datastore dataStore;
	static MongoClient mongoClient;
	private static EntityManagerFactory emf;
	static{
		try {
			emf = Persistence.createEntityManagerFactory("secondory");
			;
			MongoClientSigleton.mongoClient = new MongoClient(new MongoClientURI("mongodb://"
					+ emf.getProperties().get("hibernate.ogm.datastore.username")
					+ ":"
					+ emf.getProperties().get(
							"hibernate.ogm.datastore.password") + "@"
					+ emf.getProperties().get("hibernate.ogm.datastore.host")
					+ ":"
					+ emf.getProperties().get("hibernate.ogm.datastore.port")
					+ "/"
					+ emf.getProperties().get("hibernate.ogm.datastore.database")));
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		
		  Morphia morphia = new Morphia();
		  morphia.getMapper().getConverters().addConverter(BigDecimalConverter.class);
		  MongoClientSigleton.dataStore = morphia.createDatastore(MongoClientSigleton.mongoClient, emf.getProperties().get("hibernate.ogm.datastore.database").toString());
	      morphia.mapPackage("com.canigenus");
	}
	public static MongoClient getMongoClient() {
		return mongoClient;
	}
	public static Datastore getDatastore() {
		return dataStore;
	}

}
