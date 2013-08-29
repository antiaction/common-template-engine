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
import com.antiaction.common.templateengine.storage.TemplateStorageManager;

@RunWith(JUnit4.class)
public class TestTemplateBlocks {

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
	public void test_templateblocks() {
		URL url;
		File file;
		url = this.getClass().getClassLoader().getResource("");
		file = new File(getUrlPath(url));

		TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath() );
		TemplateMaster tplMaster = TemplateMaster.getInstance( "default" );
		tplMaster.addTemplateStorage( tplStorMan );
	}

}
