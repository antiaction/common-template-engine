/*
 * Created on 01/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine.storage;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemplateFileStorageManager implements TemplateStorageManager {

    /** Logging mechanism. */
	private static Logger logger = Logger.getLogger( TemplateFileStorageManager.class.getName() );

	/** Map of cached <code>TemplateStorageManager</code> instances. */
	protected static Map<String, TemplateFileStorageManager> pathMap = new HashMap<String, TemplateFileStorageManager>();

	/** Path to root of template files. */
	protected File templatePath = null;

	/** Map of cached <code>TemplateStorage</code> instances. */
	protected Map<String, TemplateFileStorage> storageMap = new HashMap<String, TemplateFileStorage>();

	/*
	 * Prevent creation of identical instances.
	 */
	protected TemplateFileStorageManager() {
	}

	/**
	 * Get singleton <code>TemplateMaster</code> instance. 
	 * Handles creation and caching of requested templates. 
	 * Also assures only one instance of each template.
	 * @param templatePath path to root of template files
	 * @return <code>TemplateFileStorage</code> instance
	 */
	public static synchronized TemplateStorageManager getInstance(String templatePath) {
		TemplateFileStorageManager tplStorMan = pathMap.get( templatePath );
		if ( tplStorMan == null ) {
			tplStorMan = new TemplateFileStorageManager();
			tplStorMan.templatePath = new File( templatePath );
			pathMap.put( templatePath, tplStorMan );
		}
		return tplStorMan;
	}

	@Override
	public TemplateStorage getTemplateStorage(String templateFileStr) {
		synchronized ( storageMap ) {
			TemplateFileStorage tplStor = storageMap.get( templateFileStr );
			if ( tplStor == null ) {
				File templateFile = new File( templatePath, templateFileStr );
				if ( templateFile.exists() && templateFile.isFile() ) {
					tplStor = new TemplateFileStorage( templateFile );
					storageMap.put( templateFileStr, tplStor );
				}
				else {
					logger.log( Level.SEVERE, "Template missing: " + templateFile.getAbsolutePath() );
				}
			}
			return tplStor;
		}
	}

}
