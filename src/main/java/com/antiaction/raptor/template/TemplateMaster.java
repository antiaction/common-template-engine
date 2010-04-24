/*
 * Created on 19/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Entry point for interacting with the template system. 
 * Currently only a single <code>TemplateMaster</code> instance is supported. 
 * <code>Template</code> instances are created and cached through the master.
 * @author Nicholas
 *
 */
public class TemplateMaster {

	/** <code>TemplateMaster</code> singleton instance. */
	private static TemplateMaster templateMaster = null;

	/** Path to root of template files. */
	private File templatePath = null;

	/** Map of cached <code>Template</code> instances. */
	private Map<String, Template> templates = new HashMap<String, Template>();

	/*
	 * Prevent creation of identical instances.
	 */
	private TemplateMaster() {
	}

	/**
	 * Get singleton <code>TemplateMaster</code> instance. 
	 * Handles creation and caching of requested templates. 
	 * Also assures only one instance of each template.
	 * @param templatePath path to root of template files
	 * @return <code>TemplateMaster</code> instance
	 */
	public static TemplateMaster getInstance(String templatePath) {
		if ( templateMaster == null ) {
			templateMaster = new TemplateMaster();
			templateMaster.templatePath = new File( templatePath );
		}
		return templateMaster;
	}

	/**
	 * 
	 * @param templateFileStr
	 * @return
	 */
	public Template getTemplate(String templateFileStr) {
		File templateFile = new File( templatePath, templateFileStr );
		Template template = null;
		synchronized ( templates ) {
			template = templates.get( templateFileStr );
			if ( template == null ) {
				template = Template.getInstance( templateFile );
				templates.put( templateFileStr, template );
			}
		}
		return template;
	}

}
