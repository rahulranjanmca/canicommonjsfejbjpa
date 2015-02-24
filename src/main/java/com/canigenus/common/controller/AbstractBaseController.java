package com.canigenus.common.controller;

import java.io.Serializable;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import com.canigenus.common.model.Identifiable;
import com.canigenus.common.service.GenericService;

public abstract class AbstractBaseController<T extends Identifiable<?>> implements Serializable {
	protected abstract GenericService<T> getService();

	private static final long serialVersionUID = 1L;

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	private T current;


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

	/*
	 * Support updating and deleting Member entities
	 */

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

	private T example = instanciateEntity();

	public int getPage() {
		return this.page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPageSize() {
		return 10;
	}

	public T getExample() {
		return this.example;
	}

	public void setExample(T example) {
		this.example = example;
	}

	public String search() {
		this.page = 0;
		return null;
	}

	public void paginate() {
		count = getService().getCount(example);
		pageItems = getService().paginate(page, example);

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
		return getService().getPartialModelList(getService().getClazz(), null,
				(String[]) null);
	}

	public Converter getConverter() {
		return getService().getConverter();
	}

	/*
	 * Support adding children to bidirectional, one-to-many tables
	 */

	private T add = instanciateEntity();

	public T getAdd() {
		return this.add;
	}

	public T getAdded() {
		T added = this.add;
		this.add = instanciateEntity();
		return added;
	}

	public T getCurrent() {
		return current;
	}

	public void setCurrent(T current) {
		this.current = current;
	}

	protected abstract T instanciateEntity();
}
