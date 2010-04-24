/*
 * Created on 21/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;


public class TemplatePlaceTag extends TemplatePlace {

	private TemplatePlaceTag() {
	}

	public static TemplatePlaceTag getInstance(String tagName, String idName) {
		TemplatePlaceTag place = new TemplatePlaceTag();
		place.type = TemplatePlace.PH_TAG;
		place.tagName = tagName;
		place.idName = idName;
		return place;
	}

}
