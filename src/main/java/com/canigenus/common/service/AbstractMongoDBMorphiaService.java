package com.canigenus.common.service;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;

import org.bson.types.ObjectId;
import org.mongodb.morphia.query.Query;

import com.canigenus.common.controller.EntityMangerFactorySingleton;
import com.canigenus.common.controller.MongoClientSigleton;
import com.canigenus.common.model.Identifiable;
import com.canigenus.common.util.JavaUtil;

@SuppressWarnings("unchecked")
public abstract class AbstractMongoDBMorphiaService<T extends Identifiable<?>, U extends T> extends
		GenericServiceImpl<T, U> {

	Class<T> t;
	Class<U> u;

	public AbstractMongoDBMorphiaService(Class<T> t,Class<U> u)
	{
		this.t=t;
		this.u=u;
		
	}
	

	public AbstractMongoDBMorphiaService()
	{
		
	}
	private static final long serialVersionUID = 1L;

	@Override
	public <E extends Identifiable<?>> E update(E model) {
		
		if(JavaUtil.isBlank(((Identifiable<String>)model).getId()))
		{
			((Identifiable<String>)model).setId(new ObjectId().toString());
			MongoClientSigleton.getDatastore().save(model);
		}
		else{
			MongoClientSigleton.getDatastore().merge(model);
		}
		
		return model;
	}

	@Override
	public <E> void deleteDetached(E model) {
		 MongoClientSigleton.getDatastore().delete(model);
		}
	
	@Override
	public <E> void delete(E model) {
		 MongoClientSigleton.getDatastore().delete(model);;
	}
	
	
	
	@Override
	public T get(Object id) {
		return  MongoClientSigleton.getDatastore().get(getEntityClazz(), id);
		/*T t = getEntityManager().find(getEntityClazz(), id);
		return t;*/
	}

	@Override
	public List<T> getList(
			U example, String... fieldsToLoad) {
		
		List<T> o=null;
		
		Query<T> query= MongoClientSigleton.getDatastore().createQuery(getEntityClazz());
	/*	String criteria=addCriteria(example);
		if(!JavaUtil.isBlank(criteria))
		{
			query=query.where(criteria);
		}*/
	
		o=query.asList();
		
		return o;
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

		Query<T> query= MongoClientSigleton.getDatastore().createQuery(getEntityClazz());
	/*	String criteria=addCriteria(example);
		if(!JavaUtil.isBlank(criteria))
		{
			query=query.where("{"+criteria+"}");
		}*/
		
		return query.countAll();
	}
	
	public T getWithChildById(Object id, String... fetchRelations){
		/*EntityManager em=EntityMangerFactorySingleton.getEMF().createEntityManager();
		T entity = getEntityManager().createQuery(
				"select e from " + getEntityClazz().getSimpleName() + " e "+ "where e.id='"+id+"'",
				getEntityClazz()).getSingleResult();
		em.close();
		return entity;*/
		return  MongoClientSigleton.getDatastore().get(getEntityClazz(), id);
	}

	@Override
	public T getEntityByColumnNameAndValue(String filterFieldName, Object filterFieldValue) {
		return  MongoClientSigleton.getDatastore().createQuery(getEntityClazz()).field(filterFieldName).equal(filterFieldName).get();
		
	}
	
	@Override
	public <E> E getEntityByColumnNameAndValue(Class<E> clazz, String filterFieldName,
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
		
			Query<T> query= MongoClientSigleton.getDatastore().createQuery(getEntityClazz());
			query=addCriteria(example, query);
			/*if(!JavaUtil.isBlank(criteria))
			{
				query=query.where("{"+criteria+"}");
			}*/
			if(page>=0)
			{
				query=query.offset(page*pageSize).limit(pageSize);
			}
			o=query.asList();
		return o;
	}
	
	
	
	protected Query<T> addCriteria(U searchCriteria, Query<T> query){return query;}
	
	
}
