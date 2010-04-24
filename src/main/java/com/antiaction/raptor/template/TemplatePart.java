/*
 * Created on 23/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import com.antiaction.common.html.HTMLItem;

public abstract class TemplatePart {

	public abstract byte[] getBytes();

	public static TemplatePartStatic getTemplatePartStatic(byte[] text) {
		return TemplatePartStatic.getInstance( text );
	}

	public static TemplatePartTag getTemplatePartTag(HTMLItem htmlItem, TemplatePlace templatePlace) {
		return TemplatePartTag.getInstance( htmlItem, templatePlace );
	}

}
