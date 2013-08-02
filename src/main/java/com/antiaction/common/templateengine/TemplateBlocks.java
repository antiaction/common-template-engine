/*
 * Created on 01/09/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.templateengine.storage.TemplateStorage;

public class TemplateBlocks {

	/** Template Master. */
	public TemplateMaster templateMaster = null;

	/*
	 * Source template.
	 */

	/** Template storage object. */
	protected TemplateStorage templateStorage = null;

	/** Cached template modified date. */
	protected long last_modified = -1;

	/** Cached template file length. */
	protected long last_file_length = -1;

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

	public static TemplateBlocks getInstance(TemplateMaster templateMaster, TemplateStorage templateStorage) {
		TemplateBlocks template = new TemplateBlocks();
		template.templateMaster = templateMaster;
		template.templateStorage = templateStorage;
		template.load();
		return template;
	}

	public boolean check_reload() {
		boolean reloaded = false;
		if ( templateStorage == null ) {
		}
		if ( templateStorage != null ) {
			templateStorage.checkReload();
			if ( last_modified != templateStorage.lastModified() || last_file_length != templateStorage.length() ) {
				reload();
				reloaded = true;
			}
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
		if ( templateStorage != null && templateStorage.exists() ) {
			last_modified = templateStorage.lastModified();
			last_file_length = templateStorage.length();

			html_items_cached = templateStorage.getHtmlItems();

			parse_blocks();
		}
		else {
			last_modified = -1;
			last_file_length = -1;
			html_items_cached = null;
		}
	}

	protected void parse_blocks() {
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
