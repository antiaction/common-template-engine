/*
 * Created on 23/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import com.antiaction.common.html.HtmlItem;

public abstract class TemplatePartBase {

	public static final int TP_STATIC = 1;
	public static final int TP_I18N = 2;
	public static final int TP_PLACEHOLDER = 3;
	public static final int TP_TAG = 4;

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

	public int type;

	public HtmlItem htmlItem = null;

	@Override
	public abstract Object clone();

	public abstract String getText();

	public abstract void setText(String text);

	public abstract byte[] getBytes();

	public abstract void setBytes(byte[] bytes);

}
