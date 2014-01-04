/*
 * Created on 19/04/2010
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
import com.antiaction.common.html.HtmlText;
import com.antiaction.common.templateengine.storage.TemplateStorage;

/**
 * Thread-safe as long as only public methods are used.
 *
 * @author Nicholas
 */
public class Template {

    /** Logging mechanism. */
	private static Logger logger = Logger.getLogger( Template.class.getName() );

	/** Template Master. */
	protected TemplateMaster templateMaster = null;

	/*
	 * Source template.
	 */

	/** Template storage filename/id string. */
	protected String templateFileStr = null;

	/** Template storage object. */
	protected TemplateStorage templateStorage = null;

	/** Cached template, modified date. */
	protected long last_modified = -1;

	/** Cached template, file length. */
	protected long last_file_length = -1;

	/** Cached template, raw file converted into separate HTML/XML elements. */
	protected List<HtmlItem> html_items_cached = null;

	/*
	 * Master template
	 */

	/**
	 * Internal class to keep track of <code>Place</code> tags in a master template.
	 */
	public class TemplateMasterPlace {

		public String placeholderName = null;

		public List<HtmlItem> htmlItems = new ArrayList<HtmlItem>();

	}

	/** Last time this template was changed. */
	protected long last_processed;

	/** Master template. */
	protected Template master = null;

	/** <code>List</code> of <code>Place</code> tags in master template. */
	protected List<TemplateMasterPlace> masterPlacesList = new ArrayList<TemplateMasterPlace>();

	/** <code>Map</code> of (id, <code>Place</code>) pairs in master template. */
	protected Map<String, TemplateMasterPlace> masterPlacesMap = new HashMap<String, TemplateMasterPlace>();;

	/*
	 * Processed template data.
	 */

	/** Prepared <code>List<code> of <code>HtmlItem</code> objects. */
	protected List<HtmlItem> html_items_work = null;

	/**
	 * Prevent creation of identical instances.
	 */
	protected Template() {
	}

	/**
	 * Initialize a template file by loading, parsing and splitting it into sub-parts.
	 * @param templateFileStr template path+file name.
	 * @param templateFile template file-system <code>File</code> object.
	 * @return a parsed and split template file.
	 */
	public static Template getInstance(TemplateMaster templateMaster, String templateFileStr, TemplateStorage templateStorage) {
		Template template = new Template();
		template.templateMaster = templateMaster;
		template.templateFileStr = templateFileStr;
		template.templateStorage = templateStorage;
		template.load();
		return template;
	}

	/**
	 * Check if the template and/or its master have changed and reload them.
	 * Re-process the master if it is reloaded.
	 * @return boolean indicating the template and/or master was reloaded
	 */
	public synchronized boolean check_reload() {
		boolean reloaded = false;
		if ( templateStorage == null ) {
			templateStorage = templateMaster.getTemplateStorage( templateFileStr );
		}
		if ( templateStorage != null ) {
			if ( templateStorage.checkReload() || last_modified != templateStorage.lastModified() || last_file_length != templateStorage.length() ) {
				reload();
				reloaded = true;
			}
			if ( master != null ) {
				if ( master.check_reload() || master.last_processed > last_processed ) {
					html_items_work = null;
					reloaded = true;
				}
				if ( reloaded ) {
					master_process();
				}
			}
			if ( reloaded ) {
				last_processed = System.currentTimeMillis();
			}
			if ( html_items_work == null && html_items_cached != null ) {
				html_items_work = new ArrayList<HtmlItem>( html_items_cached );
			}
		}
		return reloaded;
	}

	/**
	 * Loads and initializes a template.
	 */
	protected void load() {
		reload();
		if ( master != null ) {
			master.check_reload();
			master_process();
			last_processed = System.currentTimeMillis();
		}
		if ( html_items_work == null && html_items_cached != null ) {
			html_items_work = new ArrayList<HtmlItem>( html_items_cached );
		}
	}

	/**
	 * Load or reload a template file parsing and splitting it into <code>HtmlItem</code >sub-parts.
	 * Also makes a call to check for the use of a master page.
	 * TODO detect character encoding before turning into String internally.
	 */
	protected void reload() {
		if ( templateStorage != null && templateStorage.exists() ) {
			last_modified = templateStorage.lastModified();
			last_file_length = templateStorage.length();

			html_items_cached = templateStorage.getHtmlItems();
			html_items_work = null;
			master = null;

			check_master_use();
		}
		else {
			last_modified = -1;
			last_file_length = -1;
			html_items_cached = null;
			html_items_work = null;
			master = null;
			masterPlacesList.clear();
			masterPlacesMap.clear();
		}
	}

	/**
	 * Process list of <code>HtmlItem</code> objects to check for the use of a master file.
	 * If there is a master this templates content is copied into separate template master places.
	 */
	protected void check_master_use() {
		HtmlItem htmlItem;
		String tagname;
		String file;
		TemplateMasterPlace masterplace;

		if ( html_items_cached != null ) {
			/*
			 * Look for at <@master file="..."> directive.
			 */
			for ( int i=0; i<html_items_cached.size(); ++i ) {
				htmlItem = html_items_cached.get( i );
				if ( htmlItem.getType() == HtmlItem.T_DIRECTIVE ) {
					tagname = htmlItem.getTagname();
					if ( "master".compareToIgnoreCase( tagname ) == 0 ) {
						file = htmlItem.getAttribute( "file" );
						if ( file != null && file.length() > 1 ) {
							master = templateMaster.getTemplate( file );
						}
					}
				}
			}

			if ( master != null ) {
				masterPlacesList.clear();
				masterPlacesMap.clear();

				masterplace = null;
				String placeholderName;

				/*
				 * Look for <place placeholder="place_name">[content]</place> structures.
				 * Place [content] inside named template master places.
				 */
				for ( int i=0; i<html_items_cached.size(); ++i ) {
					htmlItem = html_items_cached.get( i );
					if ( htmlItem.getType() == HtmlItem.T_TAG && "place".compareToIgnoreCase( htmlItem.getTagname() ) == 0 ) {
						placeholderName = htmlItem.getAttribute( "placeholder" );
						if ( placeholderName != null && placeholderName.length() > 0 ) {
							masterplace = new TemplateMasterPlace();
							masterplace.placeholderName = placeholderName;
							masterPlacesList.add( masterplace );
							masterPlacesMap.put( masterplace.placeholderName, masterplace );
						}
					}
					else if ( htmlItem.getType() == HtmlItem.T_ENDTAG && "place".compareToIgnoreCase( htmlItem.getTagname() ) == 0 ) {
						masterplace = null;
					}
					else {
						if ( masterplace != null ) {
							masterplace.htmlItems.add( htmlItem );
						}
					}
				}
			}
		}
	}

	/**
	 * Create a new <code>HtmlItem</code> work list based on the master template and the current template.
	 * Loop through the master template inserting <place> content into the <placeholder> location. 
	 */
	protected void master_process() {
		HtmlItem htmlItem;
		String tagName;
		String id;
		TemplateMasterPlace masterplace;

		if ( master != null && html_items_work == null ) {
			html_items_work = new ArrayList<HtmlItem>( master.html_items_work );

			int idx = html_items_work.size() - 1;
			while ( idx >= 0 ) {
				htmlItem = html_items_work.get( idx );
				if ( htmlItem.getType() == HtmlItem.T_TAG ) {
					tagName = htmlItem.getTagname().toLowerCase();
					id = htmlItem.getAttribute( "id" );
					if ( "placeholder".compareTo( tagName) == 0 ) {
						if ( id != null && id.length() > 0 ) {
							masterplace = masterPlacesMap.get( id );
							if ( masterplace != null ) {
								html_items_work.remove( idx );
								html_items_work.addAll( idx, masterplace.htmlItems );
							}
						}
					}
				}
				--idx;
			}
		}
	}

	/**
	 * Returns this templates list of <code>HtmlItem</code> objects.
	 * @return this templates list of <code>HtmlItem</code> objects
	 */
	public synchronized List<HtmlItem> getHtmlItems() {
		return html_items_work;
	}

	/**
	 * 
	 * @param tagIdNameArr
	 * @return
	 */
	public static Map<String, Set<String>> buildTagMap(String[][] tagIdNameArr) {
		Map<String, Set<String>> tagMap = new HashMap<String, Set<String>>();
		Set<String> idNameSet;
		String[] strArr;
		for ( int i=0; i<tagIdNameArr.length; ++i ) {
			strArr = tagIdNameArr[ i ];
			if ( strArr.length > 0 ) {
				idNameSet = tagMap.get( strArr[ 0 ] );
				if ( idNameSet == null ) {
					idNameSet = new HashSet<String>();
					tagMap.put( strArr[ 0 ], idNameSet );
				}
				for ( int j=1; j<strArr.length; ++j ) {
					idNameSet.add( strArr[ j ] );
				}
			}
		}
		return tagMap;
	}

	/**
	 * 
	 * @param tagMap
	 * @return
	 */
	public synchronized int reduce(Map<String, Set<String>> tagMap) {
		HtmlItem htmlItem;
		boolean bKeep;
		String tagName;
		Set<String> idNameSet;
		String id;
		String name;
		StringBuilder sb = new StringBuilder();
		int reductions = 0;
		if ( html_items_work != null ) {
			int i = 0;
			while ( i < html_items_work.size() ) {
				htmlItem = html_items_work.get( i );
				switch ( htmlItem.getType() ) {
				case HtmlItem.T_DIRECTIVE:
					html_items_work.remove( i );
					++reductions;
					logger.log( Level.SEVERE, "Invalid @directive: " + htmlItem.getTagname().toLowerCase() );
					break;
				case HtmlItem.T_COMMENT:
				case HtmlItem.T_EXCLAMATION:
				case HtmlItem.T_PROCESSING:
				case HtmlItem.T_ENDTAG:
				case HtmlItem.T_TEXT:
					html_items_work.remove( i );
					sb.append( htmlItem.getText() );
					++reductions;
					break;
				case HtmlItem.T_TAG:
					tagName = htmlItem.getTagname().toLowerCase();
					bKeep = false;
					if ( "i18n".compareTo( tagName ) == 0 || "placeholder".compareTo( tagName ) == 0 ) {
						bKeep = true;
					}
					else if ( tagMap != null ) {
						idNameSet = tagMap.get( tagName );
						if ( idNameSet != null ) {
							if (  idNameSet.size() == 0  ) {
								bKeep = true;
							}
							else {
								name = htmlItem.getAttribute( "name" );
								if ( name != null && idNameSet.contains( name ) ) {
									bKeep = true;
								}
								else {
									id = htmlItem.getAttribute( "id" );
									if ( id != null && idNameSet.contains( id ) ) {
										bKeep = true;
									}
								}
							}
							
						}
					}
					if ( bKeep ) {
						if ( sb.length() > 0 ) {
							html_items_work.add( i, new HtmlText( sb.toString() ) );
							sb.setLength( 0 );
							++i;
						}
						++i;
					}
					else {
						html_items_work.remove( i );
						sb.append( htmlItem.getText() );
						++reductions;
					}
					break;
				}
			}
			if ( sb.length() > 0 ) {
				html_items_work.add( new HtmlText( sb.toString() ) );
				sb.setLength( 0 );
			}
		}

		return reductions;
	}

	// Check for valid character encoding and cache per encoding.
	public synchronized TemplateParts filterTemplate(List<TemplatePlaceBase> placeHolders, String character_encoding) throws UnsupportedEncodingException, IOException {
		TemplateParts templateParts = new TemplateParts();

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
				case HtmlItem.T_COMMENT:
				case HtmlItem.T_EXCLAMATION:
				case HtmlItem.T_PROCESSING:
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

		return templateParts;
	}

}
