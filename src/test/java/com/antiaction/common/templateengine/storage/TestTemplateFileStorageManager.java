/*
 * Created on 18/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine.storage;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestTemplateFileStorageManager {

	public String getUrlPath(URL url) {
		String path = url.getFile();
		path = path.replaceAll("%5b", "[");
		path = path.replaceAll("%5d", "]");
		return path;
	}

	@Test
	public void test_templatefilestoragemanager() {
		URL url;
		File file;
		url = this.getClass().getClassLoader().getResource("dir1/template1.html");
		file = new File(getUrlPath(url)).getParentFile().getParentFile();

		String dir1 = new File( file, "dir1" ).getPath();
		String dir2 = new File( file, "dir2" ).getPath();

		TemplateStorageManager tplStorMan1a = TemplateFileStorageManager.getInstance( dir1, "UTF-8" );
		Assert.assertNotNull( tplStorMan1a );
		TemplateStorageManager tplStorMan2a = TemplateFileStorageManager.getInstance( dir2, "UTF-8" );
		Assert.assertNotNull( tplStorMan2a );
		TemplateStorageManager tplStorMan1b = TemplateFileStorageManager.getInstance( dir1, "UTF-8" );
		Assert.assertNotNull( tplStorMan1b );
		TemplateStorageManager tplStorMan2b = TemplateFileStorageManager.getInstance( dir2, "UTF-8" );
		Assert.assertNotNull( tplStorMan2b );

		Assert.assertEquals( tplStorMan1a, tplStorMan1b );
		Assert.assertEquals( tplStorMan2a, tplStorMan2b );

		TemplateStorage tplStor1a = tplStorMan1a.getTemplateStorage( "template1.html" );
		Assert.assertNotNull( tplStor1a );
		TemplateStorage tplStor1b = tplStorMan1b.getTemplateStorage( "template1.html" );
		Assert.assertNotNull( tplStor1b );
		TemplateStorage tplStor2a = tplStorMan2a.getTemplateStorage( "template1.html" );
		Assert.assertNotNull( tplStor2a );
		TemplateStorage tplStor2b = tplStorMan2b.getTemplateStorage( "template1.html" );
		Assert.assertNotNull( tplStor2b );

		Assert.assertEquals( tplStor1a, tplStor1b );
		Assert.assertEquals( tplStor2a, tplStor2b );

		Assert.assertNull( tplStorMan1a.getTemplateStorage( "template.html" ) );
		Assert.assertNull( tplStorMan1b.getTemplateStorage( "template.html" ) );
		Assert.assertNull( tplStorMan2a.getTemplateStorage( "template.html" ) );
		Assert.assertNull( tplStorMan2b.getTemplateStorage( "template.html" ) );

		TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath(), "UTF-8" );
		Assert.assertNotNull( tplStorMan );
		Assert.assertNull( tplStorMan.getTemplateStorage( "dir1" ) );
		Assert.assertNull( tplStorMan.getTemplateStorage( "dir2" ) );
	}

}
