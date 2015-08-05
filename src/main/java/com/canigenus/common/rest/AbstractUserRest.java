package com.canigenus.common.rest;
//http://www.developerscrappad.com/1814/java/java-ee/rest-jax-rs/java-ee-7-jax-rs-2-0-simple-rest-api-authentication-authorization-with-custom-http-header/
//http://jasonwatmore.com/post/2015/03/10/AngularJS-User-Registration-and-Login-Example.aspx
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.security.auth.login.LoginException;
import javax.servlet.http.HttpSession;

import com.canigenus.common.constants.GenericConstant;
import com.canigenus.common.controller.AbstractMailBean;
import com.canigenus.common.model.IPassword;
import com.canigenus.common.model.IUser;
import com.canigenus.common.service.AbstractUserService;
import com.canigenus.common.util.JsfUtil;
import com.canigenus.common.util.PasswordUtil;

public abstract class AbstractUserRest<T extends IUser<?>, U extends IPassword> {

	private T current;

	private U password;

	private boolean linkValid;
	
	public abstract  AbstractMailBean getMailBean();

	@PostConstruct
	public void init() {
		current = instantiateEntity();
		password = instantiatePassword();
		
		  // The usersStorage pretty much represents a user table in the database
        usersStorage.put( "username1", "passwordForUser1" );
        usersStorage.put( "username2", "passwordForUser2" );
        usersStorage.put( "username3", "passwordForUser3" );

        /**
         * Service keys are pre-generated by the system and is given to the
         * authorized client who wants to have access to the REST API. Here,
         * only username1 and username2 is given the REST service access with
         * their respective service keys.
         */
        serviceKeysStorage.put( "f80ebc87-ad5c-4b29-9366-5359768df5a1", "username1" );
        serviceKeysStorage.put( "3b91cab8-926f-49b6-ba00-920bcf934c2a", "username2" );
	}

	public void clear() {
		current = instantiateEntity();
		password = instantiatePassword();
	}

	public String doLogin() {
		ExternalContext externalContext = FacesContext.getCurrentInstance()
				.getExternalContext();
		HttpSession session = (HttpSession) externalContext.getSession(false);
		session.removeAttribute(GenericConstant.USERINFO);
		T userDTO = getService().getUserWithPassword(getClassType(),
				current.getUserId());
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
					this.password.getPassword(), password.getSalt())
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
			session.setAttribute(GenericConstant.USERINFO, userDTO);
			clear();
			if(session.getAttribute("fromPage")!=null)
			{
				return session.getAttribute("fromPage").toString();
			}
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
				current.getUserId());
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

		if (!current.getPasswords().contains(password)) {

			current.getPasswords().add(password);
		}
		password.setOneTimeKey(oneTimeKey.toString());
		current.getRoles().add(getDefaultRole());

		current.setStatus("N");
		getService().update(current);
		getMailBean().sendMail(getSendEmailFromEmail(), getSendEmailFromName(),current.getUserId(),
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

	public T getCurrent() {
		return current;
	}

	public void setCurrent(T current) {
		this.current = current;
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
				current.getUserId());

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
		getMailBean().sendMail(getSendEmailFromEmail(), getSendEmailFromEmail(), current.getUserId(),
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
					current = instantiateEntity();
					password = instantiatePassword();
					current.setUserId(userId);
				}
			}
		}

	}

	public String resetPassword() {
		T userDTO = getService().getUserWithPassword(getClassType(),
				current.getUserId());
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
				current.getUserId());
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
	


  

    // A user storage which stores <username, password>
    private final Map<String, String> usersStorage = new HashMap<>();

    // A service key storage which stores <service_key, username>
    private final Map<String, String> serviceKeysStorage = new HashMap<>();

    // An authentication token storage which stores <service_key, auth_token>.
    private final Map<String, String> authorizationTokensStorage = new HashMap<>();


 
    public String login( String serviceKey, String username, String password ) throws LoginException {
        if ( serviceKeysStorage.containsKey( serviceKey ) ) {
            String usernameMatch = serviceKeysStorage.get( serviceKey );

            if ( usernameMatch.equals( username ) && usersStorage.containsKey( username ) ) {
                String passwordMatch = usersStorage.get( username );

                if ( passwordMatch.equals( password ) ) {

                    /**
                     * Once all params are matched, the authToken will be
                     * generated and will be stored in the
                     * authorizationTokensStorage. The authToken will be needed
                     * for every REST API invocation and is only valid within
                     * the login session
                     */
                    String authToken = UUID.randomUUID().toString();
                    authorizationTokensStorage.put( authToken, username );

                    return authToken;
                }
            }
        }

        throw new LoginException( "Don't Come Here Again!" );
    }

    /**
     * The method that pre-validates if the client which invokes the REST API is
     * from a authorized and authenticated source.
     *
     * @param serviceKey The service key
     * @param authToken The authorization token generated after login
     * @return TRUE for acceptance and FALSE for denied.
     */
    public boolean isAuthTokenValid( String serviceKey, String authToken ) {
        if ( isServiceKeyValid( serviceKey ) ) {
            String usernameMatch1 = serviceKeysStorage.get( serviceKey );

            if ( authorizationTokensStorage.containsKey( authToken ) ) {
                String usernameMatch2 = authorizationTokensStorage.get( authToken );

                if ( usernameMatch1.equals( usernameMatch2 ) ) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * This method checks is the service key is valid
     *
     * @param serviceKey
     * @return TRUE if service key matches the pre-generated ones in service key
     * storage. FALSE for otherwise.
     */
    public boolean isServiceKeyValid( String serviceKey ) {
        return serviceKeysStorage.containsKey( serviceKey );
    }

    public void logout( String serviceKey, String authToken ) throws GeneralSecurityException {
        if ( serviceKeysStorage.containsKey( serviceKey ) ) {
            String usernameMatch1 = serviceKeysStorage.get( serviceKey );

            if ( authorizationTokensStorage.containsKey( authToken ) ) {
                String usernameMatch2 = authorizationTokensStorage.get( authToken );

                if ( usernameMatch1.equals( usernameMatch2 ) ) {

                    /**
                     * When a client logs out, the authentication token will be
                     * remove and will be made invalid.
                     */
                    authorizationTokensStorage.remove( authToken );
                    return;
                }
            }
        }

        throw new GeneralSecurityException( "Invalid service key and authorization token match." );
    }


}
