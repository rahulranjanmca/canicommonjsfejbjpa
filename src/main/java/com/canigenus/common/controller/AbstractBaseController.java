package com.canigenus.common.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;



import com.canigenus.common.model.Identifiable;
import com.canigenus.common.service.GenericServiceImpl;

public abstract class AbstractBaseController<T extends Identifiable<?>, U extends T> implements Serializable, Controllable<T, U> {
	public abstract GenericServiceImpl<T, U> getService();
	
	private Class<T> t;
	private Class<U> u;
	
	
	
	
	public AbstractBaseController() {
	}
	
	public AbstractBaseController(Class<T> t, Class<U> u) {
		this.t=t;
		this.u=u;
		 example= instantiateCriteria();
	}

	private static final long serialVersionUID = 1L;

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	protected T current;
	


	public String create() {
		return "create?faces-redirect=true";
	}

	public void retrieve() {

		if (FacesContext.getCurrentInstance().isPostback()) {
			return;
		}
		if (this.id == null) {
			this.current = this.example;
		} else {
			this.current = findById(getId());
		}
	}

	public T findById(Long id) {

		return getService().get(id);
	}

	public String update() {
			try {
			if (this.id == null) {
				getService().save(this.current);
				return "search?faces-redirect=true";
			} else {
				getService().update(this.current);
				return "view?faces-redirect=true&id=" + this.current.getId();
			}
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	public String delete() {
		try {
			getService().deleteById(getId());
			;
			return "search?faces-redirect=true";
		} catch (Exception e) {
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(e.getMessage()));
			return null;
		}
	}

	/*
	 * Support searching Member entities with pagination
	 */

	private int page;
	private long count;
	private List<T> pageItems;

	protected U example;

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public U getExample() {
		return this.example;
	}

	public void setExample(U example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {
		count = getService().getCount(getExample());
		pageItems = getService().search(page,getPageSize(), getExample());

	}

	public List<T> getPageItems() {
		return this.pageItems;
	}

	public long getCount() {
		return this.count;
	}

	/*
	 * Support listing and POSTing back Member entities (e.g. from inside an
	 * HtmlSelectOneMenu)
	 */

	public List<T> getAll() {
		return getService().getList(instantiateCriteria());
	}

	public Converter getConverter() {
		return getService().getConverter();
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	

	public T getCurrent() {
		return current;
	}

	public void setCurrent(T current) {
		this.current = current;
	}

	@Override
	public T instantiateEntity() {
		try {
			return t.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public U instantiateCriteria() {
		try {
			return u.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public void setPageItems(List<T> pageItems) {
		this.pageItems = pageItems;
	}
	public  Class<T> getEntityClazz(){
		return t;
	};
	
	public  Class<U> getCriteriaClazz(){
		return u;
	};
	
	
}
