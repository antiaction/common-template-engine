/*
 * Created on 29/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import com.antiaction.common.html.HTMLItem;

public class TemplatePartPlaceHolder extends TemplatePart {

	public byte[] text = "".getBytes();

	private TemplatePartPlaceHolder() {
	}

	public static TemplatePartPlaceHolder getInstance(HTMLItem htmlItem) {
		TemplatePartPlaceHolder part = new TemplatePartPlaceHolder();
		part.htmlItem = htmlItem;
		return part;
	}

	@Override
	public byte[] getBytes() {
		return text;
	}

}
