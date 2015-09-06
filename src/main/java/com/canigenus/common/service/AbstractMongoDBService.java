package com.canigenus.common.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;

import com.canigenus.common.controller.EntityMangerFactorySingleton;
import com.canigenus.common.model.Identifiable;

@SuppressWarnings("unchecked")
public abstract class AbstractMongoDBService<T extends Identifiable<?>, U extends T> extends
		GenericServiceImpl<T, U> {

	Class<T> t;
	Class<U> u;

	public AbstractMongoDBService(Class<T> t,Class<U> u)
	{
		this.t=t;
		this.u=u;
		
	}
	

	public AbstractMongoDBService()
	{
		
	}
	private static final long serialVersionUID = 1L;

	@Override
	public <E extends Identifiable<?>> E update(E model) {
		try{
		EntityManagerFactory emf = EntityMangerFactorySingleton.getEMF();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		if (model.getId() == null ) {
			em.persist(model);
		} else {
			model=em.merge(model);
		}
		em.getTransaction().commit();
		
		
		em.close();
		}catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return model;
	}

	@Override
	public <E> void deleteDetached(E model) {
		EntityManagerFactory emf = EntityMangerFactorySingleton.getEMF();
		EntityManager em = emf.createEntityManager();
		em.getTransaction().begin();
		model = em.merge(model);
		em.remove(model);
		// if(t.isActive())
		em.getTransaction().commit();
		em.close();

	}

	@Override
	public List<T> getList(
			U criteriaPopulator, String... fieldsToLoad) {
		EntityManagerFactory emf = EntityMangerFactorySingleton.getEMF();
		EntityManager em = emf.createEntityManager();
		List<T> list = em.createQuery(
				"select e from " + getEntityClazz().getSimpleName() + " e", getEntityClazz())
				.getResultList();
		em.close();
		return list;
	}

	

	/*@Override
	public List<T> search(int page, int pageSize, U example) {
		EntityManager em=EntityMangerFactorySingleton.getEMF().createEntityManager();
		String query=	"select e from " + getEntityClazz().getSimpleName() + " e";
		
		List<T> list;
	    if(page<0)
	    {
	    	 list = em.createQuery(query,
					getEntityClazz()).getResultList();
	    }
	    else{
	    	 list = em.createQuery(query,
						getEntityClazz()).setMaxResults(pageSize).setFirstResult(page*pageSize).getResultList();
	    }
	    em.close();
		
		return list;
	}*/

	@Override
	public Long getCount(U example) {
				Long l= (Long)getEntityManager().createNativeQuery(getMongoDbCountQuery(example)).getSingleResult();
				return l;
	}
	
	public T getWithChildById(Object id, String... fetchRelations){
		EntityManager em=EntityMangerFactorySingleton.getEMF().createEntityManager();
		T entity = getEntityManager().createQuery(
				"select e from " + getEntityClazz().getSimpleName() + " e "+ "where e.id='"+id+"'",
				getEntityClazz()).getSingleResult();
		em.close();
		return entity;
	}

	@Override
	public T get(String filterFieldName, Object filterFieldValue) {
		try{
		T entity = getEntityManager().createQuery(
				"select e from " + getEntityClazz().getSimpleName() + " e "+ "where e."+filterFieldName+"='"+filterFieldValue+"'",
				getEntityClazz()).getSingleResult();
		return entity;
		}
		catch (NoResultException nre){
			return null;
		}
		
	}
	
	@Override
	public <E> E get(Class<E> clazz, String filterFieldName,
			Object filterFieldValue) {
		EntityManager em=EntityMangerFactorySingleton.getEMF().createEntityManager();
		try{
			E entity = em.createQuery(
					"select e from " + clazz.getSimpleName() + " e "+ "where e."+filterFieldName+"='"+filterFieldValue+"'",
					clazz).getSingleResult();
			
			return entity;
			}
			catch (NoResultException nre){
				return null;
			}
		finally{
			em.close();
		}
	}

	@Override
	public Class<T> getEntityClazz() {
		return t;
	}
	
	@Override
	public Class<U> getCriteriaClazz() {
		return u;
	}
	
	@Override
	public List<T> search(int page, int pageSize,
			U example) {
		List<T> o=null;
		if(page<0)
		{
		 o=getEntityManager().createNativeQuery(getMongoDbNativeQuery(example), getEntityClazz()).getResultList();
		}
		else{
			 o=getEntityManager().createNativeQuery(getMongoDbNativeQuery(example),getEntityClazz()).setFirstResult(page*pageSize).setMaxResults(pageSize).getResultList();
		}
    	
		return o;
	}
	
	public String getMongoDbNativeQuery(U searchCriteria){
		String str="db."+getEntityClazz().getSimpleName()+".find({";
				str =str+ addCriteria(searchCriteria);
				str=str+ "})";
		return str;
	}
	
	public String getMongoDbCountQuery(U searchCriteria){
		String str="db."+getEntityClazz().getSimpleName()+".count({";
				str=str + addCriteria(searchCriteria);
				str=str+ "})";

				return str;
	}
	
	protected String addCriteria(U searchCriteria){return "";}
	
	
}
