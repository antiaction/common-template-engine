/*
 * Created on 06/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.antiaction.common.html.HtmlItem;

public class TemplatePreprocessor {

    /** Logging mechanism. */
	private static Logger logger = Logger.getLogger( TemplatePreprocessor.class.getName() );

	/** Backing template. */
	protected Template template;

	protected Set<String> variables;

	public TemplatePreprocessor(Template template, Set<String> variables) {
		this.template = template;
		this.variables = variables;
	}

	public synchronized boolean check_reload() throws UnsupportedEncodingException, IOException {
		boolean reloaded = template.check_reload();
		if ( reloaded ) {
			reload();
		}
		return reloaded;
	}

	public void reload() throws UnsupportedEncodingException, IOException {
		List<HtmlItem> html_items_work = template.getHtmlItems();

		TemplateParts templateParts = new TemplateParts();

		List<TemplatePlaceBase> placeHolders = null;
		String character_encoding = "UTF-8";

		String tagName;
		String id;
		String name;
		String text_id;

		Boolean b;
		Boolean f;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.reset();

		HtmlItem htmlItem;
		TemplatePlaceBase templatePlace;
		TemplatePartBase templatePart;

		if ( html_items_work != null ) {
			int i = 0;
			while (  i<html_items_work.size() ) {
				htmlItem = html_items_work.get( i );
				switch ( htmlItem.getType() ) {
				case HtmlItem.T_DIRECTIVE:
					logger.log( Level.SEVERE, "Invalid @directive: " + htmlItem.getTagname().toLowerCase() );
					break;
				case HtmlItem.T_PROCESSING:
				case HtmlItem.T_EXCLAMATION:
				case HtmlItem.T_COMMENT:
				case HtmlItem.T_ENDTAG:
				case HtmlItem.T_TEXT:
					out.write( htmlItem.getText().getBytes( character_encoding ) );
					break;
				case HtmlItem.T_TAG:
					tagName = htmlItem.getTagname().toLowerCase();
					id = htmlItem.getAttribute( "id" );
					name = htmlItem.getAttribute( "name" );

					templatePlace = null;
					templatePart = null;
					f = false;

					if ( "i18n".compareTo( tagName ) == 0 ) {
						text_id = htmlItem.getAttribute( "text_id" );
						if ( text_id != null && text_id.length() > 0 ) {
							templatePart = TemplatePartBase.getTemplatePartI18N( (HtmlItem)htmlItem.clone() );
							templateParts.i18nList.add( (TemplatePartI18N)templatePart );
						}
						f = true;
					}
					else if ( "placeholder".compareTo( tagName ) == 0 ) {
						if ( id != null && id.length() > 0 ) {
							templatePart = TemplatePartBase.getTemplatePartPlaceHolder( (HtmlItem)htmlItem.clone() );
							templateParts.placeHoldersMap.put( id, (TemplatePartPlaceHolder)templatePart );
						}
						f = true;
					}
					if ( id != null || name != null ) {
						int j = 0;
						b = true;
						while ( b ) {
							if ( j < placeHolders.size() ) {
								templatePlace = placeHolders.get( j );
								switch ( templatePlace.type ) {
								case TemplatePlaceBase.PH_TAG:
									if ( templatePlace.tagName.compareTo( tagName ) == 0 ) {
										if ( ( id != null && templatePlace.idName.compareTo( id ) == 0) || (name != null && templatePlace.idName.compareTo( name ) == 0 ) ) {
											templatePart = TemplatePartBase.getTemplatePartTag( (HtmlItem)htmlItem.clone() );
											templatePlace.templatePart = templatePart;
											templatePlace.htmlItem = templatePart.htmlItem;
											b = false;
											f = true;
										}
									}
									break;
								case TemplatePlaceBase.PH_PLACEHOLDER:
									if ( "placeholder".compareTo( tagName ) == 0 ) {
										if ( ( id != null && templatePlace.idName.compareTo( id ) == 0) || (name != null && templatePlace.idName.compareTo( name ) == 0 ) ) {
											templatePlace.templatePart = templatePart;
											templatePlace.htmlItem = templatePart.htmlItem;
											b = false;
											f = true;
										}
									}
									break;
								}
							}
							else {
								b = false;
							}
							++j;
						}
					}
					if ( f ) {
						if ( out.size() > 0 ) {
							templateParts.parts.add( TemplatePartBase.getTemplatePartStatic( out.toByteArray() ) );
							out.reset();
						}
						if ( templatePart != null ) {
							templateParts.parts.add( templatePart );
						}
					}
					else {
						out.write( htmlItem.getText().getBytes( character_encoding ) );
					}
					break;
				}
				++i;
			}
			if ( out.size() > 0 ) {
				templateParts.parts.add( TemplatePartBase.getTemplatePartStatic( out.toByteArray() ) );
				out.reset();
			}
		}
		else {
		}
	}

}
