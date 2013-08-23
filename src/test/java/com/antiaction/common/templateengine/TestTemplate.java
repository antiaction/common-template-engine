/*
 * Created on 18/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.File;
import java.net.URL;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestTemplate {

	public String getUrlPath(URL url) {
		String path = url.getFile();
		path = path.replaceAll("%5b", "[");
		path = path.replaceAll("%5d", "]");
		return path;
	}

	@Test
	public void test_template() {
		URL url;
		File file;
		url = this.getClass().getClassLoader().getResource("");
		file = new File(getUrlPath(url));
	}

}
