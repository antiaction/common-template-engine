/*
 * Created on 28/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.templateengine.storage.TemplateFileStorageManager;
import com.antiaction.common.templateengine.storage.TemplateStorage;
import com.antiaction.common.templateengine.storage.TemplateStorageManager;

@RunWith(JUnit4.class)
public class TestTemplateMaster {

	@Test
	public void test_templateblocks() {
		File file = TestUtils.getTestResourceFile("");

		/*
		 * Instance.
		 */
		TemplateStorage tplStor;

		TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath(), "UTF-8" );
		Assert.assertNotNull( tplStorMan );

		//Assert.assertEquals( 0, TemplateMaster.templateMasterMap.size() );

		TemplateMaster tplMaster = TemplateMaster.getInstance( "default" );
		TemplateMaster tplMaster2 = TemplateMaster.getInstance( "default" );
		Assert.assertEquals( tplMaster, tplMaster2 );

		Assert.assertEquals( 1, TemplateMaster.templateMasterMap.size() );

		//Assert.assertEquals( 0, tplMaster.templateStorageManagerList.size() );

		tplMaster.addTemplateStorage( tplStorMan );

		Assert.assertEquals( 1, tplMaster.templateStorageManagerList.size() );

		tplStor = tplMaster.getTemplateStorage( "missing.html" );
		Assert.assertNull( tplStor );

		/*
		<html>
		templatefile
		<placeholder id="ph1" />
		</html>
		*/

		tplStor = tplMaster.getTemplateStorage( "templatefile.html" );
		Assert.assertNotNull( tplStor );
	}

}
