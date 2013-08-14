/*
 * Created on 19/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.antiaction.common.templateengine.storage.TemplateStorage;
import com.antiaction.common.templateengine.storage.TemplateStorageManager;

/**
 * Entry point for interacting with the template system. 
 * Currently only a single <code>TemplateMaster</code> instance is supported. 
 * <code>Template</code> instances are created and cached through the master. 
 * This implementation is thread-safe.
 *
 * @author Nicholas
 */
public class TemplateMaster {

	/** <code>TemplateMaster</code> singleton instance. */
	protected static Map<String, TemplateMaster> templateMasterMap = new HashMap<String, TemplateMaster>();

	protected List<TemplateStorageManager> templateStorageManagerList = new ArrayList<TemplateStorageManager>();

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
	public static TemplateMaster getInstance(String instanceId) {
		synchronized ( templateMasterMap ) {
			TemplateMaster templateMaster = templateMasterMap.get( instanceId );
			if ( templateMaster == null ) {
				templateMaster = new TemplateMaster();
				templateMasterMap.put( instanceId, templateMaster );
			}
			return templateMaster;
		}
	}

	public void addTemplateStorage(TemplateStorageManager tplStorMan) {
		synchronized ( templateStorageManagerList ) {
			templateStorageManagerList.add( tplStorMan );
		}
	}

	public TemplateStorage getTemplateStorage(String templateFileStr) {
		TemplateStorage templateStorage = null;
		int idx = 0;
		synchronized ( templateStorageManagerList ) {
			while ( templateStorage == null && idx < templateStorageManagerList.size() ) {
				templateStorage = templateStorageManagerList.get( idx++ ).getTemplateStorage( templateFileStr );
			}
		}
		return templateStorage;
	}

	/**
	 * Given a template filename with relative path returns a template interaction object.
	 * The template is either loaded and cached and only reloaded if the content changes.
	 * @param templateFileStr template filename with relative path
	 * @return prepared template interaction object
	 */
	public Template getTemplate(String templateFileStr) {
		synchronized ( templateMap ) {
			Template template = templateMap.get( templateFileStr );
			if ( template == null ) {
				TemplateStorage templateStorage = getTemplateStorage( templateFileStr );
				template = Template.getInstance( this, templateFileStr, templateStorage );
				templateMap.put( templateFileStr, template );
				templateList.add( template );
			}
			else {
				template.check_reload();
			}
			return template;
		}
	}

	/**
	 * Given a template blocks filename with relative path returns a template blocks interactive object.
	 * The template blocks are either loaded and cached and only reloaded if the content changes.
	 * @param blocksFileStr template blocks filename with relative path
	 * @return prepared template blocks interactive object
	 */
	public TemplateBlocks getTemplateBlocks(String blocksFileStr) {
		synchronized ( blocksMap ) {
			TemplateBlocks blocks = blocksMap.get( blocksFileStr );
			if ( blocks == null ) {
				TemplateStorage templateStorage = getTemplateStorage( blocksFileStr );
				blocks = TemplateBlocks.getInstance( this, blocksFileStr, templateStorage );
				blocksMap.put( blocksFileStr, blocks );
				blocksList.add( blocks );
			}
			else {
				blocks.check_reload();
			}
			return blocks;
		}
	}

}
