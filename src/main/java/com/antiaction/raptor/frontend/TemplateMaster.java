/*
 * Created on 19/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.frontend;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class TemplateMaster {

	private static TemplateMaster templateMaster = null;

	private File templatePath = null;

	private Map<String, Template> templates = new HashMap<String, Template>();

	private TemplateMaster() {
	}

	public static TemplateMaster getInstance(String templatePath) {
		if ( templateMaster == null ) {
			templateMaster = new TemplateMaster();
			templateMaster.templatePath = new File( templatePath );
		}
		return templateMaster;
	}

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
