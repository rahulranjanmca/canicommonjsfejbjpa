package com.canigenus.common.service;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.ejb.SessionContext;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.FetchParent;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Selection;

import com.canigenus.common.model.Identifiable;
import com.canigenus.common.util.CriteriaPopulator;
import com.canigenus.common.util.JpaCriteriaHelper;

public abstract class GenericServiceImpl<E extends Identifiable<?>> implements
		GenericService<E, JpaCriteriaHelper<E>>, Serializable {
	private static final long serialVersionUID = 1L;

	@Override
	public <T> T get(Class<T> clazz, Object id, String... fieldsToLoad) {
		CriteriaBuilder criteriaBuilder = getEntityManager()
				.getCriteriaBuilder();
		CriteriaQuery<T> cq = criteriaBuilder.createQuery(clazz);
		Root<T> root = cq.from(clazz);
		cq.where(criteriaBuilder.equal(root.get("id"), id));
		List<Selection<?>> selections = new ArrayList<Selection<?>>();
		for (String fieldToLoad : fieldsToLoad) {
			selections.add(root.get(fieldToLoad));
		}
		cq.multiselect(selections);
		cq.orderBy(criteriaBuilder.desc(root.get("id")));
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	@Override
	public E get(Object id, String... fieldsToLoad) {
		return get(getClazz(), id, fieldsToLoad);
	}

	@Override
	public <T> boolean isUnique(Class<T> entity, String propertyName,
			Object propertyValue) {
		if (entity == null || propertyValue == null) {
			return true;
		}
		return getEntityManager()
				.createQuery(
						String.format(
								"select count(c) from %s c where %s = :propertyValue",
								entity.getSimpleName(), propertyName),
						Long.class)
				.setParameter("propertyValue", propertyValue) //
				.getSingleResult() == 0;
	}

	@Override
	public boolean isUnique(String propertyName, Object propertyValue) {
		// TODO Auto-generated method stub
		return isUnique(getClazz(), propertyName, propertyValue);
	}

	@Override
	public <T> boolean isUniqueExceptThis(Class<T> entity,
			Identifiable<?> object, String propertyName, Object propertyValue) {
		if (entity == null || propertyValue == null) {
			return true;
		}
		return getEntityManager()
				//
				.createQuery(
						//
						String.format(
								"select count(c) from %s c where %s = :propertyValue and %s != :id", //
								entity.getSimpleName(), propertyName, "id"),
						Long.class) //
				.setParameter("propertyValue", propertyValue) //
				.setParameter("id", object.getId()) //
				.getSingleResult() == 0;
	}

	@Override
	public boolean isUniqueExceptThis(Identifiable<?> object,
			String propertyName, Object propertyValue) {
		return isUniqueExceptThis(getClazz(), object, propertyName,
				propertyValue);
	}

	@Override
	public <T> boolean isUniqueForSaveOrUpdate(Class<T> entity,
			Identifiable<?> object, String propertyName, Object propertyValue) {
		if (entity == null || propertyValue == null) {
			return true;
		}
		if (object.getId() != null) {
			return isUniqueExceptThis(entity, object, propertyName,
					propertyValue);

		} else {
			return isUnique(entity, propertyName, propertyValue);
		}
	}

	@Override
	public boolean isUniqueForSaveOrUpdate(Identifiable<?> object,
			String propertyName, Object propertyValue) {
		// TODO Auto-generated method stub
		return isUniqueForSaveOrUpdate(getClazz(), object, propertyName,
				propertyValue);
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String filterFieldName,
			Object filterFieldValue, String... fieldsToLoad) {
		CriteriaBuilder criteriaBuilder = getEntityManager()
				.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(clazz);
		Root<T> r = criteriaQuery.from(clazz);
		criteriaQuery.where(criteriaBuilder.equal(r.get(filterFieldName),
				filterFieldValue));
		List<Selection<?>> selections = new ArrayList<Selection<?>>();
		for (String fieldToLoad : fieldsToLoad) {
			selections.add(r.get(fieldToLoad));
		}
		if (!selections.isEmpty()) {
			criteriaQuery.multiselect(selections);
		}
		criteriaQuery.orderBy(criteriaBuilder.desc(r.get("id")));
		return getEntityManager().createQuery(criteriaQuery).getResultList();
	}

	@Override
	public List<E> getList(String filterFieldName, Object filterFieldValue) {
		// TODO Auto-generated method stub
		return getList(getClazz(), filterFieldName, filterFieldValue);
	}

	@Override
	public List<E> getList(String filterFieldName, Object filterFieldValue,
			String... fieldsToLoad) {
		// TODO Auto-generated method stub
		return getList(getClazz(), filterFieldName, filterFieldValue,
				fieldsToLoad);
	}

	@Override
	public <T> Long getCount(Class<T> clazz,
			CriteriaPopulator<?> criteriaPopulator2) {
		@SuppressWarnings("unchecked")
		CriteriaPopulator<JpaCriteriaHelper<T>> criteriaPopulator = (CriteriaPopulator<JpaCriteriaHelper<T>>) criteriaPopulator2;
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		cq.select(cb.count(cq.from(clazz)));
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (criteriaPopulator != null) {
			JpaCriteriaHelper<T> jpaCriteriaHelper = new JpaCriteriaHelper<>();
			jpaCriteriaHelper.setCriteriaBuilder(cb);
			jpaCriteriaHelper.setCriteriaQuery(cq);
			jpaCriteriaHelper.setPredicates(predicates);
			criteriaPopulator.populateCriteria(jpaCriteriaHelper);
		}
		Predicate[] array = new Predicate[predicates.size()];
		array = predicates.toArray(array);
		cq.where(cb.and(array));

		return getEntityManager().createQuery(cq).getSingleResult();
	}

	@Override
	public Long getCount(
			CriteriaPopulator<JpaCriteriaHelper<E>> criteriaPopulator) {
		// TODO Auto-generated method stub
		return getCount(getClazz(), criteriaPopulator);
	}

	@Override
	public <T, U extends Number> U getSumByColumn(Class<T> clazz,
			Class<U> returningClass, CriteriaPopulator<?> criteriaPopulator,
			String column) {
		return getSumByColumn(clazz, returningClass, criteriaPopulator, column,
				-1, -1);
	}

	@Override
	public <T, U extends Number> U getSumByColumn(Class<T> clazz,
			Class<U> returningClass, CriteriaPopulator<?> criteriaPopulator2,
			String column, int start, int end) {
		@SuppressWarnings("unchecked")
		CriteriaPopulator<JpaCriteriaHelper<T>> criteriaPopulator = (CriteriaPopulator<JpaCriteriaHelper<T>>) criteriaPopulator2;
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<U> cq = cb.createQuery(returningClass);
		Root<T> personEntity = cq.from(clazz);
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (criteriaPopulator != null) {
			JpaCriteriaHelper<T> jpaCriteriaHelper = new JpaCriteriaHelper<>();
			jpaCriteriaHelper.setCriteriaBuilder(cb);
			jpaCriteriaHelper.setCriteriaQuery(cq);
			jpaCriteriaHelper.setPredicates(predicates);
			criteriaPopulator.populateCriteria(jpaCriteriaHelper);
		}

		Predicate[] array = new Predicate[predicates.size()];
		array = predicates.toArray(array);
		cq.where(cb.and(array));

		Path<U> ageAttr = personEntity.<U> get(column);
		cq = cq.select(cb.sum(ageAttr));

		TypedQuery<U> typedQuery = getEntityManager().createQuery(cq);
		if (start > 0) {
			typedQuery.setMaxResults(start);
			typedQuery.setFirstResult(end);
		}
		return typedQuery.getSingleResult();

	}

	@Override
	public <T> Long getMaxByColumn(Class<T> clazz, String column) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> cq = cb.createQuery(Long.class);
		Root<T> personEntity = cq.from(clazz);
		Path<Long> ageAttr = personEntity.<Long> get(column);
		cq = cq.select(cb.max(ageAttr));
		TypedQuery<Long> typedQuery = getEntityManager().createQuery(cq);
		Long number = typedQuery.getSingleResult();
		if (number == null) {
			return new Long(0);
		} else
			return number;

	}

	@Override
	public Long getMaxByColumn(String column) {
		// TODO Auto-generated method stub
		return getMaxByColumn(getClazz(), column);
	}

	@Override
	public <T> List<T> getList(Class<T> clazz,
			CriteriaPopulator<?> criteriaPopulator, String... fieldsToLoad) {
		return getList(clazz, criteriaPopulator, -1, -1, fieldsToLoad);
	}

	@Override
	public List<E> getList(
			CriteriaPopulator<JpaCriteriaHelper<E>> criteriaPopulator,
			String... fieldsToLoad) {
		return getList(getClazz(), criteriaPopulator, fieldsToLoad);
	}

	@Override
	public <T> List<T> getList(Class<T> clazz,
			CriteriaPopulator<?> criteriaPopulator, int firstResult,
			int maxResult, Map<String, Boolean> orderBy, String... fieldsToLoad) {
		return getList(clazz, criteriaPopulator, firstResult, maxResult,
				orderBy, null, fieldsToLoad);
	}

	@Override
	public List<E> getList(
			CriteriaPopulator<JpaCriteriaHelper<E>> criteriaPopulator,
			int firstResult, int maxResult, Map<String, Boolean> orderBy,
			String... fieldsToLoad) {
		// TODO Auto-generated method stub
		return getList(getClazz(), criteriaPopulator, firstResult, maxResult,
				orderBy, fieldsToLoad);
	}

	@Override
	public <T> List<T> getList(Class<T> clazz,
			CriteriaPopulator<?> criteriaPopulator, int firstResult,
			int maxResult, String... fieldsToLoad) {
		return getList(clazz, criteriaPopulator, firstResult, maxResult, null,
				fieldsToLoad);
	}

	@Override
	public <T> List<T> getList(Class<T> clazz,
			CriteriaPopulator<?> criteriaPopulator, int firstResult,
			int maxResult, Map<String, Boolean> orderBy,
			Map<String, Set<String>> joinTableWithFieldsToLoad,
			String... fieldsToLoad) {
		return getList(clazz, criteriaPopulator, firstResult, maxResult,
				orderBy, false, joinTableWithFieldsToLoad, fieldsToLoad);

	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> List<T> getList(Class<T> clazz,
			CriteriaPopulator<?> criteriaPopulator2, int firstResult,
			int maxResult, Map<String, Boolean> orderBy, boolean distinct,
			Map<String, Set<String>> joinTableWithFieldsToLoad,
			String... fieldsToLoad) {

		CriteriaPopulator<JpaCriteriaHelper<T>> criteriaPopulator = (CriteriaPopulator<JpaCriteriaHelper<T>>) criteriaPopulator2;
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> r = cq.from(clazz);
		List<Predicate> predicates = new ArrayList<Predicate>();
		if (criteriaPopulator != null) {
			JpaCriteriaHelper<T> jpaCriteriaHelper = new JpaCriteriaHelper<>();
			jpaCriteriaHelper.setCriteriaBuilder(cb);
			jpaCriteriaHelper.setCriteriaQuery(cq);
			jpaCriteriaHelper.setPredicates(predicates);
			criteriaPopulator.populateCriteria(jpaCriteriaHelper);
		}
		Predicate[] array = new Predicate[predicates.size()];
		array = predicates.toArray(array);
		cq.where(cb.and(array));

		List<Selection<?>> selections = new ArrayList<Selection<?>>();
		if (fieldsToLoad != null) {
			for (String fieldToLoad : fieldsToLoad) {
				selections.add(r.get(fieldToLoad));

			}
		}
		if (joinTableWithFieldsToLoad != null) {
			for (String joinTable : joinTableWithFieldsToLoad.keySet()) {
				Join<Object, Object> join = (Join<Object, Object>) r.fetch(
						joinTable, JoinType.LEFT);

				for (String fieldsToLoad2 : joinTableWithFieldsToLoad
						.get(joinTable)) {

					selections.add(join.get(fieldsToLoad2));
				}
			}
		}
		if (!selections.isEmpty()) {
			cq.multiselect(selections);
		}
		if (orderBy == null) {
			cq.orderBy(cb.desc(r.get("id")));
		} else {
			for (Map.Entry<String, Boolean> order : orderBy.entrySet()) {
				if (order.getValue() == false) {
					cq.orderBy(cb.desc(r.get(order.getKey())));
				} else {
					cq.orderBy(cb.asc(r.get(order.getKey())));
				}

			}
		}
		if (distinct) {
			cq.distinct(true);
		}
		TypedQuery<T> typedQuery = getEntityManager().createQuery(cq);
		if (maxResult > 0) {
			typedQuery.setMaxResults(maxResult);
			typedQuery.setFirstResult(firstResult);
		}

		return typedQuery.getResultList();
	}

	@Override
	public <T> List<T> getList(Class<T> clazz, String filterFieldName,
			Object filterFieldValue) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> r = cq.from(clazz);
		if (filterFieldValue == null) {
			cq.where(cb.isNull(r.get(filterFieldName)));
		} else {
			cq.where(cb.equal(r.get(filterFieldName), filterFieldValue));
		}
		cq.orderBy(cb.desc(r.get("id")));
		return getEntityManager().createQuery(cq).getResultList();
	}

	@Override
	public <T> T get(Class<T> clazz, String filterFieldName,
			Object filterFieldValue) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> r = cq.from(clazz);
		if (filterFieldValue == null) {
			cq.where(cb.isNull(r.get(filterFieldName)));
		} else {
			cq.where(cb.equal(r.get(filterFieldName), filterFieldValue));
		}
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	@Override
	public <T> T getWithChild(Class<T> clazz, String filterFieldName,
			Object filterFieldValue, String... fetchRelations) {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(clazz);
		Root<T> r = cq.from(clazz);
		if (filterFieldValue == null) {
			cq.where(cb.isNull(r.get(filterFieldName)));
		} else {
			cq.where(cb.equal(r.get(filterFieldName), filterFieldValue));
		}

		for (String relation : fetchRelations) {
			FetchParent<T, T> fetch = r;
			for (String pathSegment : relation.split("\\.")) {
				fetch = fetch.fetch(pathSegment, JoinType.LEFT);

			}
		}
		return getEntityManager().createQuery(cq).getSingleResult();
	}

	@Override
	public E getWithChild(String filterFieldName, Object filterFieldValue,
			String... fetchRelations) {
		return getWithChild(getClazz(), filterFieldName, filterFieldValue,
				fetchRelations);
	}

	@Override
	public E get(String filterFieldName, Object filterFieldValue) {
		return get(getClazz(), filterFieldName, filterFieldValue);
	}

	@Override
	public <T> T update(T model) {
		return getEntityManager().merge(model);
	}

	@Override
	public <T> void save(T model) {
		getEntityManager().persist(model);
	}

	@Override
	public <T extends Identifiable<?>> T saveOrUpdate(T model) {
		if (model.getId() != null) {
			return getEntityManager().merge(model);
		} else {
			getEntityManager().persist(model);
			return model;
		}
	}

	@Override
	public <T> void refresh(T model) {
		if (!getEntityManager().contains(model)) {
			getEntityManager().merge(model);
		}
		getEntityManager().refresh(model);
	}

	@Override
	public <T> void persist(T model, boolean clear) {
		if (clear)
			getEntityManager().clear();
		getEntityManager().merge(model);
	}

	@Override
	public <T> void delete(T model) {
		getEntityManager().remove(model);
	}

	@Override
	public <T> T get(Object id, Class<T> classType) {
		T t = getEntityManager().find(classType, id);
		return t;
	}

	
	@Override
	public E get(Object id) {
		E t = getEntityManager().find(getClazz(), id);
		return t;
	}

	@Override
	public E getWithChild(Object id, String... fetchRelations) {
		CriteriaBuilder criteriaBuilder = getEntityManager()
				.getCriteriaBuilder();
		CriteriaQuery<E> criteriaQuery = criteriaBuilder
				.createQuery(getClazz());
		Root<E> root = criteriaQuery.from(getClazz());

		for (String relation : fetchRelations) {
			FetchParent<E, E> fetch = root;
			for (String pathSegment : relation.split("\\.")) {
				fetch = fetch.fetch(pathSegment, JoinType.LEFT);

			}
		}

		criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));

		return getEntityManager().createQuery(criteriaQuery).getSingleResult();
	}

	@Override
	public <T> T getWithChild(Class<T> classType, Object id,
			String... fetchRelations) {
		CriteriaBuilder criteriaBuilder = getEntityManager()
				.getCriteriaBuilder();
		CriteriaQuery<T> criteriaQuery = criteriaBuilder.createQuery(classType);
		Root<T> root = criteriaQuery.from(classType);

		for (String relation : fetchRelations) {
			FetchParent<T, T> fetch = root;
			for (String pathSegment : relation.split("\\.")) {
				fetch = fetch.fetch(pathSegment, JoinType.LEFT);

			}
		}
		criteriaQuery.where(criteriaBuilder.equal(root.get("id"), id));

		return getEntityManager().createQuery(criteriaQuery).getSingleResult();
	}

	@Override
	public <T> void deleteDetached(T model) {
		getEntityManager().remove(getEntityManager().merge(model));
	}

	@Override
	public void deleteById(Object id) {
		getEntityManager().remove(getEntityManager().find(getClazz(), id));
	}

	public abstract EntityManager getEntityManager();

	@Override
	public abstract Class<E> getClazz();

	@Override
	public Long getCount(E example) {
		// Populate this.count
		CriteriaBuilder builder = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<E> root = countCriteria.from(getClazz());
		countCriteria = countCriteria.select(builder.count(root)).where(
				getSearchPredicates(root, example));
		return getEntityManager().createQuery(countCriteria).getSingleResult();
	}

	@Override
	public List<E> search(int page, int pageSize, E example) {
		CriteriaBuilder builder = this.getEntityManager().getCriteriaBuilder();

		// Populate this.count

		CriteriaQuery<Long> countCriteria = builder.createQuery(Long.class);
		Root<E> root = countCriteria.from(getClazz());

		// Populate this.pageItems

		CriteriaQuery<E> criteria = builder.createQuery(getClazz());
		root = criteria.from(getClazz());
		TypedQuery<E> query = getEntityManager()
				.createQuery(
						criteria.select(root).where(
								getSearchPredicates(root, example)));
		if (page >= 0) {
			query.setFirstResult(page * pageSize).setMaxResults(pageSize);
		}
		return query.getResultList();

	}

	protected abstract Predicate[] getSearchPredicates(Root<E> root, E example);

	@Resource
	private SessionContext sessionContext;

	@Override
	public Converter getConverter() {
		// final MemberBean ejbProxy =
		// this.sessionContext.getBusinessObject(getClass());
		return new Converter() {

			@Override
			public Object getAsObject(FacesContext context,
					UIComponent component, String value) {

				return getEntityManager().find(getClazz(), Long.valueOf(value));
			}

			@SuppressWarnings("unchecked")
			@Override
			public String getAsString(FacesContext context,
					UIComponent component, Object value) {

				if (value == null) {
					return "";
				}

				return String.valueOf(((E) value).getId());
			}
		};
	}

	@Override
	public List<E> getList(
			CriteriaPopulator<JpaCriteriaHelper<E>> criteriaPopulator,
			int firstResult, int maxResult, Map<String, Boolean> orderBy,
			Map<String, Set<String>> joinTableWithFieldsToLoad,
			String... fieldsToLoad) {
		return getList(getClazz(), criteriaPopulator, firstResult, maxResult,
				orderBy, joinTableWithFieldsToLoad, fieldsToLoad);
	}

	@Override
	public List<E> getList(
			CriteriaPopulator<JpaCriteriaHelper<E>> criteriaPopulator,
			int firstResult, int maxResult, String... fieldsToLoad) {
		// TODO Auto-generated method stub
		return getList(getClazz(), criteriaPopulator, firstResult, maxResult,
				fieldsToLoad);
	}

}
