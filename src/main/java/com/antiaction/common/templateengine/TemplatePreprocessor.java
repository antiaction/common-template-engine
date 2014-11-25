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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.antiaction.common.html.HtmlItem;

public class TemplatePreprocessor {

    /** Logging mechanism. */
	private static Logger logger = Logger.getLogger( TemplatePreprocessor.class.getName() );

	/** Backing template. */
	protected Template template;

	protected Map<String, Set<String>> tagIdNameMap;

	protected String character_encoding;

	protected List<TemplatePartBase> templatePartsList = new ArrayList<TemplatePartBase>();

	protected Set<String> i18nTextIdSet = new HashSet<String>();

	protected Set<String> placeHolderIdSet = new HashSet<String>();

	/** Last time this template was changed. */
	protected long last_processed;

	/**
	 * Prevent external construction.
	 */
	protected TemplatePreprocessor() {
	}

	public static TemplatePreprocessor getInstance(Template template, Map<String, Set<String>> tagIdNameMap, String character_encoding) throws IOException {
		TemplatePreprocessor tplPp = new TemplatePreprocessor();
		tplPp.template = template;
		if ( tagIdNameMap == null ) {
			tagIdNameMap = new HashMap<String, Set<String>>();
		}
		tplPp.tagIdNameMap = tagIdNameMap;
		tplPp.character_encoding = character_encoding;
		tplPp.reload();
		return tplPp;
	}

	public Template getTemplate() {
		return template;
	}

	public synchronized boolean check_reload() throws IOException {
		boolean reloaded = template.check_reload();
		if ( reloaded || template.last_processed > last_processed ) {
			reload();
		}
		return reloaded;
	}

	protected void reload() throws IOException {
		List<HtmlItem> html_items_work = template.getHtmlItems();
		templatePartsList.clear();
		i18nTextIdSet.clear();
		placeHolderIdSet.clear();
		last_processed = System.currentTimeMillis();

		HtmlItem htmlItem;
		String tagName;
		String id;
		String name;
		String text_id;
		TemplatePartBase templatePart;
		Set<String> idNameSet;

		Boolean f;
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.reset();

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

					templatePart = null;
					f = false;

					if ( "i18n".compareTo( tagName ) == 0 ) {
						text_id = htmlItem.getAttribute( "text_id" );
						if ( text_id != null && text_id.length() > 0 ) {
							templatePart = TemplatePartBase.getTemplatePartI18N( (HtmlItem)htmlItem.clone() );
 							i18nTextIdSet.add( text_id );
						}
						f = true;
					}
					else if ( "placeholder".compareTo( tagName ) == 0 ) {
						if ( id != null && id.length() > 0 ) {
							templatePart = TemplatePartBase.getTemplatePartPlaceHolder( (HtmlItem)htmlItem.clone() );
							placeHolderIdSet.add( id );
						}
						f = true;
					}
					if ( id != null || name != null ) {
						idNameSet = tagIdNameMap.get( tagName );
						if (idNameSet != null) {
							if ( (idNameSet.size() == 0) || ( id != null && idNameSet.contains( id )) || (name != null && idNameSet.contains( name )) ) {
								templatePart = TemplatePartBase.getTemplatePartTag( (HtmlItem)htmlItem.clone() );
								f = true;
							}
						}
					}
					if ( f ) {
						if ( out.size() > 0 ) {
							templatePartsList.add( TemplatePartBase.getTemplatePartStatic( out.toByteArray() ) );
							out.reset();
						}
						if ( templatePart != null ) {
							templatePartsList.add( templatePart );
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
				templatePartsList.add( TemplatePartBase.getTemplatePartStatic( out.toByteArray() ) );
				out.reset();
			}
		}
		else {
		}
	}

	public synchronized TemplateParts filterTemplate(List<TemplatePlaceBase> placeHolders) throws UnsupportedEncodingException, IOException {
		check_reload();
		TemplateParts templateParts = new TemplateParts();
		TemplatePartBase templatePartBase;
		TemplatePartBase templatePartBaseNew;
		HtmlItem htmlItem;
		String tagName;
		String id;
		String name;
		int j;
		boolean b;
		TemplatePlaceBase templatePlace;
		for ( int i=0; i<templatePartsList.size(); ++i ) {
			templatePartBase = templatePartsList.get( i );
			switch ( templatePartBase.type ) {
			case TemplatePartBase.TP_STATIC:
				templateParts.parts.add( templatePartBase );
				break;
			case TemplatePartBase.TP_I18N:
				templatePartBaseNew = (TemplatePartBase)templatePartBase.clone();
				templateParts.parts.add( templatePartBaseNew );
				templateParts.i18nList.add( (TemplatePartI18N)templatePartBaseNew );
				break;
			case TemplatePartBase.TP_PLACEHOLDER:
				templatePartBaseNew = (TemplatePartBase)templatePartBase.clone();
				templateParts.parts.add( templatePartBaseNew );
				htmlItem = templatePartBaseNew.htmlItem;
				tagName = htmlItem.getTagname().toLowerCase();
				id = htmlItem.getAttribute( "id" );
				name = htmlItem.getAttribute( "name" );
				j = 0;
				b = true;
				while ( b ) {
					if ( j < placeHolders.size() ) {
						templatePlace = placeHolders.get( j );
						if ( templatePlace.type == TemplatePlaceBase.PH_PLACEHOLDER ) {
							if ( "placeholder".compareTo( tagName ) == 0 ) {
								if ( ( id != null && templatePlace.idName.compareTo( id ) == 0) || (name != null && templatePlace.idName.compareTo( name ) == 0 ) ) {
									templatePlace.templatePart = templatePartBaseNew;
									templatePlace.htmlItem = templatePartBaseNew.htmlItem;
									b = false;
								}
							}
						}
					}
					else {
						b = false;
					}
					++j;
				}
				break;
			case TemplatePartBase.TP_TAG:
				templatePartBaseNew = (TemplatePartBase)templatePartBase.clone();
				templateParts.parts.add( templatePartBaseNew );
				htmlItem = templatePartBaseNew.htmlItem;
				tagName = htmlItem.getTagname().toLowerCase();
				id = htmlItem.getAttribute( "id" );
				name = htmlItem.getAttribute( "name" );
				j = 0;
				b = true;
				while ( b ) {
					if ( j < placeHolders.size() ) {
						templatePlace = placeHolders.get( j );
						if ( templatePlace.type == TemplatePlaceBase.PH_TAG ) {
							if ( templatePlace.tagName.compareTo( tagName ) == 0 ) {
								if ( ( id != null && templatePlace.idName.compareTo( id ) == 0) || (name != null && templatePlace.idName.compareTo( name ) == 0 ) ) {
									templatePlace.templatePart = templatePartBaseNew;
									templatePlace.htmlItem = templatePartBaseNew.htmlItem;
									b = false;
								}
							}
						}
					}
					else {
						b = false;
					}
					++j;
				}
				break;
			}
		}
		return templateParts;
	}

}
