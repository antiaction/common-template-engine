/*
 * Created on 21/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.html.HtmlParser;

public class TemplatePlaceHolder extends TemplatePlaceBase {

	private TemplatePlaceHolder() {
	}

	public static TemplatePlaceHolder getInstance(String idName) {
		TemplatePlaceHolder place = new TemplatePlaceHolder();
		place.type = TemplatePlaceBase.PH_PLACEHOLDER;
		place.tagName = "placeholder";
		place.idName = idName;
		return place;
	}

	public void setText(String text) {
		if ( templatePart != null ) {
			((TemplatePartPlaceHolder)templatePart).setText( text );
		}

		try {
			ByteArrayInputStream is = new ByteArrayInputStream( text.getBytes() );

			// Parse html into a List of html items.
			HtmlParser htmlParser = new HtmlParser();
			List<HtmlItem> html_items = htmlParser.parse( is );

			// Validate html. 
			HtmlValidator.validate( html_items );
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
