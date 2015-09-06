package com.canigenus.common.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import com.canigenus.common.model.Identifiable;

@Named
@ApplicationScoped
public class JavaUtil {
	
	
	public static String convertToCommaSeparatedString(List<?> objects){
		String str="";
		for(Object object:objects)
		{
			if(str.equals(""))
			{
				str=str+object.toString();
			}
			else{
				str=str+","+object.toString();
			}
		}
		return str;
	}
	
	public static String convertNumberToAlphbets(int i){
	 return	Character.valueOf((char)(65+i)).toString();
	}
	public static List<String>  convertCommaSeparatedStringToList(String str){
		if(str==null)
		{
			return new ArrayList<>();
		}
		return Arrays.asList(str.split(","));
	}


	// return deleted item
	public static <T extends Identifiable<?>> Set<T> getDeletedItem(
			Set<T> existingSet, Set<T> currentSet) {

		Set<T> identifiable = new HashSet<T>();

		for (Iterator<T> i = existingSet.iterator(); i.hasNext();) {
			T identifiable2 = i.next();
			if (!currentSet.contains(identifiable2)) {
				identifiable.add(identifiable2);
			}
		}

		return identifiable;

		/*
		 * for (String screenId : role.getScreenIds()) { boolean
		 * screenPresentInDB = false; for (Screen screen3 :
		 * existingRole.getScreens()) { if
		 * (screen3.getName().getName().equals(screenId)) { screenPresentInDB =
		 * true; } } if (!screenPresentInDB) { Screen screen = new Screen();
		 * screen.setName(new ScreenName(screenId));
		 * existingRole.getScreens().add(screen); } } //
		 * validator.validate(user, result); ModelAndView mv = new
		 * ModelAndView("AssignScreenToRoles"); if (!result.hasErrors()) {
		 * roleService.updateRole(existingRole); role = new Role(); //
		 * user.setId(UUID.randomUUID().toString()); mv.addObject("role", role);
		 * } refreshUserAttachRole(mv.getModelMap());
		 */
	}
	

	public static boolean isBlank(String o) {
		if (o == null || o.isEmpty()) {
			return true;
		}
		return false;
	}
	
	public static String replacePlaceHolders(String o, Map<String,String> keyValues) {
	  for(Map.Entry<String, String> entry:keyValues.entrySet())
	  {
		  o=o.replace(entry.getKey(), entry.getValue());
	  }
	  return o;
	}
	
	
	
	public static void main(String[] args) {
		System.out.println(convertNumberToAlphbets(0));
	}
}
