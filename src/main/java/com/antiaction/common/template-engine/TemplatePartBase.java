/*
 * Created on 23/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import com.antiaction.common.html.HtmlItem;

public abstract class TemplatePartBase {

	public HtmlItem htmlItem = null;

	public abstract void setText(String text);

	public abstract void setBytes(byte[] bytes);

	public abstract String getText();

	public abstract byte[] getBytes();

	public static TemplatePartStatic getTemplatePartStatic(String text) {
		return TemplatePartStatic.getInstance( text );
	}

	public static TemplatePartStatic getTemplatePartStatic(byte[] bytes) {
		return TemplatePartStatic.getInstance( bytes );
	}

	public static TemplatePartI18N getTemplatePartI18N(HtmlItem htmlItem) {
		return TemplatePartI18N.getInstance( htmlItem );
	}

	public static TemplatePartPlaceHolder getTemplatePartPlaceHolder(HtmlItem htmlItem) {
		return TemplatePartPlaceHolder.getInstance( htmlItem );
	}

	public static TemplatePartTag getTemplatePartTag(HtmlItem htmlItem) {
		return TemplatePartTag.getInstance( htmlItem );
	}

}
