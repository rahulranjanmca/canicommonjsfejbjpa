package com.canigenus.common.service;

import java.util.List;

import com.canigenus.common.model.IUser;
import com.canigenus.common.model.Identifiable;

@SuppressWarnings("unchecked")
public abstract class AbstractUserService<T extends Identifiable<?>, U extends T> extends GenericServiceImpl<T,U> {

	private static final long serialVersionUID = 5866700361637002188L;

	public <E extends IUser<?>> E getUserWithForSessionLoad(Class<E> clazz,
			String userId) {
		List<E> list = getListByColumnNameAndValue(clazz, "userId", userId);
		if (list.isEmpty()) {
			return null;
		} else {
			list.get(0).getPasswords().size();
			list.get(0).getRoles().size();
			return list.get(0);
		}
	}
	
	public <E extends IUser<?>> E getUserWithPassword(Class<E> clazz,
			String userId) {
		List<E> list = getListByColumnNameAndValue(clazz, "userId", userId);
		if (list.isEmpty()) {
			return null;
		} else {
			list.get(0).getPasswords().size();
			return list.get(0);
		}
	}

	public <E> boolean isUserIdExists(Class<E> clazz, String userId) {
		List<E> userDTOs = getListByColumnNameAndValue(clazz, "userId", userId);
		if (userDTOs != null && !userDTOs.isEmpty()) {
			return true;
		} else
			return false;
	}

}
