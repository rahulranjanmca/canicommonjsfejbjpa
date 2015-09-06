package com.canigenus.common.util;

import java.text.MessageFormat;
import java.util.List;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;

import com.canigenus.common.model.Identifiable;
import com.canigenus.common.model.Message;
import com.canigenus.common.model.Message.Severity;
import com.canigenus.common.model.Response;


public class JsfUtil {
	
	/*@SuppressWarnings({"unchecked", "el-syntax"})
	public static <T> T findBean(String beanName) {
	    FacesContext context = FacesContext.getCurrentInstance();
	    return (T) context.getApplication().evaluateExpressionGet(context, "#{" + beanName + "}", Object.class);
	}*/

    public static SelectItem[] getSelectItems(List<?> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem(null, "---");
            i++;
        }
        for (Object x : entities) {
            items[i++] = new SelectItem(x, x.toString());
        }
        return items;
    }
    
    public static SelectItem[] getSelectItemsWithId(List<? extends Identifiable<?>> entities, boolean selectOne) {
        int size = selectOne ? entities.size() + 1 : entities.size();
        SelectItem[] items = new SelectItem[size];
        int i = 0;
        if (selectOne) {
            items[0] = new SelectItem(null, "---");
            i++;
        }
        for (Identifiable<?> x : entities) {
            items[i++] = new SelectItem(x.getId(), x.toString());
        }
        return items;
    }

    public static void addErrorMessage(Exception ex, String defaultMsg) {
        String msg = ex.getLocalizedMessage();
        if (msg != null && msg.length() > 0) {
            addErrorMessage(msg);
        } else {
            addErrorMessage(defaultMsg);
        }
    }

    public static void addErrorMessages(List<String> messages) {
        for (String message : messages) {
            addErrorMessage(message);
        }
    }

    public static void addErrorMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, msg);
        FacesContext.getCurrentInstance().addMessage(null, facesMsg);
        FacesContext.getCurrentInstance().validationFailed();
    }
    
    public static void addMessage(String msg, String component, javax.faces.application.FacesMessage.Severity severity) {
        FacesMessage facesMsg = new FacesMessage(severity, msg, msg);
        FacesContext.getCurrentInstance().addMessage(component, facesMsg);
        if(severity==FacesMessage.SEVERITY_ERROR)
        FacesContext.getCurrentInstance().validationFailed();
    }

    public static void addSuccessMessage(String msg) {
        FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, msg, msg);
        FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
    }
    
    public static void addMessage(Response<?> response) {

    	ResourceBundle resourceBundle=	ResourceBundle.getBundle("/Bundle");
    	for(Message message:response.getMessages())
    	{
    		addMessage(resourceBundle, message, null);
    		
    	}
        
    }

	private static void addMessage(ResourceBundle resourceBundle,
			Message message, String component) {
		String stringmsg=resourceBundle.containsKey(message.getCode())?resourceBundle.getString(message.getCode()):message.getMessage();
		if(message.getParameters()!=null && message.getParameters().length!=0)
		{
			stringmsg= MessageFormat.format(stringmsg,  (Object[])message.getParameters());
		}
		if(message.getSeverity()==Severity.ERROR)
		{
		
			FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR,stringmsg, stringmsg);
		    FacesContext.getCurrentInstance().addMessage(component, facesMsg);
		    FacesContext.getCurrentInstance().validationFailed();
		}
		else if(message.getSeverity()==Severity.WARN) {
			FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, stringmsg,stringmsg);
		    FacesContext.getCurrentInstance().addMessage(component, facesMsg);
		}
		else {
			FacesMessage facesMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, stringmsg, stringmsg);
		    FacesContext.getCurrentInstance().addMessage("successInfo", facesMsg);
		}
	}
	
	public static void addMessage(
			Message message, String component) {
		ResourceBundle resourceBundle=	ResourceBundle.getBundle("/Bundle");
		addMessage(resourceBundle, message,component);
	}

    public static String getRequestParameter(String key) {
        return FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
    }

    public static Object getObjectFromRequestParameter(String requestParameterName, Converter converter, UIComponent component) {
        String theId = JsfUtil.getRequestParameter(requestParameterName);
        return converter.getAsObject(FacesContext.getCurrentInstance(), component, theId);
    }
    
    public static void redirectTo(String path)
    {
    	try{
			ExternalContext externalContext=	FacesContext.getCurrentInstance().getExternalContext();
			
			HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
			String returnString=request.getContextPath()+"/"+path;
			externalContext.redirect(returnString);
			}catch(Exception e)
			{
				e.printStackTrace();
			}
    }
}