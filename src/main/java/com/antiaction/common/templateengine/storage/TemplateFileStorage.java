/*
 * Created on 01/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine.storage;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.html.HtmlParser;
import com.antiaction.common.html.HtmlReaderInput;
import com.antiaction.common.templateengine.HtmlValidator;

public class TemplateFileStorage implements TemplateStorage {

	private static Logger logger = Logger.getLogger( TemplateFileStorage.class.getName() );

	protected File templateFile;

	/** Cached template, raw bytes. */
	protected byte[] html_raw_bytes = null;

	/** Cached template, modified date. */
	protected long last_modified = -1;

	/** Cached template, file length. */
	protected long last_file_length = -1;

	/** Cached template, raw file converted into separate HTML/XML elements. */
	protected List<HtmlItem> html_items_cached = null;

	public TemplateFileStorage(File templateFile) {
		this.templateFile = templateFile;
		checkReload();
	}

	@Override
	public boolean exists() {
		return (html_items_cached != null);
	}

	@Override
	public synchronized void checkReload() {
		RandomAccessFile ram = null;
		ByteArrayInputStream is = null;
		InputStreamReader reader = null;
		try {
			if ( templateFile.exists() && templateFile.isFile() ) {
				if ( last_modified != templateFile.lastModified() || last_file_length != templateFile.length() ) {
					ram = new RandomAccessFile( templateFile, "r" );
					html_raw_bytes = new byte[ (int)ram.length() ];
					ram.readFully( html_raw_bytes );
					ram.close();
					ram = null;

					last_modified = templateFile.lastModified();
					last_file_length = templateFile.length();

					is = new ByteArrayInputStream( html_raw_bytes );
					reader = new InputStreamReader( is, "UTF-8" );

					// Parse html into a List of html items.
					HtmlParser htmlParser = new HtmlParser();
					html_items_cached = htmlParser.parse( HtmlReaderInput.getInstance( reader ) );

					reader.close();
					reader = null;
					is.close();
					is = null;

					// Validate html.
					HtmlValidator.validate( html_items_cached );

					logger.log( Level.INFO, "Template-loaded: " + templateFile.getAbsolutePath() );
				}
			}
			else {
				html_raw_bytes = null;
				html_items_cached = null;
				logger.log( Level.SEVERE, "Template missing: " + templateFile.getAbsolutePath() );
			}
		}
		catch (IOException e) {
			html_raw_bytes = null;
			html_items_cached = null;
			logger.log( Level.SEVERE, e.toString(), e );
		}
		finally {
			if ( ram != null ) {
				try {
					ram.close();
				}
				catch (IOException e) {
				}
				ram = null;
			}
			if ( reader != null ) {
				try {
					reader.close();
				}
				catch (IOException e) {
				}
				reader = null;
			}
			if ( is != null ) {
				try {
					is.close();
				}
				catch (IOException e) {
				}
				is = null;
			}
		}
	}

	@Override
	public long lastModified() {
		return last_modified;
	}

	@Override
	public long length() {
		return last_file_length;
	}

	@Override
	public List<HtmlItem> getHtmlItems() {
		return html_items_cached;
	}

}
