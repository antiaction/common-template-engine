/*
 * Created on 21/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import com.antiaction.common.html.HTMLItem;

public abstract class TemplatePlace {

	public static final int PH_TAG = 1;

	public static final int PH_PLACEHOLDER = 2;

	public HTMLItem htmlItem = null;

	public int type = 0;

	public String tagName = null;

	public String idName = null;

	public static TemplatePlaceTag getTemplateTagPlace(String tagName, String idName) {
		return TemplatePlaceTag.getInstance( tagName, idName );
	}

	public static TemplatePlaceHolder getTemplatePlaceHolder(String idName) {
		return TemplatePlaceHolder.getInstance( idName );
	}

}
