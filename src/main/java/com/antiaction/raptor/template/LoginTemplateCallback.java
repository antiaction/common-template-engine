/*
 * Created on 16/07/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

public interface LoginTemplateCallback<UserType> {

	public UserType validateUserCookie(String token);

	public UserType validateUserCredentials(String id, String password);

	public String getTranslated(String text_idstring);

}
