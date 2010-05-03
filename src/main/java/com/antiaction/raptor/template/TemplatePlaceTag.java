/*
 * Created on 21/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import java.util.Map;

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

	public Map<String, String> getAttributes() {
		Map<String, String> attributes = null;
		if ( htmlItem != null ) {
			attributes = htmlItem.getAttributes();
		}
		return attributes;
	}

	public String getAttribute(String name) {
		String value = null;
		if ( htmlItem != null ) {
			value = htmlItem.getAttribute( name );
		}
		return value;
	}

	public Object setAttribute(String name, String value) {
		Object old = null;
		if ( htmlItem != null ) {
			old = htmlItem.setAttribute( name, value );
		}
		return old;
	}

	public Object removeAttribute(String name) {
		Object old = null;
		if ( htmlItem != null ) {
			old = htmlItem.removeAttribute( name );
		}
		return old;
	}

}
