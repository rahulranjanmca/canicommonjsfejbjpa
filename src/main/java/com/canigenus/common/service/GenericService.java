package com.canigenus.common.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.convert.Converter;

import com.canigenus.common.model.Identifiable;


public interface GenericService<T extends Identifiable<?>, U extends T> {

	public <E extends Identifiable<?>> void save(E model);

	public <E> void delete(E model);

	public void deleteById(Object id);

	public <E> void deleteDetached(E model);

	<E> void persist(E model, boolean clear);

	<E> void refresh(E model);

	<E extends Identifiable<?>> E saveOrUpdate(E model);

	<E extends Identifiable<?>> E update(E model);

	public <E> E get(Object id);

	public <E> E get(Object id, Class<E> classType);

	<E> E getPartialEntity(Class<E> clazz, Object id, String... fieldsToLoad);

	T getPartialEntity(Object id, String... fieldsToLoad);


	<E> List<E> getListWithPartialEntityByColumnNameAndValue(Class<E> clazz, String filterFieldName,
			Object filterFieldValue, String... fieldsToLoad);

	List<T> getListWithPartialEntityByColumnNameAndValue(String filterFieldName, Object filterFieldValue,
			String... fieldsToLoad);

	
	<E> List<E> getListByColumnNameAndValue(Class<E> clazz, String filterFieldName,
			Object filterFieldValue);

	List<T> getListByColumnNameAndValue(String filterFieldName, Object filterFieldValue);




	List<T> getList(U criteriaPopulator, int firstResult,
			int maxResult, Map<String, Boolean> orderBy,
			Map<String, Set<String>> joinTableWithFieldsToLoad,
			String... fieldsToLoad);



	List<T> getList(U criteriaPopulator, int firstResult,
			int maxResult, String... fieldsToLoad);

	
	

	 List<T> getList(U criteriaPopulator,
			int firstResult, int maxResult, Map<String, Boolean> orderBy,
			String... fieldsToLoad);


	
	 List<T> getList(U criteriaPopulator, String... fieldsToLoad);

	Long getCount(U criteriaPopulator);

	<E> boolean isUnique(Class<E> entity, String propertyName,
			Object propertyValue);

	boolean isUnique(String propertyName, Object propertyValue);

	<E> boolean isUniqueExceptThis(Class<E> entity, Identifiable<?> object,
			String propertyName, Object propertyValue);

	boolean isUniqueExceptThis(Identifiable<?> object, String propertyName,
			Object propertyValue);

	<E> boolean isUniqueForSaveOrUpdate(Class<E> entity,
			Identifiable<?> object, String propertyName, Object propertyValue);

	boolean isUniqueForSaveOrUpdate(Identifiable<?> object,
			String propertyName, Object propertyValue);

	<E> Long getMaxByColumn(Class<E> clazz, String column);

	Long getMaxByColumn(String column);

	

	<E> E getEntityByColumnNameAndValue(Class<E> clazz, String filterFieldName, Object filterFieldValue);

	T getEntityByColumnNameAndValue(String filterFieldName, Object filterFieldValue);

	Converter getConverter();


	List<T> search(int page, int pageSize, U example);

	T getWithChildById(Object id, String... fetchRelations);

	<E> E getWithChildById(Class<E> classType, Object id, String... fetchRelations);

     <E>	E getWithChild(Class<E> clazz, String filterFieldName, Object filterFieldValue,
			String... fetchRelations);

	T getWithChild(String filterFieldName, Object filterFieldValue,
			String... fetchRelations);
	
	public  Class<T> getEntityClazz();
	
	public  Class<U> getCriteriaClazz();
	

	List<T> getList(U criteriaPopulator2, int firstResult, int maxResult,
			Map<String, Boolean> orderBy, boolean distinct,
			Map<String, Set<String>> joinTableWithFieldsToLoad,
			String... fieldsToLoad);
	

}
