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
public class TestTemplatePartI18N {

	@Test
	public void test_templateparti18n() {
		HtmlItem htmlItem;
		TemplatePartI18N templatePart = null;
		TemplatePartI18N templatePart2 = null;
		TemplatePartI18N templatePart3 = null;

		try {
			/*
			 * 
			 */
			htmlItem = new HtmlTag( "i18n" );
			htmlItem.setAttribute( "text_id", "42" );
			htmlItem.setClosed( true );

			templatePart = TemplatePartBase.getTemplatePartI18N( htmlItem );
			templatePart2 = TemplatePartBase.getTemplatePartI18N( htmlItem );
			Assert.assertTrue( templatePart.htmlItem == templatePart2.htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertTrue( htmlItem == templatePart.htmlItem );

			Assert.assertEquals( "42", templatePart.getText() );
			Assert.assertArrayEquals( "42".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart3 = (TemplatePartI18N)templatePart.clone();
			Assert.assertFalse( templatePart3.htmlItem == templatePart.htmlItem );
			Assert.assertFalse( htmlItem == templatePart3.htmlItem );

			Assert.assertEquals( "42", templatePart3.getText() );
			Assert.assertArrayEquals( "42".getBytes( "UTF-8" ), templatePart3.getBytes() );
			/*
			 * 
			 */
			htmlItem = new HtmlTag( "i18n" );
			htmlItem.setAttribute( "text_id", "42" );
			htmlItem.setClosed( false );

			templatePart = TemplatePartBase.getTemplatePartI18N( htmlItem );
			templatePart2 = TemplatePartBase.getTemplatePartI18N( htmlItem );
			Assert.assertTrue( templatePart.htmlItem == templatePart2.htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertTrue( htmlItem == templatePart.htmlItem );

			Assert.assertEquals( "42", templatePart.getText() );
			Assert.assertArrayEquals( "42".getBytes( "UTF-8" ), templatePart.getBytes() );

			templatePart3 = (TemplatePartI18N)templatePart.clone();
			Assert.assertFalse( templatePart3.htmlItem == templatePart.htmlItem );
			Assert.assertFalse( htmlItem == templatePart3.htmlItem );

			Assert.assertEquals( "42", templatePart3.getText() );
			Assert.assertArrayEquals( "42".getBytes( "UTF-8" ), templatePart3.getBytes() );
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
