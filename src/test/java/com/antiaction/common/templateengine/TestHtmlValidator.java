/*
 * Created on 18/03/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.html.HtmlItem;

@RunWith(JUnit4.class)
public class TestHtmlValidator {

	@Test
	public void test_htmlvalidator() {
		List<HtmlItem> html_items;

		html_items = null;
		HtmlValidator.validate( html_items );

		html_items = new ArrayList<HtmlItem>();
		HtmlValidator.validate( html_items );
	}

}
