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
import java.util.logging.Level;
import java.util.logging.Logger;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.html.HtmlParser;
import com.antiaction.common.templateengine.storage.TemplateFileStorageManager;

public class TemplatePlaceHolder extends TemplatePlaceBase {

    /** Logging mechanism. */
	private static Logger logger = Logger.getLogger( TemplatePlaceHolder.class.getName() );

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
		if ( text != null ) {
			try {
				ByteArrayInputStream is = new ByteArrayInputStream( text.getBytes() );

				// Parse html into a List of html items.
				HtmlParser htmlParser = new HtmlParser();
				List<HtmlItem> html_items = htmlParser.parse( is );

				// Validate html. 
				HtmlValidator.validate( html_items );
			}
			catch (IOException e) {
				logger.log( Level.SEVERE, e.toString(), e );
			}
		}
	}

}
