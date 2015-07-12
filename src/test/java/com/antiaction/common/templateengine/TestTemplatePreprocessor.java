/*
 * Created on 07/06/2014
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.templateengine.storage.TemplateFileStorageManager;
import com.antiaction.common.templateengine.storage.TemplateStorageManager;

@RunWith(JUnit4.class)
public class TestTemplatePreprocessor {

	@Test
	public void test_templatepreprocessor() {
		File file = TestUtils.getTestResourceFile("");

		try {
			TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath(), "UTF-8" );
			TemplateMaster tplMaster = TemplateMaster.getInstance( "default" );
			tplMaster.addTemplateStorage( tplStorMan );
			TemplatePreprocessor tplPp;

			/*
			 * Missing.
			 */

			tplPp = tplMaster.getTemplatePreprocessor( "missing.html", new HashMap<String, Set<String>>(), "UTF-8" );
			Assert.assertNotNull( tplPp );
			Assert.assertNotNull( tplPp.template );
			Assert.assertNull( tplPp.template.getHtmlItems() );

			tplPp.check_reload();
			Assert.assertNotNull( tplPp.template );
			Assert.assertNull( tplPp.template.getHtmlItems() );

			/*
			 * Template.
			 */

			tplPp = tplMaster.getTemplatePreprocessor( "templatefile.html", new HashMap<String, Set<String>>(), "UTF-8" );
			Assert.assertNotNull( tplPp );
			Assert.assertNotNull( tplPp.template );
			Assert.assertNotNull( tplPp.template.getHtmlItems() );

			tplPp.check_reload();
			Assert.assertNotNull( tplPp.template );
			Assert.assertNotNull( tplPp.template.getHtmlItems() );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	@Test
	public void test_templatepreprocessor2() {
		File file = TestUtils.getTestResourceFile("");

		TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath(), "UTF-8" );
		TemplateMaster tplMaster = TemplateMaster.getInstance( "default" );
		tplMaster.addTemplateStorage( tplStorMan );

		try {
			TemplatePreprocessor tplPp = tplMaster.getTemplatePreprocessor( "templatefile.html", new HashMap<String, Set<String>>(), "UTF-8" );
			Assert.assertNotNull( tplPp );

			Template tpl = tplMaster.getTemplate( "templatefile.html" );
			Assert.assertNotNull( tpl );

			Assert.assertEquals( tplPp.getTemplate(), tpl );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	@Test
	public void test_templatepreprocessor_master() {
		File file = TestUtils.getTestResourceFile("");

		try {
			TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath(), "UTF-8" );
			TemplateMaster tplMaster = TemplateMaster.getInstance( "default" );
			tplMaster.addTemplateStorage( tplStorMan );

			String master;
			String child1a;
			String child1b;
			String child2a;

			master = "<html>";
			master += "templatemaster";
			master += "<placeholder id=\"ph1\" />";
			master += "</html>";

			child1a = "<@master file=\"masterfile.html\">";
			child1a += "<place placeholder=\"ph1\">";
			child1a += "b41a";
			child1a += "<placeholder id=\"ph1\" />";
			child1a += "after1a";
			child1a += "</place>";

			child1b = "<@master file=\"masterfile.html\">";
			child1b += "<place placeholder=\"ph1\">";
			child1b += "b41b";
			child1b += "<placeholder id=\"ph1\" />";
			child1b += "after1b";
			child1b += "</place>";

			child2a = "<@master file=\"child1afile.html\">";
			child2a += "<place placeholder=\"ph1\">";
			child2a += "b42a";
			child2a += "<placeholder id=\"ph2\" />";
			child2a += "after2a";
			child2a += "</place>";

			File masterFile = new File( file, "masterfile.html" );
			File child1aFile = new File( file, "child1afile.html" );
			File child1bFile = new File( file, "child1bfile.html" );
			File child2aFile = new File( file, "child2afile.html" );

			TestUtils.saveBytes( masterFile, master.getBytes() );
			TestUtils.saveBytes( child1aFile, child1a.getBytes() );
			TestUtils.saveBytes( child1bFile, child1b.getBytes() );
			TestUtils.saveBytes( child2aFile, child2a.getBytes() );

			TemplatePreprocessor master_tplPp = tplMaster.getTemplatePreprocessor( "masterfile.html", null, "UTF-8" );
			TemplatePreprocessor child1a_tplPp = tplMaster.getTemplatePreprocessor( "child1afile.html", null, "UTF-8" );
			TemplatePreprocessor child1b_tplPp = tplMaster.getTemplatePreprocessor( "child1bfile.html", null, "UTF-8" );
			TemplatePreprocessor child2a_tplPp = tplMaster.getTemplatePreprocessor( "child2afile.html", null, "UTF-8" );

			Assert.assertFalse( master_tplPp.check_reload() );
			Assert.assertFalse( child1a_tplPp.check_reload() );
			Assert.assertFalse( child1b_tplPp.check_reload() );
			Assert.assertFalse( child2a_tplPp.check_reload() );

			Assert.assertNotNull( master_tplPp );
			Assert.assertNotNull( master_tplPp.template );
			Assert.assertNotNull( master_tplPp.template.getHtmlItems() );
			Assert.assertNotNull( child1a_tplPp );
			Assert.assertNotNull( child1a_tplPp.template );
			Assert.assertNotNull( child1a_tplPp.template.getHtmlItems() );
			Assert.assertNotNull( child1b_tplPp );
			Assert.assertNotNull( child1b_tplPp.template );
			Assert.assertNotNull( child1b_tplPp.template.getHtmlItems() );
			Assert.assertNotNull( child2a_tplPp );
			Assert.assertNotNull( child2a_tplPp.template );
			Assert.assertNotNull( child2a_tplPp.template.getHtmlItems() );

			masterFile.setLastModified( masterFile.lastModified() + 1000 );
			child1aFile.setLastModified( child1aFile.lastModified() + 1000 );
			child1bFile.setLastModified( child1bFile.lastModified() + 1000 );
			child2aFile.setLastModified( child2aFile.lastModified() + 1000 );

			Assert.assertTrue( master_tplPp.check_reload() );
			Assert.assertTrue( child1a_tplPp.check_reload() );
			Assert.assertTrue( child1b_tplPp.check_reload() );
			Assert.assertTrue( child2a_tplPp.check_reload() );

			masterFile.setLastModified( masterFile.lastModified() + 1000 );
			child1aFile.setLastModified( child1aFile.lastModified() + 1000 );
			child1bFile.setLastModified( child1bFile.lastModified() + 1000 );
			child2aFile.setLastModified( child2aFile.lastModified() + 1000 );

			Assert.assertTrue( child2a_tplPp.check_reload() );
			Assert.assertTrue( child1b_tplPp.check_reload() );
			Assert.assertFalse( child1a_tplPp.check_reload() );
			Assert.assertFalse( master_tplPp.check_reload() );

			List<TemplatePartBase> parts = child2a_tplPp.templatePartsList;
			System.out.println(parts);
			for ( int i=0; i<parts.size(); ++i ) {
				System.out.println( parts.get( i ).getId() );
				System.out.println( parts.get( i ).getText() );
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

}
