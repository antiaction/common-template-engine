/*
 * Created on 03/05/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import com.antiaction.common.html.HTMLItem;

public class TemplatePartI18N extends TemplatePart {

	public byte[] text = "".getBytes();

	private TemplatePartI18N() {
	}

	public static TemplatePartI18N getInstance(HTMLItem htmlItem) {
		TemplatePartI18N part = new TemplatePartI18N();
		part.htmlItem = htmlItem;
		return part;
	}

	@Override
	public byte[] getBytes() {
		return text;
	}

}
