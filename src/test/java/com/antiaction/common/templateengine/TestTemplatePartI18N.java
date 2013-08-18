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

		htmlItem = new HtmlTag( "i18n" );
		htmlItem.setAttribute( "text_id", "42" );
		htmlItem.setClosed( true );

		try {
			/*
			 * 
			 */
			htmlItem = new HtmlTag( "i18n" );
			htmlItem.setAttribute( "text_id", "42" );
			htmlItem.setClosed( true );

			templatePart = TemplatePartBase.getTemplatePartI18N( htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertEquals( htmlItem, templatePart.htmlItem );

			Assert.assertEquals( "42", templatePart.getText() );
			Assert.assertArrayEquals( "42".getBytes( "UTF-8" ), templatePart.getBytes() );
			/*
			 * 
			 */
			htmlItem = new HtmlTag( "i18n" );
			htmlItem.setAttribute( "text_id", "42" );
			htmlItem.setClosed( false );

			templatePart = TemplatePartBase.getTemplatePartI18N( htmlItem );
			Assert.assertNotNull( templatePart );
			Assert.assertEquals( htmlItem, templatePart.htmlItem );

			Assert.assertEquals( "42", templatePart.getText() );
			Assert.assertArrayEquals( "42".getBytes( "UTF-8" ), templatePart.getBytes() );
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
