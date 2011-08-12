/*
 * Created on 29/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import java.io.UnsupportedEncodingException;

import com.antiaction.common.html.HtmlItem;

public class TemplatePartPlaceHolder extends TemplatePartBase {

	private String text = "";

	private byte[] bytes = "".getBytes();

	private TemplatePartPlaceHolder() {
	}

	public static TemplatePartPlaceHolder getInstance(HtmlItem htmlItem) {
		TemplatePartPlaceHolder part = new TemplatePartPlaceHolder();
		part.htmlItem = htmlItem;
		return part;
	}

	@Override
	public void setText(String text) {
		this.text = text;
		bytes = null;
	}

	@Override
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
		text = null;
	}

	@Override
	public String getText() {
		if ( text == null && bytes != null ) {
			try {
				text = new String( bytes, "utf-8" );
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return text;
	}

	@Override
	public byte[] getBytes() {
		if ( bytes == null && text != null ) {
			try {
				bytes = text.getBytes( "utf-8" );
			}
			catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return bytes;
	}

}
