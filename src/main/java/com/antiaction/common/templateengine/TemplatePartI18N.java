/*
 * Created on 03/05/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.UnsupportedEncodingException;

import com.antiaction.common.html.HtmlItem;

public class TemplatePartI18N extends TemplatePartBase {

	private String text_id = "";

	private String text = "";

	private byte[] bytes = "".getBytes();

	private TemplatePartI18N() {
	}

	public static TemplatePartI18N getInstance(HtmlItem htmlItem) {
		TemplatePartI18N part = new TemplatePartI18N();
		part.htmlItem = htmlItem;
		part.text_id = htmlItem.getAttribute( "text_id" );
		if ( part.text_id != null ) {
			// TODO language translation
			part.text = part.text_id;
			try {
				part.bytes = part.text.getBytes( "utf-8" );
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return part;
	}

	@Override
	public void setText(String text) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setBytes(byte[] bytes) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public byte[] getBytes() {
		return bytes;
	}

}