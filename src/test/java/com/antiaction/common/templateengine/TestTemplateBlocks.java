/*
 * Created on 28/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.File;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.templateengine.storage.TemplateFileStorageManager;
import com.antiaction.common.templateengine.storage.TemplateStorageManager;

@RunWith(JUnit4.class)
public class TestTemplateBlocks {

	@Test
	public void test_templateblocks() {
		File file = TestUtils.getTestResourceFile("");

		TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath(), "UTF-8" );
		TemplateMaster tplMaster = TemplateMaster.getInstance( "default" );
		tplMaster.addTemplateStorage( tplStorMan );
	}

}
