/*
 * Created on 23/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine.storage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.templateengine.TestUtils;

@RunWith(JUnit4.class)
public class TestTemplateFileStorage {

	@Test
	public void test_templatefilestorage() {
		long last_modified;
		long last_file_length;
		List<HtmlItem> htmlItems;

		File resourceRootFile = TestUtils.getTestResourceFile("");

		try {
			File templateFile = new File( resourceRootFile.getParent(), "temp.html" );
			if ( templateFile.exists() ) {
				if ( !templateFile.delete() ) {
					Assert.fail( "Unable to delete file!" );
				}
			}
			RandomAccessFile raf = new RandomAccessFile( templateFile, "rw" );
			raf.write( "templatefile org".getBytes() );
			raf.close();
			/*
			 * File.
			 */
			TemplateFileStorage tplStor = new TemplateFileStorage( templateFile, "UTF-8" );
			Assert.assertNotNull( tplStor );

			Assert.assertTrue( tplStor.exists() );

			last_modified = tplStor.lastModified();
			last_file_length = tplStor.length();

			htmlItems = tplStor.getHtmlItems();
			Assert.assertNotNull( htmlItems );

			Assert.assertEquals( templateFile.lastModified(), last_modified );
			Assert.assertEquals( templateFile.length(), last_file_length );

			Assert.assertArrayEquals( "templatefile org".getBytes(), tplStor.html_raw_bytes );
			Assert.assertEquals( tplStor.last_modified, tplStor.lastModified() );
			Assert.assertEquals( tplStor.last_file_length, tplStor.length() );
			Assert.assertEquals( tplStor.html_items_cached, htmlItems );

			Assert.assertFalse( tplStor.checkReload() );

			Assert.assertTrue( tplStor.exists() );

			htmlItems = tplStor.getHtmlItems();
			Assert.assertNotNull( htmlItems );

			Assert.assertEquals( last_modified, tplStor.lastModified() );
			Assert.assertEquals( last_file_length, tplStor.length() );

			Assert.assertArrayEquals( "templatefile org".getBytes(), tplStor.html_raw_bytes );
			Assert.assertEquals( tplStor.last_modified, tplStor.lastModified() );
			Assert.assertEquals( tplStor.last_file_length, tplStor.length() );
			Assert.assertEquals( tplStor.html_items_cached, htmlItems );
			/*
			 * Updated file.
			 */
			raf = new RandomAccessFile( templateFile, "rw" );
			raf.write( "templatefile new".getBytes() );
			raf.close();

			// Last modified on disk is not as accurate.
			templateFile.setLastModified( templateFile.lastModified() + 1000 );

			Assert.assertTrue( tplStor.checkReload() );

			Assert.assertTrue( tplStor.exists() );

			last_modified = tplStor.lastModified();
			last_file_length = tplStor.length();

			htmlItems = tplStor.getHtmlItems();
			Assert.assertNotNull( htmlItems );

			Assert.assertEquals( last_modified, tplStor.lastModified() );
			Assert.assertEquals( last_file_length, tplStor.length() );

			Assert.assertArrayEquals( "templatefile new".getBytes(), tplStor.html_raw_bytes );
			Assert.assertEquals( tplStor.last_modified, tplStor.lastModified() );
			Assert.assertEquals( tplStor.last_file_length, tplStor.length() );
			Assert.assertEquals( tplStor.html_items_cached, htmlItems );
			/*
			 * New file old date.
			 */
			raf = new RandomAccessFile( templateFile, "rw" );
			raf.write( "templatefile length".getBytes() );
			raf.close();

			templateFile.setLastModified( last_modified );

			Assert.assertTrue( tplStor.checkReload() );

			Assert.assertTrue( tplStor.exists() );

			last_modified = tplStor.lastModified();
			last_file_length = tplStor.length();

			htmlItems = tplStor.getHtmlItems();
			Assert.assertNotNull( htmlItems );

			Assert.assertEquals( last_modified, tplStor.lastModified() );
			Assert.assertEquals( last_file_length, tplStor.length() );

			Assert.assertArrayEquals( "templatefile length".getBytes(), tplStor.html_raw_bytes );
			Assert.assertEquals( tplStor.last_modified, tplStor.lastModified() );
			Assert.assertEquals( tplStor.last_file_length, tplStor.length() );
			Assert.assertEquals( tplStor.html_items_cached, htmlItems );
			/*
			 * File deleted.
			 */
			if ( !templateFile.delete() ) {
				Assert.fail( "Unable to delete file!" );
			}
			Assert.assertTrue( tplStor.exists() );

			htmlItems = tplStor.getHtmlItems();
			Assert.assertNotNull( htmlItems );

			Assert.assertArrayEquals( "templatefile length".getBytes(), tplStor.html_raw_bytes );
			Assert.assertEquals( last_modified, tplStor.lastModified() );
			Assert.assertEquals( last_file_length, tplStor.length() );
			Assert.assertEquals( tplStor.last_modified, tplStor.lastModified() );
			Assert.assertEquals( tplStor.last_file_length, tplStor.length() );
			Assert.assertEquals( tplStor.html_items_cached, htmlItems );
			/*
			 * checkReload no file.
			 */
			Assert.assertFalse( tplStor.checkReload() );

			Assert.assertFalse( tplStor.exists() );

			htmlItems = tplStor.getHtmlItems();
			Assert.assertNull( htmlItems );

			Assert.assertNull( tplStor.html_raw_bytes );
			Assert.assertEquals( -1, tplStor.lastModified() );
			Assert.assertEquals( -1, tplStor.length() );
			Assert.assertEquals( tplStor.last_modified, tplStor.lastModified() );
			Assert.assertEquals( tplStor.last_file_length, tplStor.length() );
			Assert.assertNull( htmlItems );
			/*
			 * Directory.
			 */
			templateFile.mkdir();

			Assert.assertFalse( tplStor.checkReload() );

			Assert.assertFalse( tplStor.exists() );

			htmlItems = tplStor.getHtmlItems();
			Assert.assertNull( htmlItems );

			Assert.assertNull( tplStor.html_raw_bytes );
			Assert.assertEquals( -1, tplStor.lastModified() );
			Assert.assertEquals( -1, tplStor.length() );
			Assert.assertEquals( tplStor.last_modified, tplStor.lastModified() );
			Assert.assertEquals( tplStor.last_file_length, tplStor.length() );
			Assert.assertNull( htmlItems );
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

}
