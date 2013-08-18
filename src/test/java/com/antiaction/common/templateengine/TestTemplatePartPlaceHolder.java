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

		try {
			/*
			 * 
			 */
			htmlItem = new HtmlTag( "placeholder" );
			htmlItem.setAttribute( "id", "42" );
			htmlItem.setClosed( true );

			templatePart = TemplatePartBase.getTemplatePartPlaceHolder( htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertEquals( htmlItem, templatePart.htmlItem );

			Assert.assertEquals( "", templatePart.getText() );
			Assert.assertArrayEquals( "".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart.setText( "template text" );

			Assert.assertEquals( "template text", templatePart.getText() );
			Assert.assertArrayEquals( "template text".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart.setBytes( "text template".getBytes() );

			Assert.assertEquals( "text template", templatePart.getText() );
			Assert.assertArrayEquals( "text template".getBytes( "UTF-8" ), templatePart.getBytes() );
			/*
			 * 
			 */
			htmlItem = new HtmlTag( "placeholder" );
			htmlItem.setAttribute( "id", "42" );
			htmlItem.setClosed( false );

			templatePart = TemplatePartBase.getTemplatePartPlaceHolder( htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertEquals( htmlItem, templatePart.htmlItem );

			Assert.assertEquals( "", templatePart.getText() );
			Assert.assertArrayEquals( "".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart.setText( "template text" );

			Assert.assertEquals( "template text", templatePart.getText() );
			Assert.assertArrayEquals( "template text".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart.setBytes( "text template".getBytes() );

			Assert.assertEquals( "text template", templatePart.getText() );
			Assert.assertArrayEquals( "text template".getBytes( "UTF-8" ), templatePart.getBytes() );
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
