/*
 * Created on 17/03/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.html.HtmlParser;

@RunWith(JUnit4.class)
public class TestTemplatePlaceTag {

	@Test
	public void test_templaceplacetag() {
		TemplatePlaceTag tplPlaceTag;

		try {
			tplPlaceTag = TemplatePlaceTag.getTemplatePlaceTag( null, null );

			Assert.assertNotNull( tplPlaceTag );
			Assert.assertEquals( TemplatePlaceBase.PH_TAG, tplPlaceTag.type );
			Assert.assertNull( tplPlaceTag.templatePart );
			Assert.assertNull( tplPlaceTag.htmlItem );
			Assert.assertNull( tplPlaceTag.tagName );
			Assert.assertNull( tplPlaceTag.idName );

			Assert.assertNull( tplPlaceTag.getAttributes() );
			Assert.assertNull( tplPlaceTag.getAttribute( "tagName" ) );
			Assert.assertNull( tplPlaceTag.setAttribute( "name", "value" ) );
			Assert.assertNull( tplPlaceTag.removeAttribute( "name" ) );

			tplPlaceTag = TemplatePlaceTag.getTemplatePlaceTag( "tagName", "idName" );

			Assert.assertNotNull( tplPlaceTag );
			Assert.assertEquals( TemplatePlaceBase.PH_TAG, tplPlaceTag.type );
			Assert.assertNull( tplPlaceTag.templatePart );
			Assert.assertNull( tplPlaceTag.htmlItem );
			Assert.assertEquals( "tagName", tplPlaceTag.tagName );
			Assert.assertEquals( "idName", tplPlaceTag.idName );

			Assert.assertNull( tplPlaceTag.getAttributes() );
			Assert.assertNull( tplPlaceTag.getAttribute( "tagName" ) );
			Assert.assertNull( tplPlaceTag.setAttribute( "name", "value" ) );
			Assert.assertNull( tplPlaceTag.removeAttribute( "name" ) );

			String html = "<taggo attributo=\"valueo\">";

			ByteArrayInputStream is = new ByteArrayInputStream( html.getBytes() );

			// Parse html into a List of html items.
			HtmlParser htmlParser = new HtmlParser();
			List<HtmlItem> html_items = htmlParser.parse( is );

			Assert.assertNotNull( html_items );
			Assert.assertEquals( 1, html_items.size() );

			tplPlaceTag.htmlItem = html_items.get( 0 );

			Assert.assertNotNull( tplPlaceTag.getAttributes() );
			Assert.assertEquals( 1, tplPlaceTag.getAttributes().size() );

			Assert.assertNull( tplPlaceTag.getAttribute( "tagName" ) );
			Assert.assertNull( tplPlaceTag.removeAttribute( "name" ) );
			Assert.assertNull( tplPlaceTag.setAttribute( "name", "value" ) );

			Assert.assertNotNull( tplPlaceTag.getAttributes() );
			Assert.assertEquals( 2, tplPlaceTag.getAttributes().size() );

			Assert.assertEquals( "value", tplPlaceTag.removeAttribute( "name" ) );
			Assert.assertNull( tplPlaceTag.removeAttribute( "name" ) );

			Assert.assertNotNull( tplPlaceTag.getAttributes() );
			Assert.assertEquals( 1, tplPlaceTag.getAttributes().size() );

			Assert.assertEquals( "valueo", tplPlaceTag.getAttribute( "attributo" ) );
			Assert.assertEquals( "valueo", tplPlaceTag.removeAttribute( "attributo" ) );
			Assert.assertNull( tplPlaceTag.setAttribute( "attributo", "valueo2" ) );
			Assert.assertEquals( "valueo2", tplPlaceTag.getAttribute( "attributo" ) );
			Assert.assertEquals( "valueo2", tplPlaceTag.removeAttribute( "attributo" ) );

			Assert.assertNotNull( tplPlaceTag.getAttributes() );
			Assert.assertEquals( 0, tplPlaceTag.getAttributes().size() );
		}
		catch (IOException e) {
		}
	}

}
