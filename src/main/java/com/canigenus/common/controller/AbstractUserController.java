package com.canigenus.common.controller;

import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Event;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpSession;

import com.canigenus.common.constants.GenericConstant;
import com.canigenus.common.event.LoginSuccess;
import com.canigenus.common.model.IPassword;
import com.canigenus.common.model.IUser;
import com.canigenus.common.service.AbstractUserService;
import com.canigenus.common.util.JsfUtil;
import com.canigenus.common.util.PasswordUtil;

public abstract class AbstractUserController<T extends IUser<?>, U extends IPassword> {

	private T currentLogin;
	
	private T currentRegister;

	private U password;
	
	private String loginPassword;

	private boolean linkValid;
	
	@Inject
	@LoginSuccess
	private Event<T> loginSuccess;
	
	public abstract  AbstractMailBean getMailBean();

	@PostConstruct
	public void init() {
		currentLogin = instantiateEntity();
		currentRegister=instantiateEntity();
		password = instantiatePassword();
	}

	public void clear() {
		currentLogin = instantiateEntity();
		currentRegister=instantiateEntity();
		password = instantiatePassword();
	}

	public String doLogin() {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		HttpSession session = (HttpSession) externalContext.getSession(false);
		session.removeAttribute(GenericConstant.USERINFO);
		T userDTO = getService().getUserWithPassword(getClassType(),
				currentLogin.getUserId());
		if (userDTO == null) {
			JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle")
					.getString("UserIdDoesntExist"));
			clear();
			return null;
		}
		//Collections.sort(userDTO.getPasswords());
		IPassword password = userDTO.getPasswords().get(userDTO.getPasswords().size()-1);

		if (userDTO.getStatus().equals(GenericConstant.INACTIVE)) {
			JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle")
					.getString("AccountIsInactiveConfirmYourMailBeforeLogin"));
			return null;
		}
		boolean isSuccess = false;
		try {
			isSuccess = (PasswordUtil.enctyptPassword(
					this.loginPassword, password.getSalt())
					.get(GenericConstant.PASSWORD)).equals(password
					.getPassword());
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isSuccess) {
			userDTO.getPasswords().clear();
			userDTO=getService().getUserWithForSessionLoad(getClassType(), currentLogin.getUserId());
			session.setAttribute(GenericConstant.USERINFO, userDTO);
			clear();
			if(session.getAttribute("fromPage")!=null)
			{
				return session.getAttribute("fromPage").toString();
			}
			loginSuccess.fire(userDTO);
			return "index";
		} else {
			JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle")
					.getString("PasswordDoesntMatch"));
			password = instantiatePassword();
			return null;
		}
	}

	public String save() {
		boolean isUserIdExists = getService().isUserIdExists(getClassType(),
				currentRegister.getUserId());
		if (isUserIdExists) {
			JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle")
					.getString("UserIdAlreadyExists"));
			return null;
		}

		if (!password.getPassword().equals(password.getConfirmPassword())) {
			JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle")
					.getString(
							"PasswordNConfirmPasswordDoesntMatchErrorMessage"));
			return null;
		}

		Map<String, String> map = null;
		try {
			map = PasswordUtil.enctyptPassword(password.getPassword(), null);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		password.setPassword(map.get(GenericConstant.PASSWORD));
		password.setSalt(map.get(GenericConstant.SALT));
		UUID oneTimeKey = UUID.randomUUID();

		if (!currentRegister.getPasswords().contains(password)) {

			currentRegister.getPasswords().add(password);
		}
		password.setOneTimeKey(oneTimeKey.toString());
		currentRegister.getRoles().add(getDefaultRole());

		currentRegister.setStatus("N");
		getService().update(currentRegister);
		currentRegister= instantiateEntity();
		loginPassword="";
		getMailBean().sendMail(getSendEmailFromEmail(), getSendEmailFromName(),currentRegister.getUserId(),
				getRegistrationConfirmationTemplate(),getRegistrationConfirmationEmailSubject(), true);
		JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle")
				.getString(
						"RegistrationSuccessfulNEmailSentForActivationMessage"));
		clear();
		return "registrationSuccess";
	}

	public abstract AbstractUserService<?,?> getService();

	public abstract Class<T> getClassType();

	public abstract Class<U> getPasswordClassType();

	public abstract T instantiateEntity();

	public abstract U instantiatePassword();

	public U getPassword() {
		return password;
	}

	public void setPassword(U password) {
		this.password = password;
	}

	public abstract String getRegistrationConfirmationTemplate();

	public String getRegistrationConfirmationEmailSubject() {
		return ResourceBundle.getBundle("/Bundle").getString(
				"RegistrationConfirmationEmailSubject");
	}

	@SuppressWarnings("unchecked")
	public void sendPasswordChangeMail() {
		UUID oneTimeKey = UUID.randomUUID();
		T userDTO = getService().getUserWithPassword(getClassType(),
				currentLogin.getUserId());

		if (userDTO == null) {
			JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle")
					.getString("UserIdDoesntExist"));
			clear();
			return;
		}
		//Collections.sort(userDTO.getPasswords());
		password = (U) userDTO.getPasswords().get(userDTO.getPasswords().size()-1);
		password.setOneTimeKey(oneTimeKey.toString());
		getService().update(userDTO);
		getMailBean().sendMail(getSendEmailFromEmail(), getSendEmailFromEmail(), currentLogin.getUserId(),
				getChangePasswordEmailSubject(),
				getChangePasswordEmailTemplate(),true);
		JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle")
				.getString("ChangePasswordMailSentSucessfullMessage"));
		password = instantiatePassword();

	}

	public abstract String getChangePasswordEmailTemplate();

	private String getChangePasswordEmailSubject() {
		return ResourceBundle.getBundle("/Bundle").getString(
				"PasswordChangeEmailSubject");
	}

	public void linkValid() {
		linkValid=false;
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		String link = externalContext.getRequestParameterMap().get("link");
		String userId = externalContext.getRequestParameterMap().get("userId");
		String activate = externalContext.getRequestParameterMap().get(
				"activate");

		if (link != null && userId != null && !link.isEmpty()
				&& !userId.isEmpty()) {
			T userDTO = getService().getUserWithPassword(getClassType(),
					userId);
			//Collections.sort(userDTO.getPasswords());
			IPassword passwordable = userDTO.getPasswords().get(userDTO.getPasswords().size()-1);
			if (!link.equals(passwordable.getOneTimeKey())) {
				linkValid = false;
			} else {
				linkValid = true;
				if (activate != null && activate.equals("true")) {
					userDTO.setStatus("Y");
					passwordable.setOneTimeKey(null);
					getService().update(userDTO);
					JsfUtil.addSuccessMessage(ResourceBundle.getBundle(
							"/Bundle").getString(
							"AccountActivationThruMailMessage"));
				} else {
					currentLogin = instantiateEntity();
					password = instantiatePassword();
					currentLogin.setUserId(userId);
				}
			}
		}

	}

	public String resetPassword() {
		T userDTO = getService().getUserWithPassword(getClassType(),
				currentLogin.getUserId());
		if (!password.getPassword().equals(password.getConfirmPassword())) {
			JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle")
					.getString(
							"PasswordNConfirmPasswordDoesntMatchErrorMessage"));
			return null;
		}

		Map<String, String> map = null;
		try {
			map = PasswordUtil.enctyptPassword(password.getPassword(), null);
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		password.setPassword(map.get(GenericConstant.PASSWORD));
		password.setSalt(map.get(GenericConstant.SALT));

		userDTO.getPasswords().add(password);
		getService().update(userDTO);
		clear();
		JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle")
				.getString("PasswordChangeSuccessfullyMessage"));
		password=instantiatePassword();
		return "login";

	}

	public void changePassword() {
		T userDTO = getService().getUserWithPassword(getClassType(),
				currentLogin.getUserId());
		if (!password.getPassword().equals(password.getConfirmPassword())) {
			JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle")
					.getString(
							"PasswordNConfirmPasswordDoesntMatchErrorMessage"));
			return;
		}
		//Collections.sort(userDTO.getPasswords());
		IPassword passwordable = userDTO.getPasswords().get(userDTO.getPasswords().size()-1);
		try {
			if ((PasswordUtil.enctyptPassword(password.getOldPassword(),
					passwordable.getSalt()).get(GenericConstant.PASSWORD))
					.equals(passwordable.getPassword())) {
				Map<String, String> map = null;
				try {
					map = PasswordUtil.enctyptPassword(password.getPassword(),
							null);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				password.setPassword(map.get(GenericConstant.PASSWORD));
				password.setSalt(map.get(GenericConstant.SALT));
				userDTO.getPasswords().add(password);

			} else {
				JsfUtil.addErrorMessage(ResourceBundle.getBundle("/Bundle")
						.getString("PasswordDoesntMatch"));
				return;
			}
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Some Problem");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new RuntimeException("Some Problem");
		}
		getService().update(userDTO);
		JsfUtil.addSuccessMessage(ResourceBundle.getBundle("/Bundle")
				.getString("PasswordChangeSuccessfullyMessage"));
		clear();

	}

	public boolean isLinkValid() {
		return linkValid;
	}

	public void setLinkValid(boolean linkValid) {
		this.linkValid = linkValid;
	}

	public abstract String getSendEmailFromEmail();
	public abstract String getSendEmailFromName();

	public abstract Enum<?> getDefaultRole();

	public boolean isPasswordChangeRequired() {
		return false;
	}

	public boolean isAutoGeneratePassword() {
		return false;
	}

	public T getCurrentLogin() {
		return currentLogin;
	}

	public void setCurrentLogin(T currentLogin) {
		this.currentLogin = currentLogin;
	}

	public T getCurrentRegister() {
		return currentRegister;
	}

	public void setCurrentRegister(T currentRegister) {
		this.currentRegister = currentRegister;
	}

	public String getLoginPassword() {
		return loginPassword;
	}

	public void setLoginPassword(String loginPassword) {
		this.loginPassword = loginPassword;
	}

	
	
}
