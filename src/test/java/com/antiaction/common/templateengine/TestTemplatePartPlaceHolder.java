/*
 * Created on 17/08/2013
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
public class TestTemplatePartPlaceHolder {

	@Test
	public void test_templatepartplaceholder() {
		HtmlItem htmlItem;
		TemplatePartPlaceHolder templatePart;
		TemplatePartPlaceHolder templatePart2;
		TemplatePartPlaceHolder templatePart3;

		try {
			/*
			 * 
			 */
			htmlItem = new HtmlTag( "placeholder" );
			htmlItem.setAttribute( "id", "42" );
			htmlItem.setClosed( true );

			templatePart = TemplatePartBase.getTemplatePartPlaceHolder( htmlItem );
			templatePart2 = TemplatePartBase.getTemplatePartPlaceHolder( htmlItem );
			Assert.assertTrue( templatePart.htmlItem == templatePart2.htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertTrue( htmlItem == templatePart.htmlItem );

			Assert.assertEquals( "", templatePart.getText() );
			Assert.assertArrayEquals( "".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart3 = (TemplatePartPlaceHolder)templatePart.clone();
			Assert.assertFalse( templatePart3.htmlItem == templatePart.htmlItem );
			Assert.assertFalse( htmlItem == templatePart3.htmlItem );

			Assert.assertEquals( "", templatePart3.getText() );
			Assert.assertArrayEquals( "".getBytes( "UTF-8" ), templatePart3.getBytes() );

			templatePart.setText( "template text" );

			Assert.assertEquals( "template text", templatePart.getText() );
			Assert.assertArrayEquals( "template text".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart3 = (TemplatePartPlaceHolder)templatePart.clone();
			Assert.assertFalse( templatePart3.htmlItem == templatePart.htmlItem );
			Assert.assertFalse( htmlItem == templatePart3.htmlItem );

			Assert.assertEquals( "template text", templatePart3.getText() );
			Assert.assertArrayEquals( "template text".getBytes( "UTF-8" ), templatePart3.getBytes() );

			templatePart.setBytes( "text template".getBytes() );

			Assert.assertEquals( "text template", templatePart.getText() );
			Assert.assertArrayEquals( "text template".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart3 = (TemplatePartPlaceHolder)templatePart.clone();
			Assert.assertFalse( templatePart3.htmlItem == templatePart.htmlItem );
			Assert.assertFalse( htmlItem == templatePart3.htmlItem );

			Assert.assertEquals( "text template", templatePart3.getText() );
			Assert.assertArrayEquals( "text template".getBytes( "UTF-8" ), templatePart3.getBytes() );
			/*
			 * 
			 */
			htmlItem = new HtmlTag( "placeholder" );
			htmlItem.setAttribute( "id", "42" );
			htmlItem.setClosed( false );

			templatePart = TemplatePartBase.getTemplatePartPlaceHolder( htmlItem );
			templatePart2 = TemplatePartBase.getTemplatePartPlaceHolder( htmlItem );
			Assert.assertTrue( templatePart.htmlItem == templatePart2.htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertTrue( htmlItem == templatePart.htmlItem );

			Assert.assertEquals( "", templatePart.getText() );
			Assert.assertArrayEquals( "".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart3 = (TemplatePartPlaceHolder)templatePart.clone();
			Assert.assertFalse( templatePart3.htmlItem == templatePart.htmlItem );
			Assert.assertFalse( htmlItem == templatePart3.htmlItem );

			Assert.assertEquals( "", templatePart3.getText() );
			Assert.assertArrayEquals( "".getBytes( "UTF-8" ), templatePart3.getBytes() );

			templatePart.setText( "template text" );

			Assert.assertEquals( "template text", templatePart.getText() );
			Assert.assertArrayEquals( "template text".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart3 = (TemplatePartPlaceHolder)templatePart.clone();
			Assert.assertFalse( templatePart3.htmlItem == templatePart.htmlItem );
			Assert.assertFalse( htmlItem == templatePart3.htmlItem );

			Assert.assertEquals( "template text", templatePart3.getText() );
			Assert.assertArrayEquals( "template text".getBytes( "UTF-8" ), templatePart3.getBytes() );

			templatePart.setBytes( "text template".getBytes() );

			Assert.assertEquals( "text template", templatePart.getText() );
			Assert.assertArrayEquals( "text template".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart3 = (TemplatePartPlaceHolder)templatePart.clone();
			Assert.assertFalse( templatePart3.htmlItem == templatePart.htmlItem );
			Assert.assertFalse( htmlItem == templatePart3.htmlItem );

			Assert.assertEquals( "text template", templatePart3.getText() );
			Assert.assertArrayEquals( "text template".getBytes( "UTF-8" ), templatePart3.getBytes() );
		}
		catch (UnsupportedOperationException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

}
