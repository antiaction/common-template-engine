/*
 * Created on 21/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

public class TemplatePlaceHolder extends TemplatePlace {

	private TemplatePlaceHolder() {
	}

	public static TemplatePlaceHolder getInstance(String idName) {
		TemplatePlaceHolder place = new TemplatePlaceHolder();
		place.type = TemplatePlace.PH_PLACEHOLDER;
		place.tagName = "placeholder";
		place.idName = idName;
		return place;
	}

	public void setText(String text) {
		if ( templatePart != null ) {
			((TemplatePartPlaceHolder)templatePart).text = text.getBytes();
		}
	}

}
