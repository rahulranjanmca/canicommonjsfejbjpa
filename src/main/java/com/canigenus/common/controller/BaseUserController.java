package com.canigenus.common.controller;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.canigenus.common.constants.GenericConstant;
import com.canigenus.common.model.IUser;

public abstract class BaseUserController {

	public  static String getUserId() {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		HttpSession session = (HttpSession) externalContext.getSession(false);
		IUser userDTO;
		if (session != null
				&& (userDTO = (IUser) session
						.getAttribute(GenericConstant.USERINFO)) != null) {
			return userDTO.getUserId();
		} else {
			return null;
		}
	}
	
	public  static IUser getUser() {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		HttpSession session = (HttpSession) externalContext.getSession(false);
		IUser userDTO;
		if (session != null
				&& (userDTO = (IUser) session
						.getAttribute(GenericConstant.USERINFO)) != null) {
			return userDTO;
		} else {
			return null;
		}
	}
    
	public static boolean isLoggedIn() {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		HttpSession session = (HttpSession) externalContext.getSession(false);
		if (session != null
				&& session.getAttribute(GenericConstant.USERINFO) != null) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean hasRole(String role) {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		HttpSession session = (HttpSession) externalContext.getSession(false);
		IUser userDTO;
		if (session != null
				&& (userDTO = (IUser) session
						.getAttribute(GenericConstant.USERINFO)) != null) {

			for (Enum<?> role1 : userDTO.getRoles())

			{
				if (role1.name().equals(role)) {
					return true;
				}
			}

		} else {
			return false;
		}
		return false;
	}

	public String logout() {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		HttpSession session = (HttpSession) externalContext.getSession(false);
		session.invalidate();
		return null;
	}
	
	public String goToLogin(){
		FacesContext facesContext=FacesContext.getCurrentInstance();
		String viewId = facesContext.getViewRoot().getViewId();
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		HttpSession session = (HttpSession) externalContext.getSession(false);
		session.setAttribute("fromPage", viewId);
		return "login";
	}

}
