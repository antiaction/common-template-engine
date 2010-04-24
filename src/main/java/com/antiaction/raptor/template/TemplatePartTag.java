/*
 * Created on 23/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import java.util.Iterator;
import java.util.Map;

import com.antiaction.common.html.HTMLItem;

public class TemplatePartTag extends TemplatePart {

	public HTMLItem htmlItem = null;

	public TemplatePlace templatePlace = null;

	private TemplatePartTag() {
	}

	public static TemplatePartTag getInstance(HTMLItem htmlItem, TemplatePlace templatePlace) {
		templatePlace.htmlItem = htmlItem;
		TemplatePartTag part = new TemplatePartTag();
		part.htmlItem = htmlItem;
		part.templatePlace = templatePlace;
		return part;
	}

	@Override
	public byte[] getBytes() {
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
		return sb.toString().getBytes();
	}

}
