/*
 * Created on 19/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point for interacting with the template system. 
 * Currently only a single <code>TemplateMaster</code> instance is supported. 
 * <code>Template</code> instances are created and cached through the master.
 *
 * @author Nicholas
 */
public class TemplateMaster {

	/** <code>TemplateMaster</code> singleton instance. */
	protected static TemplateMaster templateMaster = null;

	/** Path to root of template files. */
	protected File templatePath = null;

	/** Map of cached <code>Template</code> instances. */
	protected Map<String, Template> templateMap = new HashMap<String, Template>();

	/** List of cached <code>Template</code> instances. */
	protected List<Template> templateList = new ArrayList<Template>();

	/** Map of cached <code>TemplateBlocks</code> instances. */
	protected Map<String, TemplateBlocks> blocksMap = new HashMap<String, TemplateBlocks>();

	/** List of cached <code>TemplateBlocks</code> instances. */
	protected List<TemplateBlocks> blocksList = new ArrayList<TemplateBlocks>();

	/*
	 * Prevent creation of identical instances.
	 */
	protected TemplateMaster() {
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
	 * Given a template filename with relative path returns a template interaction object.
	 * The template is either loaded and cached and only reloaded if the content changes.
	 * @param templateFileStr template filename with relative path
	 * @return prepared template interaction object
	 */
	public Template getTemplate(String templateFileStr) {
		File templateFile = new File( templatePath, templateFileStr );
		Template template = null;
		synchronized ( templateMap ) {
			template = templateMap.get( templateFileStr );
			if ( template != null ) {
				if ( templateFile.exists() && templateFile.isFile() ) {
					template.check_reload();
				}
				else {
					template = null;
				}
			}
			else {
				template = Template.getInstance( this, templateFileStr, templateFile );
				if ( template != null ) {
					templateMap.put( templateFileStr, template );
					templateList.add( template );
				}
			}
		}
		return template;
	}

	/**
	 * Given a template blocks filename with relative path returns a template blocks interactive object.
	 * The template blocks are either loaded and cached and only reloaded if the content changes.
	 * @param blocksFileStr template blocks filename with relative path
	 * @return prepared template blocks interactive object
	 */
	public TemplateBlocks getTemplateBlocks(String blocksFileStr) {
		File blocksFile = new File( templatePath, blocksFileStr );
		TemplateBlocks blocks = null;
		synchronized ( blocksMap ) {
			blocks = blocksMap.get( blocksFileStr );
			if ( blocks != null ) {
				if ( blocksFile.exists() && blocksFile.isFile() ) {
					blocks.check_reload();
				}
				else {
					blocks = null;
				}
			}
			else {
				blocks = TemplateBlocks.getInstance( this, blocksFileStr, blocksFile );
				if ( blocks != null ) {
					blocksMap.put( blocksFileStr, blocks );
					blocksList.add( blocks );
				}
			}
		}
		return blocks;
	}

}
