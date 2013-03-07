/*
 * Created on 01/09/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.html.HtmlParser;
import com.antiaction.common.html.HtmlReaderInput;

public class TemplateBlocks {

	/** Template Master. */
	public TemplateMaster templateMaster = null;

	/*
	 * Source template.
	 */

	/** Cached template path+file name. */
	protected String blocksFileStr = null;

	/** Cached template <code>File</code> object. */
	protected File blocksFile = null;

	/** Cached template modified date. */
	protected long last_modified = -1;

	/** Cached template file length. */
	protected long last_file_length = -1;

	/** Cached template raw bytes. */
	protected byte[] html_raw_bytes = null;

	/** Cached template split into separate html/xml elements. */
	protected List<HtmlItem> html_items_cached = null;

	/*
	 * Template blocks
	 */

	protected List<TemplateBlock> blocksList = new ArrayList<TemplateBlock>();

	protected Map<String, TemplateBlock> blocksMap = new HashMap<String, TemplateBlock>();;

	/**
	 * Disable public constructor.
	 */
	protected TemplateBlocks() {
	}

	public static TemplateBlocks getInstance(TemplateMaster templateMaster, String blocksFileStr, File blocksFile) {
		TemplateBlocks template = new TemplateBlocks();
		template.templateMaster = templateMaster;
		template.blocksFileStr = blocksFileStr;
		template.blocksFile = blocksFile;
		template.load();
		return template;
	}

	public boolean check_reload() {
		boolean reloaded = false;
		if ( last_modified != blocksFile.lastModified() || last_file_length != blocksFile.length() ) {
			reload();
		}
		return reloaded;
	}

	/**
	 * Loads and initializes a template.
	 */
	protected void load() {
		reload();
	}

	/**
	 * Load or reload a template file parsing and splitting it into <code>HtmlItem</code >sub-parts.
	 * TODO detect character encoding before turning into String internally.
	 */
	protected void reload() {
		RandomAccessFile ram;
		try {
			if ( blocksFile.exists() && blocksFile.isFile() ) {
				last_modified = blocksFile.lastModified();
				last_file_length = blocksFile.length();

				ram = new RandomAccessFile( blocksFile, "r" );
				html_raw_bytes = new byte[ (int)ram.length() ];
				ram.readFully( html_raw_bytes );
				ram.close();

				// debug
				System.out.println( "Template-blocks-loading: " + blocksFile.getCanonicalFile() );

				ByteArrayInputStream is = new ByteArrayInputStream( html_raw_bytes );
				InputStreamReader reader = new InputStreamReader( is, "utf-8" );

				// Parse html into a List of html items.
				HtmlParser htmlParser = new HtmlParser();
				html_items_cached = htmlParser.parse( HtmlReaderInput.getInstance( reader ) );

				reader.close();
				is.close();

				parse_blocks();
			}
			else {
				last_modified = -1;
				last_file_length = -1;
				html_raw_bytes = null;
				html_items_cached = null;
			}
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void parse_blocks() {
		HtmlItem htmlItem;
		String tagName;
		String id;

		TemplateBlock block = null;

		int state = 0;
		boolean bProcess;

		if ( html_items_cached != null ) {
			int i = 0;
			while (  i<html_items_cached.size() ) {
				htmlItem = html_items_cached.get( i );
				switch ( state ) {
				case 0:
					if ( htmlItem.getType() == HtmlItem.T_TAG ) {
						tagName = htmlItem.getTagname().toLowerCase();
						id = htmlItem.getAttribute( "id" );
						if ( "part".compareTo( tagName ) == 0 || "block".compareTo( tagName ) == 0 ) {
							block = new TemplateBlock();
							blocksList.add( block );
							if ( id != null && id.length() > 0 ) {
								blocksMap.put( id, block );
							}
							state = 1;
						}
					}
					++i;
					break;
				case 1:
					bProcess = true;
					if ( htmlItem.getType() == HtmlItem.T_ENDTAG ) {
						tagName = htmlItem.getTagname().toLowerCase();
						if ( "part".compareTo( tagName ) == 0 || "block".compareTo( tagName ) == 0 ) {
							block = null;
							bProcess = false;
							state = 0;
						}
					}
					if ( bProcess ) {
						block.html_items_cached.add( htmlItem );
					}
					++i;
					break;
				}
			}
		}
	}

}
