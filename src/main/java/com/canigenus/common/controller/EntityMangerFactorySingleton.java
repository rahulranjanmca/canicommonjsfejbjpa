package com.canigenus.common.controller;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

@Named
@ApplicationScoped
@Eager
public class EntityMangerFactorySingleton {
	private static EntityManagerFactory emf;
	static {
		emf = Persistence.createEntityManagerFactory("secondory");
		;
		
	}

	public static EntityManagerFactory getEMF() {
		return emf;
	}

	@Produces
	@Dependent
	public EntityManager createEntityManager() {
		System.out.println(EntityMangerFactorySingleton.getEMF()
				.getProperties().get("hibernate.ogm.datastore.database"));
		return emf.createEntityManager();
	}

	public void closeEM(@Disposes EntityManager manager) {
		manager.close();
	}
}
