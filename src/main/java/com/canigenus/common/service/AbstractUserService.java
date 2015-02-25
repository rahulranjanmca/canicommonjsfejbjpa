package com.canigenus.common.service;

import java.util.List;

import com.canigenus.common.model.IUser;
import com.canigenus.common.model.Identifiable;

@SuppressWarnings("unchecked")
public abstract class AbstractUserService<T extends Identifiable<?>> extends GenericServiceImpl<T> {

	private static final long serialVersionUID = 5866700361637002188L;

	public <E extends IUser> E getUserWithPasswordAndRole(Class<E> clazz,
			String userId) {
		List<E> list = getList(clazz, "userId", userId);
		if (list.isEmpty()) {
			return null;
		} else {
			list.get(0).getPasswords().size();
			list.get(0).getRoles().size();
			return list.get(0);
		}
	}

	public <E> boolean isUserIdExists(Class<E> clazz, String userId) {
		List<E> userDTOs = getList(clazz, "userId", userId);
		if (userDTOs != null && !userDTOs.isEmpty()) {
			return true;
		} else
			return false;
	}

}
