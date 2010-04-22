/*
 * Created on 21/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.frontend;

public abstract class TemplatePlace {

	public static TemplateTagPlace getTemplateTagPlace(String tagName, String idName) {
		return TemplateTagPlace.getInstance( tagName, idName );
	}

	public static TemplatePlaceHolder getTemplatePlaceHolder(String idName) {
		return TemplatePlaceHolder.getInstance( idName );
	}

}
