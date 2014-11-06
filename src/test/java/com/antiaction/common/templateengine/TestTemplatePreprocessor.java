/*
 * Created on 07/06/2014
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.templateengine.storage.TemplateFileStorageManager;
import com.antiaction.common.templateengine.storage.TemplateStorageManager;

@RunWith(JUnit4.class)
public class TestTemplatePreprocessor {

	public static String getUrlPath(URL url) {
		String path = url.getFile();
		path = path.replaceAll("%5b", "[");
		path = path.replaceAll("%5d", "]");
		return path;
	}

	@Test
	@Ignore
	public void test_templatepreprocessor() {
		URL url;
		File file;
		url = this.getClass().getClassLoader().getResource("");
		file = new File(getUrlPath(url));

		TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath(), "UTF-8" );
		TemplateMaster tplMaster = TemplateMaster.getInstance( "default" );
		tplMaster.addTemplateStorage( tplStorMan );

		try {
			TemplatePreprocessor tplPp = tplMaster.getTemplatePreprocessor( "templatefile.html", new HashMap<String, Set<String>>() );
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

}
