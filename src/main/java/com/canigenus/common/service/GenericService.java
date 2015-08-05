package com.canigenus.common.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.convert.Converter;

import com.canigenus.common.model.Identifiable;


public interface GenericService<E extends Identifiable<?>, F extends E> {

	public <T> void save(T model);

	public <T> void delete(T model);

	public void deleteById(Object id);

	public <T> void deleteDetached(T model);

	<T> void persist(T model, boolean clear);

	<T> void refresh(T model);

	<T extends Identifiable<?>> T saveOrUpdate(T model);

	<T extends Identifiable<?>> T update(T model);

	public <T> T get(Object id);

	public <T> T get(Object id, Class<T> classType);

	<T> T getPartialEntity(Class<T> clazz, Object id, String... fieldsToLoad);

	E getPartialEntity(Object id, String... fieldsToLoad);


	<T> List<T> getListWithPartialEntityByColumnNameAndValue(Class<T> clazz, String filterFieldName,
			Object filterFieldValue, String... fieldsToLoad);

	List<E> getListWithPartialEntityByColumnNameAndValue(String filterFieldName, Object filterFieldValue,
			String... fieldsToLoad);

	
	<T> List<T> getListByColumnNameAndValue(Class<T> clazz, String filterFieldName,
			Object filterFieldValue);

	List<E> getListByColumnNameAndValue(String filterFieldName, Object filterFieldValue);




	List<E> getList(F criteriaPopulator, int firstResult,
			int maxResult, Map<String, Boolean> orderBy,
			Map<String, Set<String>> joinTableWithFieldsToLoad,
			String... fieldsToLoad);



	List<E> getList(F criteriaPopulator, int firstResult,
			int maxResult, String... fieldsToLoad);

	
	

	 List<E> getList(F criteriaPopulator,
			int firstResult, int maxResult, Map<String, Boolean> orderBy,
			String... fieldsToLoad);


	
	 List<E> getList(F criteriaPopulator, String... fieldsToLoad);

	Long getCount(F criteriaPopulator);

	<T> boolean isUnique(Class<T> entity, String propertyName,
			Object propertyValue);

	boolean isUnique(String propertyName, Object propertyValue);

	<T> boolean isUniqueExceptThis(Class<T> entity, Identifiable<?> object,
			String propertyName, Object propertyValue);

	boolean isUniqueExceptThis(Identifiable<?> object, String propertyName,
			Object propertyValue);

	<T> boolean isUniqueForSaveOrUpdate(Class<T> entity,
			Identifiable<?> object, String propertyName, Object propertyValue);

	boolean isUniqueForSaveOrUpdate(Identifiable<?> object,
			String propertyName, Object propertyValue);

	<T> Long getMaxByColumn(Class<T> clazz, String column);

	Long getMaxByColumn(String column);

	

	<T> T get(Class<T> clazz, String filterFieldName, Object filterFieldValue);

	E get(String filterFieldName, Object filterFieldValue);

	Converter getConverter();


	List<E> search(int page, int pageSize, F example);

	E getWithChildById(Object id, String... fetchRelations);

	<T> T getWithChildById(Class<T> classType, Object id, String... fetchRelations);

     <T>	T getWithChild(Class<T> clazz, String filterFieldName, Object filterFieldValue,
			String... fetchRelations);

	E getWithChild(String filterFieldName, Object filterFieldValue,
			String... fetchRelations);
	
	public  Class<E> getEntityClazz();
	
	public  Class<F> getCriteriaClazz();
	

	List<E> getList(F criteriaPopulator2, int firstResult, int maxResult,
			Map<String, Boolean> orderBy, boolean distinct,
			Map<String, Set<String>> joinTableWithFieldsToLoad,
			String... fieldsToLoad);
	

}
