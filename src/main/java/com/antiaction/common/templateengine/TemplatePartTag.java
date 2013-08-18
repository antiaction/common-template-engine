/*
 * Created on 23/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.antiaction.common.html.HtmlItem;

public class TemplatePartTag extends TemplatePartBase {

    /** Logging mechanism. */
	private static Logger logger = Logger.getLogger( TemplatePartTag.class.getName() );

	private TemplatePartTag() {
	}

	public static TemplatePartTag getInstance(HtmlItem htmlItem) {
		TemplatePartTag part = new TemplatePartTag();
		part.htmlItem = htmlItem;
		return part;
	}

	@Override
	public String getText() {
		//return htmlItem.getText().getBytes();
		StringBuffer sb = new StringBuffer();
		sb.append( '<' );
		sb.append( htmlItem.getTagname() );

		Map<String, String> attributes = htmlItem.getAttributes();
		Iterator<String> iter = attributes.keySet().iterator();
		while ( iter.hasNext() ) {
			String key = (String)iter.next();
			String val = attributes.get( key );
			sb.append( ' ' );
			sb.append( key );
			if ( val != null ) {
				sb.append( "=\"" );
				sb.append( val );
				sb.append( '"' );
			}
		}

		if ( htmlItem.getClosed() ) {
			sb.append( " /" );
		}
		sb.append( '>' );
		return sb.toString();
	}

	@Override
	public void setText(String text) {
		throw new UnsupportedOperationException();
	}

	@Override
	public byte[] getBytes() {
		byte[] bytes = new byte[ 0 ];
		try {
			bytes = getText().getBytes( "UTF-8" );
		}
		catch (UnsupportedEncodingException e) {
			logger.log( Level.SEVERE, e.toString(), e );
		}
		return bytes;
	}

	@Override
	public void setBytes(byte[] bytes) {
		throw new UnsupportedOperationException();
	}

}
