/*
 * Created on 23/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemplatePartStatic extends TemplatePartBase {

    /** Logging mechanism. */
	private static Logger logger = Logger.getLogger( TemplatePartStatic.class.getName() );

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
		part.text = null;
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
				logger.log( Level.SEVERE, e.toString(), e );
			}
		}
		return text;
	}

	@Override
	public void setText(String text) {
		throw new UnsupportedOperationException();
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
		throw new UnsupportedOperationException();
	}

}
