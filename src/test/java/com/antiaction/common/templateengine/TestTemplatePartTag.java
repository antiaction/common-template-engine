/*
 * Created on 16/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.UnsupportedEncodingException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.html.HtmlTag;

@RunWith(JUnit4.class)
public class TestTemplatePartTag {

	@Test
	public void test_templateparttag() {
		HtmlItem htmlItem;
		TemplatePartTag templatePart = null;

		try {
			/*
			 * 
			 */
			htmlItem = new HtmlTag("tag");

			templatePart = TemplatePartBase.getTemplatePartTag( htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertEquals( htmlItem, templatePart.htmlItem );

			Assert.assertEquals( "<tag>", templatePart.getText() );
			Assert.assertArrayEquals( "<tag>".getBytes( "UTF-8" ), templatePart.getBytes() );
			/*
			 * 
			 */
			htmlItem = new HtmlTag("tag");
			htmlItem.setAttribute("attr1", null);

			templatePart = TemplatePartBase.getTemplatePartTag( htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertEquals( htmlItem, templatePart.htmlItem );

			Assert.assertEquals( "<tag attr1>", templatePart.getText() );
			Assert.assertArrayEquals( "<tag attr1>".getBytes( "UTF-8" ), templatePart.getBytes() );
			/*
			 * 
			 */
			htmlItem = new HtmlTag("tag");
			htmlItem.setAttribute("attr1", "value1");

			templatePart = TemplatePartBase.getTemplatePartTag( htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertEquals( htmlItem, templatePart.htmlItem );

			Assert.assertEquals( "<tag attr1=\"value1\">", templatePart.getText() );
			Assert.assertArrayEquals( "<tag attr1=\"value1\">".getBytes( "UTF-8" ), templatePart.getBytes() );
			/*
			 * 
			 */
			htmlItem = new HtmlTag("tag");
			htmlItem.setClosed( true );

			templatePart = TemplatePartBase.getTemplatePartTag( htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertEquals( htmlItem, templatePart.htmlItem );

			Assert.assertEquals( "<tag />", templatePart.getText() );
			Assert.assertArrayEquals( "<tag />".getBytes( "UTF-8" ), templatePart.getBytes() );
			/*
			 * 
			 */
			htmlItem = new HtmlTag("tag");
			htmlItem.setAttribute("attr1", null);
			htmlItem.setClosed( true );

			templatePart = TemplatePartBase.getTemplatePartTag( htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertEquals( htmlItem, templatePart.htmlItem );

			Assert.assertEquals( "<tag attr1 />", templatePart.getText() );
			Assert.assertArrayEquals( "<tag attr1 />".getBytes( "UTF-8" ), templatePart.getBytes() );
			/*
			 * 
			 */
			htmlItem = new HtmlTag("tag");
			htmlItem.setAttribute("attr1", "value1");
			htmlItem.setClosed( true );

			templatePart = TemplatePartBase.getTemplatePartTag( htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertEquals( htmlItem, templatePart.htmlItem );

			Assert.assertEquals( "<tag attr1=\"value1\" />", templatePart.getText() );
			Assert.assertArrayEquals( "<tag attr1=\"value1\" />".getBytes( "UTF-8" ), templatePart.getBytes() );
		}
		catch (UnsupportedEncodingException e) {
			Assert.fail( "Unexpected exception!" );
		}

		try {
			templatePart.setText( "yo!" );
			Assert.fail( "Exception expected!" );
		}
		catch (UnsupportedOperationException e) {
		}

		try {
			templatePart.setBytes( "yo!".getBytes() );
			Assert.fail( "Exception expected!" );
		}
		catch (UnsupportedOperationException e) {
		}
	}

}
