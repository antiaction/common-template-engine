/*
 * Created on 18/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URL;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.templateengine.storage.TemplateFileStorageManager;
import com.antiaction.common.templateengine.storage.TemplateStorage;
import com.antiaction.common.templateengine.storage.TemplateStorageManager;

@RunWith(JUnit4.class)
public class TestTemplate {

	public static String getUrlPath(URL url) {
		String path = url.getFile();
		path = path.replaceAll("%5b", "[");
		path = path.replaceAll("%5d", "]");
		return path;
	}

	public static void saveBytes(File file, byte[] bytes) throws IOException {
		if ( file.exists() ) {
			if ( !file.delete() ) {
				Assert.fail( "Unable to delete file!" );
			}
		}
		RandomAccessFile raf = new RandomAccessFile( file, "rw" );
		raf.seek( 0L );
		raf.setLength( 0L );
		raf.write( bytes );
		raf.close();
	}

	@Test
	public void test_template() {
		URL url;
		File file;
		url = this.getClass().getClassLoader().getResource("");
		file = new File(getUrlPath(url));

		TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath() );
		TemplateMaster tplMaster = TemplateMaster.getInstance( "default" );
		tplMaster.addTemplateStorage( tplStorMan );

		/*
		 * Missing.
		 */

		Template tpl = tplMaster.getTemplate( "missing.html" );
		Assert.assertNotNull( tpl );
		Assert.assertNull( tpl.getHtmlItems() );

		tpl.check_reload();
		Assert.assertNull( tpl.getHtmlItems() );

		/*
		 * Template.
		 */

		tpl = tplMaster.getTemplate( "templatefile.html" );
		Assert.assertNotNull( tpl );
		Assert.assertNotNull( tpl.getHtmlItems() );

		tpl.check_reload();
		Assert.assertNotNull( tpl.getHtmlItems() );
	}

	@Test
	public void test_template_master() {
		URL url;
		File file;
		url = this.getClass().getClassLoader().getResource("");
		file = new File(getUrlPath(url));

		try {
			TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath() );
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

			saveBytes( masterFile, master.getBytes() );
			saveBytes( child1aFile, child1a.getBytes() );
			saveBytes( child1bFile, child1b.getBytes() );
			saveBytes( child2aFile, child2a.getBytes() );

			Template master_tpl = tplMaster.getTemplate( "masterfile.html" );
			Template child1a_tpl = tplMaster.getTemplate( "child1afile.html" );
			Template child1b_tpl = tplMaster.getTemplate( "child1bfile.html" );
			Template child2a_tpl = tplMaster.getTemplate( "child2afile.html" );

			Assert.assertFalse( master_tpl.check_reload() );
			Assert.assertFalse( child1a_tpl.check_reload() );
			Assert.assertFalse( child1b_tpl.check_reload() );
			Assert.assertFalse( child2a_tpl.check_reload() );

			Assert.assertNotNull( master_tpl );
			Assert.assertNotNull( master_tpl.getHtmlItems() );
			Assert.assertNotNull( child1a_tpl );
			Assert.assertNotNull( child1a_tpl.getHtmlItems() );
			Assert.assertNotNull( child1b_tpl );
			Assert.assertNotNull( child1b_tpl.getHtmlItems() );
			Assert.assertNotNull( child2a_tpl );
			Assert.assertNotNull( child2a_tpl.getHtmlItems() );

			masterFile.setLastModified( masterFile.lastModified() + 1000 );
			child1aFile.setLastModified( child1aFile.lastModified() + 1000 );
			child1bFile.setLastModified( child1bFile.lastModified() + 1000 );
			child2aFile.setLastModified( child2aFile.lastModified() + 1000 );

			Assert.assertTrue( master_tpl.check_reload() );
			Assert.assertTrue( child1a_tpl.check_reload() );
			Assert.assertTrue( child1b_tpl.check_reload() );
			Assert.assertTrue( child2a_tpl.check_reload() );

			masterFile.setLastModified( masterFile.lastModified() + 1000 );
			child1aFile.setLastModified( child1aFile.lastModified() + 1000 );
			child1bFile.setLastModified( child1bFile.lastModified() + 1000 );
			child2aFile.setLastModified( child2aFile.lastModified() + 1000 );

			Assert.assertTrue( child2a_tpl.check_reload() );
			Assert.assertTrue( child1b_tpl.check_reload() );
			Assert.assertFalse( child1a_tpl.check_reload() );
			Assert.assertFalse( master_tpl.check_reload() );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

}
