/*
 * Created on 18/03/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.html.HtmlParser;

@RunWith(JUnit4.class)
public class TestHtmlValidator {

	@Test
	public void test_htmlvalidator() {
		List<HtmlItem> html_items;
		String html;

		HtmlValidator htmlValidator = new HtmlValidator();
		Assert.assertNotNull( htmlValidator );

		html_items = null;
		HtmlValidator.validate( html_items );

		html_items = new ArrayList<HtmlItem>();
		HtmlValidator.validate( html_items );

		html = "<?xml version=\"1.0\"?>"
				+ "<!DOCTYPE PARTS SYSTEM \"parts.dtd\">"
				+ "<?xml-stylesheet type=\"text/css\" href=\"xmlpartsstyle.css\"?>"
				+ "<PARTS>"
				+ "   <TITLE>Computer Parts</TITLE>"
				+ "   <PART>"
				+ "      <ITEM>Motherboard</ITEM>"
				+ "      <MANUFACTURER>ASUS</MANUFACTURER>"
				+ "      <MODEL>P3B-F</MODEL>"
				+ "      <COST> 123.00</COST>"
				+ "   </PART>"
				+ "   <PART>"
				+ "      <ITEM>Video Card</ITEM>"
				+ "      <MANUFACTURER>ATI</MANUFACTURER>"
				+ "      <MODEL>All-in-Wonder Pro</MODEL>"
				+ "      <COST> 160.00</COST>"
				+ "   </PART>"
				+ "   <PART>"
				+ "      <ITEM>Sound Card</ITEM>"
				+ "      <MANUFACTURER>Creative Labs</MANUFACTURER>"
				+ "      <MODEL>Sound Blaster Live</MODEL>"
				+ "      <COST> 80.00</COST>"
				+ "   </PART>"
				+ "   <PART>"
				+ "      <ITEMᡋ inch Monitor</ITEM>"
				+ "      <MANUFACTURER>LG Electronics</MANUFACTURER>"
				+ "      <MODEL> 995E</MODEL>"
				+ "      <COST> 290.00</COST>"
				+ "   </PART>"
				+ "</PARTS>";

		HtmlParser htmlParser = new HtmlParser();
		try {
			html_items = htmlParser.parse( new ByteArrayInputStream( html.getBytes( "UTF-8" ) ) );
			HtmlValidator.validate( html_items );
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
