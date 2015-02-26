package com.canigenus.common.service;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.convert.Converter;

import com.canigenus.common.model.Identifiable;
import com.canigenus.common.util.CriteriaPopulator;

public interface GenericService<E extends Identifiable<?>, F> {

	public <T> void save(T model);

	public <T> void delete(T model);

	public void deleteById(Object id);

	public <T> void deleteDetached(T model);

	<T> void persist(T model, boolean clear);

	<T> void refresh(T model);

	<T extends Identifiable<?>> T saveOrUpdate(T model);

	<T> T update(T model);

	/**
	 * @param clazz
	 * @param id
	 * @param fieldsToLoad
	 * @return
	 */

	public <T> T get(Object id);

	public <T> T get(Object id, Class<T> classType);

	<T> T get(Class<T> clazz, Object id, String... fieldsToLoad);

	E get(Object id, String... fieldsToLoad);

	/**
	 * @param clazz
	 * @param filterFieldName
	 * @param filterFieldValue
	 * @param fieldsToLoad
	 * @return
	 */
	<T> List<T> getList(Class<T> clazz, String filterFieldName,
			Object filterFieldValue, String... fieldsToLoad);

	List<E> getList(String filterFieldName, Object filterFieldValue,
			String... fieldsToLoad);

	/**
	 * @param clazz
	 * @param filterFieldName
	 * @param filterFieldValue
	 * @return
	 */
	<T> List<T> getList(Class<T> clazz, String filterFieldName,
			Object filterFieldValue);

	List<E> getList(String filterFieldName, Object filterFieldValue);

	/**
	 * @param clazz
	 * @param criteriaPopulator
	 * @param firstResult
	 * @param maxResult
	 * @param orderBy
	 * @param joinTableWithFieldsToLoad
	 * @param fieldsToLoad
	 * @return
	 */
	<T> List<T> getList(Class<T> clazz, CriteriaPopulator<F> criteriaPopulator,
			int firstResult, int maxResult, Map<String, Boolean> orderBy,
			Map<String, Set<String>> joinTableWithFieldsToLoad,
			String... fieldsToLoad);

	List<E> getList(CriteriaPopulator<F> criteriaPopulator, int firstResult,
			int maxResult, Map<String, Boolean> orderBy,
			Map<String, Set<String>> joinTableWithFieldsToLoad,
			String... fieldsToLoad);

	/**
	 * @param clazz
	 * @param criteriaPopulator
	 * @param firstResult
	 * @param maxResult
	 * @param fieldsToLoad
	 * @return
	 */
	<T> List<T> getList(Class<T> clazz, CriteriaPopulator<F> criteriaPopulator,
			int firstResult, int maxResult, String... fieldsToLoad);

	List<E> getList(CriteriaPopulator<F> criteriaPopulator, int firstResult,
			int maxResult, String... fieldsToLoad);

	/**
	 * @param clazz
	 * @param criteriaPopulator
	 * @param firstResult
	 * @param maxResult
	 * @param orderBy
	 * @param fieldsToLoad
	 * @return
	 */
	<T> List<T> getList(Class<T> clazz, CriteriaPopulator<F> criteriaPopulator,
			int firstResult, int maxResult, Map<String, Boolean> orderBy,
			String... fieldsToLoad);

	 List<E> getList(CriteriaPopulator<F> criteriaPopulator,
			int firstResult, int maxResult, Map<String, Boolean> orderBy,
			String... fieldsToLoad);

	/**
	 * @param clazz
	 * @param criteriaPopulator
	 * @param fieldsToLoad
	 * @return
	 */
	<T> List<T> getList(Class<T> clazz,
			CriteriaPopulator<F> criteriaPopulator, String... fieldsToLoad);
	
	 List<E> getList(CriteriaPopulator<F> criteriaPopulator, String... fieldsToLoad);
	

	/**
	 * @param clazz
	 * @param criteriaPopulator
	 * @return
	 */
	<T> Long getCount(Class<T> clazz, CriteriaPopulator<F> criteriaPopulator);
	
	Long getCount(CriteriaPopulator<F> criteriaPopulator);

	<T, U extends Number> U getSumByColumn(Class<T> clazz,
			Class<U> returningClass, CriteriaPopulator<F> criteriaPopulator,
			String column);

	<T, U extends Number> U getSumByColumn(Class<T> clazz,
			Class<U> returningClass, CriteriaPopulator<F> criteriaPopulator,
			String column, int start, int end);

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

	<T> List<T> getList(Class<T> clazz, CriteriaPopulator<F> criteriaPopulator,
			int firstResult, int maxResult, Map<String, Boolean> orderBy,
			boolean distinct,
			Map<String, Set<String>> joinTableWithFieldsToLoad,
			String... fieldsToLoad);

	<T> T get(Class<T> clazz, String filterFieldName, Object filterFieldValue);

	E get(String filterFieldName, Object filterFieldValue);

	Converter getConverter();

	Long getCount(E example);

	List<E> search(int page, E example);

	Class<E> getClazz();

	E getWithChild(Object id, String... fetchRelations);

	<T> T getWithChild(Class<T> classType, Object id, String... fetchRelations);
	

}
