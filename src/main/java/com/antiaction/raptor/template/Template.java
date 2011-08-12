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
import java.util.List;

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
	public List<HtmlItem> html_items = null;

	/** Master template. */
	public Template master = null;

	public List<TemplatePlace> places;

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
		template.reload();
		return template;
	}

	/**
	 * Load or reload a template file parsing and splitting it into sub-parts.
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
				html_items = htmlParser.parse( HtmlReaderInput.getInstance( reader ) );

				// Validate html. 
				HtmlValidator.validate( html_items );

				if ( html_items != null ) {
					HtmlItem htmlItem;
					String tagname;
					String file;
					for ( int i=0; i<html_items.size(); ++i ) {
						htmlItem = html_items.get( i );
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
				}

				reader.close();
				is.close();
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
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

		if ( html_items != null ) {
			int i = 0;
			while (  i<html_items.size() ) {
				htmlItem = html_items.get( i );
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

}
