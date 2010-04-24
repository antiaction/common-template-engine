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
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.List;

import com.antiaction.common.html.HTMLItem;
import com.antiaction.common.html.HTMLParser;

public class Template {

	public byte[] raw_html = null;

	public List<HTMLItem> html_items = null;

	private Template() {
	}

	public static Template getInstance(File templateFile) {
		Template t = new Template();

		RandomAccessFile ram;
		try {
			ram = new RandomAccessFile( templateFile, "r" );
			t.raw_html = new byte[ (int)ram.length() ];
			ram.readFully( t.raw_html );

			ByteArrayInputStream is = new ByteArrayInputStream( t.raw_html );

			HTMLParser htmlParser = new HTMLParser();
			t.html_items = htmlParser.parse( is );

			is.close();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return t;
	}

	// Check for valid character encoding and cache per encoding.
	public TemplateParts filterTemplate(List<TemplatePlace> placeHolders, String character_encoding) throws UnsupportedEncodingException, IOException {
		TemplateParts templateParts = new TemplateParts();

		String tagName;
		String id;
		String name;

		Boolean b;
		Boolean f;

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.reset();

		HTMLItem htmlItem;
		TemplatePlace templatePlace;

		if ( html_items != null ) {
			int i = 0;
			while (  i<html_items.size() ) {
				htmlItem = html_items.get( i );
				switch ( htmlItem.getType() ) {
				case HTMLItem.T_TAG:
					tagName = htmlItem.getTagname().toLowerCase();
					id = htmlItem.getAttribute( "id" );
					name = htmlItem.getAttribute( "name" );

					templatePlace = null;
					f = false;

					if ( id != null || name != null ) {
						int j = 0;
						b = true;
						while ( b ) {
							if ( j < placeHolders.size() ) {
								templatePlace = placeHolders.get( j );
								switch ( templatePlace.type ) {
								case TemplatePlace.PH_TAG:
									if ( templatePlace.tagName.compareTo( tagName ) == 0 ) {
										if ( ( id != null && templatePlace.idName.compareTo( id ) == 0) || (name != null && templatePlace.idName.compareTo( name ) == 0 ) ) {
											b = false;
											f = true;
										}
									}
									break;
								case TemplatePlace.PH_PLACEHOLDER:
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
							templateParts.parts.add( TemplatePart.getTemplatePartStatic( out.toByteArray() ) );
							out.reset();
						}
						templateParts.parts.add( TemplatePart.getTemplatePartTag( (HTMLItem)htmlItem.clone(), templatePlace ) );
					}
					else {
						out.write( htmlItem.getText().getBytes( character_encoding ) );
					}
					break;
				case HTMLItem.T_ENDTAG:
				case HTMLItem.T_TEXT:
				case HTMLItem.T_PROCESSING:
				case HTMLItem.T_EXCLAMATION:
				case HTMLItem.T_COMMENT:
					out.write( htmlItem.getText().getBytes( character_encoding ) );
					break;
				}
				++i;
			}
			if ( out.size() > 0 ) {
				templateParts.parts.add( TemplatePart.getTemplatePartStatic( out.toByteArray() ) );
				out.reset();
			}
		}
		else {
		}

		return templateParts;
	}

}
