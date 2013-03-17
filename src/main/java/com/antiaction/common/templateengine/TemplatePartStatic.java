/*
 * Created on 23/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.UnsupportedEncodingException;

public class TemplatePartStatic extends TemplatePartBase {

	private String text = "";

	private byte[] bytes = "".getBytes();

	private TemplatePartStatic() {
	}

	public static TemplatePartStatic getInstance(String text) {
		TemplatePartStatic part = new TemplatePartStatic();
		part.text = text;
		part.bytes = null;
		return part;
	}

	public static TemplatePartStatic getInstance(byte[] bytes) {
		TemplatePartStatic part = new TemplatePartStatic();
		part.bytes = bytes;
		part.text = null;
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
