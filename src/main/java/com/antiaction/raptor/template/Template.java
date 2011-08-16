/*
 * Created on 19/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

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
import java.util.List;
import java.util.Map;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.html.HtmlParser;
import com.antiaction.common.html.HtmlReaderInput;

public class Template {

	/** Template Master. */
	public TemplateMaster templateMaster = null;

	/** Cached template path+file name. */
	public String templateFileStr = null;

	/** Cached template <code>File</code> object. */
	public File templateFile = null;

	/** Cached template modified date. */
	public long last_modified = -1;

	/** Cached template file length. */
	public long last_file_length = -1;

	/** Cached template raw bytes. */
	public byte[] html_raw_bytes = null;

	/** Cached template split into separate html/xml elements. */
	public List<HtmlItem> html_items_cached = null;

	public List<HtmlItem> html_items_work = null;

	/** Master template. */
	public Template master = null;

	public List<TemplatePlace> placesList = new ArrayList<TemplatePlace>();

	public Map<String, TemplatePlace> placesMap = new HashMap<String, TemplatePlace>();;

	/**
	 * Disable public constructor.
	 */
	private Template() {
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

	public void load() {
		reload();
		if ( master != null ) {
			master.check_reload();
			master_process();
		}
		if ( html_items_work == null && html_items_cached != null ) {
			html_items_work = new ArrayList<HtmlItem>( html_items_cached );
		}
	}

	public boolean check_reload() {
		boolean reloaded = false;
		if ( last_modified != templateFile.lastModified() || last_file_length != templateFile.length() ) {
			reload();
			reloaded = true;
		}
		if ( master != null ) {
			if ( master.check_reload() ) {
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
	 * Load or reload a template file parsing and splitting it into sub-parts.
	 * Also makes call to check for use of master page.
	 * TODO detect character encoding before turning into String internally.
	 */
	public void reload() {
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
				placesList.clear();
				placesMap.clear();
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
	 * Process list of <code>HtmlItems</code> to check for use of a master file.
	 */
	public void check_master_use() {
		HtmlItem htmlItem;
		String tagname;
		String file;
		TemplatePlace place;

		if ( html_items_cached != null ) {
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
				placesList.clear();
				placesMap.clear();

				place = null;
				String placeholder;

				for ( int i=0; i<html_items_cached.size(); ++i ) {
					htmlItem = html_items_cached.get( i );
					if ( htmlItem.getType() == HtmlItem.T_TAG && "place".compareToIgnoreCase( htmlItem.getTagname() ) == 0 ) {
						placeholder = htmlItem.getAttribute( "placeholder" );
						if ( placeholder != null && placeholder.length() > 0 ) {
							place = new TemplatePlace();
							place.placeholder = placeholder;
							placesList.add( place );
							placesMap.put( place.placeholder, place );
						}
					}
					else if ( htmlItem.getType() == HtmlItem.T_ENDTAG && "place".compareToIgnoreCase( htmlItem.getTagname() ) == 0 ) {
						place = null;
					}
					else {
						if ( place != null ) {
							place.htmlItems.add( htmlItem );
						}
					}
				}
			}
		}
	}

	/**
	 * Create a new <code>HtmlItem</code> work list based on the master template and the current template.
	 */
	public void master_process() {
		HtmlItem htmlItem;
		String tagName;
		String id;
		TemplatePlace place;

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
							place = placesMap.get( id );
							if ( place != null ) {
								html_items_work.remove( idx );
								html_items_work.addAll( idx, place.htmlItems );
							}
						}
					}
				}
				--idx;
			}
		}
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
					System.out.println( "@directive: " + htmlItem.getTagname().toLowerCase() );
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
					else if ( "placeholder".compareTo( tagName) == 0 ) {
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
									if ( "placeholder".compareTo( tagName) == 0 ) {
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

	public class TemplatePlace {

		public String placeholder = null;

		public List<HtmlItem> htmlItems = new ArrayList<HtmlItem>();

	}

}
