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

@RunWith(JUnit4.class)
public class TestTemplatePartStatic {

	@Test
	public void test_templatepartstatic() {
		TemplatePartStatic templatePart = null;
		TemplatePartStatic templatePart2 = null;

		try {
			/*
			 * 
			 */
			templatePart = TemplatePartBase.getTemplatePartStatic( "static" );
			Assert.assertNotNull( templatePart );
			Assert.assertNull( templatePart.htmlItem );

			Assert.assertEquals( "static", templatePart.getText() );
			Assert.assertArrayEquals( "static".getBytes(), templatePart.getBytes() );

			templatePart2 = (TemplatePartStatic)templatePart.clone();
			Assert.assertEquals( "static", templatePart2.getText() );
			Assert.assertArrayEquals( "static".getBytes(), templatePart2.getBytes() );
			/*
			 * 
			 */
			templatePart = TemplatePartBase.getTemplatePartStatic( "acid".getBytes( "UTF-8" ) );
			Assert.assertNotNull( templatePart );
			Assert.assertNull( templatePart.htmlItem );

			Assert.assertEquals( "acid", templatePart.getText() );
			Assert.assertArrayEquals( "acid".getBytes(), templatePart.getBytes() );

			templatePart2 = (TemplatePartStatic)templatePart.clone();
			Assert.assertEquals( "acid", templatePart2.getText() );
			Assert.assertArrayEquals( "acid".getBytes(), templatePart2.getBytes() );
			/*
			 * 
			 */
			templatePart = TemplatePartBase.getTemplatePartStatic( (String)null );
			Assert.assertNotNull( templatePart );
			Assert.assertNull( templatePart.htmlItem );

			Assert.assertNull( templatePart.getText() );
			Assert.assertNull( templatePart.getBytes() );

			templatePart2 = (TemplatePartStatic)templatePart.clone();
			Assert.assertNull( templatePart2.getText() );
			Assert.assertNull( templatePart2.getBytes() );
			/*
			 * 
			 */
			templatePart = TemplatePartBase.getTemplatePartStatic( (byte[])null );
			Assert.assertNotNull( templatePart );
			Assert.assertNull( templatePart.htmlItem );

			Assert.assertNull( templatePart.getText() );
			Assert.assertNull( templatePart.getBytes() );

			templatePart2 = (TemplatePartStatic)templatePart.clone();
			Assert.assertNull( templatePart2.getText() );
			Assert.assertNull( templatePart2.getBytes() );
		}
		catch (UnsupportedOperationException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
		catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
