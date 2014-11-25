/*
 * Created on 03/05/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.antiaction.common.html.HtmlItem;

public class TemplatePartI18N extends TemplatePartBase {

    /** Logging mechanism. */
	private static Logger logger = Logger.getLogger( TemplatePartI18N.class.getName() );

	private String text_id = "";

	private String text = "";

	private byte[] bytes = "".getBytes();

	private TemplatePartI18N() {
		type = TP_I18N;
	}

	public static TemplatePartI18N getInstance(HtmlItem htmlItem) {
		TemplatePartI18N part = new TemplatePartI18N();
		part.htmlItem = htmlItem;
		part.text_id = htmlItem.getAttribute( "text_id" );
		if ( part.text_id != null ) {
			// TODO language translation
			part.text = part.text_id;
			try {
				part.bytes = part.text.getBytes( "UTF-8" );
			}
			catch (UnsupportedEncodingException e) {
				logger.log( Level.SEVERE, e.toString(), e );
			}
		}
		return part;
	}

	@Override
	public Object clone() {
		TemplatePartI18N part = new TemplatePartI18N();
		part.htmlItem = (HtmlItem)htmlItem.clone();
		part.text_id = text_id;
		part.text = text;
		part.bytes = bytes;
		return part;
	}

	@Override
	public String getText() {
		return text;
	}

	@Override
	public void setText(String text) {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getBytes() {
		return bytes;
	}

	@Override
	public void setBytes(byte[] bytes) {
		throw new UnsupportedOperationException();
	}

}
