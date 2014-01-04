/*
 * Created on 28/08/2013
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
public class TestTemplateMaster {

	public static String getUrlPath(URL url) {
		String path = url.getFile();
		path = path.replaceAll("%5b", "[");
		path = path.replaceAll("%5d", "]");
		return path;
	}

	public static void deleteFile(File file) throws IOException {
		if ( file.exists() ) {
			if ( !file.delete() ) {
				Assert.fail( "Unable to delete file!" );
			}
		}
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
	public void test_templateblocks() {
		URL url;
		File file;
		url = this.getClass().getClassLoader().getResource("");
		file = new File(getUrlPath(url));

		/*
		 * Instance.
		 */
		TemplateStorage tplStor;

		TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath() );
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
