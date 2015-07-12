/*
 * Created on 29/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.antiaction.common.html.HtmlItem;

public class TemplatePartPlaceHolder extends TemplatePartBase {

    /** Logging mechanism. */
	private static Logger logger = Logger.getLogger( TemplatePartPlaceHolder.class.getName() );

	private String text = "";

	private byte[] bytes = "".getBytes();

	private TemplatePartPlaceHolder() {
		type = TP_PLACEHOLDER;
	}

	public static TemplatePartPlaceHolder getInstance(HtmlItem htmlItem) {
		TemplatePartPlaceHolder part = new TemplatePartPlaceHolder();
		part.htmlItem = htmlItem;
		part.id = htmlItem.getAttribute( "id" );
		return part;
	}

	@Override
	public Object clone() {
		TemplatePartPlaceHolder part = new TemplatePartPlaceHolder();
		part.htmlItem = (HtmlItem)htmlItem.clone();
		part.id = id;
		part.text = text;
		part.bytes = bytes;
		return part;
	}

	@Override
	public String getText() {
		if ( text == null && bytes != null ) {
			try {
				text = new String( bytes, "UTF-8" );
			}
			catch (UnsupportedEncodingException e) {
				logger.log( Level.SEVERE, e.toString(), e) ;
			}
		}
		return text;
	}

	@Override
	public void setText(String text) {
		this.text = text;
		bytes = null;
	}

	@Override
	public byte[] getBytes() {
		if ( bytes == null && text != null ) {
			try {
				bytes = text.getBytes( "UTF-8" );
			}
			catch (UnsupportedEncodingException e) {
				logger.log( Level.SEVERE, e.toString(), e );
			}
		}
		return bytes;
	}

	@Override
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
		text = null;
	}

}
