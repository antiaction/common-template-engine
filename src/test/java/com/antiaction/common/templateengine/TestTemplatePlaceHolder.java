/*
 * Created on 17/03/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestTemplatePlaceHolder {

	@Test
	public void test_templateplaceholder() {
		TemplatePlaceHolder tplPlaceHolder;

		tplPlaceHolder = TemplatePlaceBase.getTemplatePlaceHolder( null );

		Assert.assertNotNull( tplPlaceHolder );
		Assert.assertEquals( TemplatePlaceBase.PH_PLACEHOLDER, tplPlaceHolder.type );
		Assert.assertNull( tplPlaceHolder.templatePart );
		Assert.assertNull( tplPlaceHolder.htmlItem );
		Assert.assertEquals( "placeholder", tplPlaceHolder.tagName );
		Assert.assertNull( tplPlaceHolder.idName );

		tplPlaceHolder = TemplatePlaceBase.getTemplatePlaceHolder( "idName" );

		Assert.assertNotNull( tplPlaceHolder );
		Assert.assertEquals( TemplatePlaceBase.PH_PLACEHOLDER, tplPlaceHolder.type );
		Assert.assertNull( tplPlaceHolder.templatePart );
		Assert.assertNull( tplPlaceHolder.htmlItem );
		Assert.assertEquals( "placeholder", tplPlaceHolder.tagName );
		Assert.assertEquals( "idName", tplPlaceHolder.idName );

		tplPlaceHolder.setText( null );
		tplPlaceHolder.setText( "le texte" );
		tplPlaceHolder.setText( "<img src=\"monkey.gif\" alt=\"look ze cute monkeyz\" />" );
	}

}
