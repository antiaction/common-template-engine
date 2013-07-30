/*
 * Created on 19/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.html.HtmlParser;
import com.antiaction.common.html.HtmlReaderInput;
import com.antiaction.common.html.HtmlText;

/**
 * Thread-safe as long as check_reload() is the only method used externally.
 *
 * @author Nicholas
 */
public class Template {

	/** Template Master. */
	protected TemplateMaster templateMaster = null;

	/*
	 * Source template.
	 */

	/** Cached template path+file name. */
	protected String templateFileStr = null;

	/** Cached template <code>File</code> object. */
	protected File templateFile = null;

	/** Cached template, modified date. */
	protected long last_modified = -1;

	/** Cached template, file length. */
	protected long last_file_length = -1;

	/** Cached template, raw bytes. */
	protected byte[] html_raw_bytes = null;

	/** Cached template, raw file converted into separate HTML/XML elements. */
	protected List<HtmlItem> html_items_cached = null;

	/*
	 * Master template
	 */

	protected long masterProcessedTS;

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
	public static Template getInstance(TemplateMaster templateMaster, String templateFileStr, File templateFile) {
		Template template = new Template();
		template.templateMaster = templateMaster;
		template.templateFileStr = templateFileStr;
		template.templateFile = templateFile;
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
		if ( last_modified != templateFile.lastModified() || last_file_length != templateFile.length() ) {
			reload();
			reloaded = true;
		}
		if ( master != null ) {
			if ( master.check_reload() || master.last_modified > masterProcessedTS ) {
				html_items_work = null;
				reloaded = true;
			}
			if ( reloaded ) {
				master_process();
			}
		}
		if ( html_items_work == null && html_items_cached != null ) {
			html_items_work = new ArrayList<HtmlItem>( html_items_cached );
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
		}
		if ( html_items_work == null && html_items_cached != null ) {
			html_items_work = new ArrayList<HtmlItem>( html_items_cached );
		}
	}

	/**
	 * Load or reload a template file parsing and splitting it into <code>HtmlItem</code >sub-parts.
	 * Also makes call to check for use of master page.
	 * TODO detect character encoding before turning into String internally.
	 */
	protected void reload() {
		RandomAccessFile ram;
		try {
			if ( templateFile.exists() && templateFile.isFile() ) {
				last_modified = templateFile.lastModified();
				last_file_length = templateFile.length();

				ram = new RandomAccessFile( templateFile, "r" );
				html_raw_bytes = new byte[ (int)ram.length() ];
				ram.readFully( html_raw_bytes );
				ram.close();

				// debug
				System.out.println( "Template-loading: " + templateFile.getCanonicalFile() );

				ByteArrayInputStream is = new ByteArrayInputStream( html_raw_bytes );
				InputStreamReader reader = new InputStreamReader( is, "utf-8" );

				// Parse html into a List of html items.
				HtmlParser htmlParser = new HtmlParser();
				html_items_cached = htmlParser.parse( HtmlReaderInput.getInstance( reader ) );
				html_items_work = null;

				reader.close();
				is.close();

				// Validate html. 
				HtmlValidator.validate( html_items_cached );

				master = null;

				check_master_use();
			}
			else {
				last_modified = -1;
				last_file_length = -1;
				html_raw_bytes = null;
				html_items_cached = null;
				html_items_work = null;
				master = null;
				masterPlacesList.clear();
				masterPlacesMap.clear();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Process list of <code>HtmlItem</code> objects to check for use of a master file.
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
			masterProcessedTS = System.currentTimeMillis();
		}
	}

	/**
	 * 
	 * @param tagnameList
	 * @return
	 */
	public int prepare(List<String> tagnameList) {
		Set<String> tagnameSet = new HashSet<String>();
		if ( tagnameList != null ) {
			for ( int i=0; i<tagnameList.size(); ++i ) {
				tagnameSet.add( tagnameList.get( i ).toLowerCase() );
			}
		}

		HtmlItem htmlItem;
		String tagName;

		int reductions = 0;

		StringBuffer sb = new StringBuffer();

		if ( html_items_work != null ) {
			int i = 0;
			while ( i<html_items_work.size() ) {
				htmlItem = html_items_work.get( i );
				switch ( htmlItem.getType() ) {
				case HtmlItem.T_DIRECTIVE:
					html_items_work.remove( i );
					++reductions;
					// debug
					System.out.println( "Invalid @directive: " + htmlItem.getTagname().toLowerCase() );
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
					if ( "i18n".compareTo( tagName ) == 0 || "placeholder".compareTo( tagName ) == 0 || tagnameSet.contains( tagName ) ) {
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
	public TemplateParts filterTemplate(List<TemplatePlaceBase> placeHolders, String character_encoding) throws UnsupportedEncodingException, IOException {
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
					System.out.println( "Invalid @directive: " + htmlItem.getTagname().toLowerCase() );
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
				case HtmlItem.T_ENDTAG:
				case HtmlItem.T_TEXT:
				case HtmlItem.T_PROCESSING:
				case HtmlItem.T_EXCLAMATION:
				case HtmlItem.T_COMMENT:
					out.write( htmlItem.getText().getBytes( character_encoding ) );
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

	/**
	 * Internal class to keep track of <code>Place</code> tags in a master template.
	 */
	public class TemplateMasterPlace {

		public String placeholderName = null;

		public List<HtmlItem> htmlItems = new ArrayList<HtmlItem>();

	}

}
