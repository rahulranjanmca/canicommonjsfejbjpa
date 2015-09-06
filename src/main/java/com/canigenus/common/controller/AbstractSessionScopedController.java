/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.canigenus.common.controller;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpSession;

import com.canigenus.common.model.Identifiable;
import com.canigenus.common.service.GenericService;
import com.canigenus.common.util.JsfUtil;

/**
 * 
 * @author Rahul
 */
public abstract class AbstractSessionScopedController<T extends Identifiable<?>, U extends T> implements
		Serializable, Controllable<T, U> {
	
	
	private Class<T> t;
	private Class<U> u;
	public AbstractSessionScopedController() {
	}
	
	public AbstractSessionScopedController(Class<T> t, Class<U> u) {
		this.t=t;
		this.u=u;
		example = instantiateCriteria();
	}

	protected void addMessage(String str) {
		FacesMessage msg = new FacesMessage(str);
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}

	protected void removeBeanFromSession(String beanName) {
		HttpSession session = (HttpSession) FacesContext.getCurrentInstance()
				.getExternalContext().getSession(true);
		session.removeAttribute(beanName);
	}


	private static final long serialVersionUID = 8983428198453421888L;

	protected T current;

	public abstract GenericService<T, U> getService();

	public String prepareList() {
		return "List";
	}

	public String prepareView() {
		return "View";
	}

	public String prepareCreate() {
		current = instantiateEntity();
		return "Edit";
	}

	public String create() {
		try {
			setCurrent(getService().update(current));;
			JsfUtil.addSuccessMessage(getEntityClazz().getSimpleName()+" Created");
			return "List";
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle")
					.getString("PersistenceErrorOccured"));
			return null;
		}
	}

	public String prepareEdit() {
		return "Edit";
	}

	public String update() {
		try {
			setCurrent(getService().update(current));;
			JsfUtil.addSuccessMessage(getEntityClazz().getSimpleName()+" Updated");
			return "List";
		} catch (Exception e) {
			e.printStackTrace();
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle")
					.getString("PersistenceErrorOccured"));
			return null;
		}
	}


	/*private void recreateModel() {
		items = null;
	}*/

	public String destroy() {

		try {
			getService().deleteDetached(current);
			JsfUtil.addSuccessMessage(getEntityClazz().getSimpleName()+" Deleted");
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle")
					.getString("PersistenceErrorOccured"));
			return null;
		}

		search();

		return "List";

	}
	
	public String destroy(Long id) {
		try {
			getService().deleteById(id);
			JsfUtil.addSuccessMessage(getEntityClazz().getSimpleName()+" Deleted");
		} catch (Exception e) {
			JsfUtil.addErrorMessage(e, ResourceBundle.getBundle("/Bundle")
					.getString("PersistenceErrorOccured"));
			return null;
		}

		search();

		return "List";

	}
	
	public void destroyAndStayOnSamePage(Long id){
		destroy(id);
	}

	public SelectItem[] getItemsAvailableSelectMany() {
		return JsfUtil.getSelectItemsWithId(
				getService().getList(instantiateCriteria()), false);
	}

	public SelectItem[] getItemsAvailableSelectOne() {
		return JsfUtil.getSelectItemsWithId(
				getService().getList(instantiateCriteria()), true);
	}

	public T getCurrent() {
		return current;
	}

	public void setCurrent(T current) {
		this.current = current;
	}

	
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
	
	public  Class<T> getEntityClazz(){return t;};
	
	public  Class<U> getCriteriaClazz(){return u;};

}
