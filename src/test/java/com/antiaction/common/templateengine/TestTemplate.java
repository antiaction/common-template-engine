/*
 * Created on 18/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.html.HtmlItem;
import com.antiaction.common.templateengine.storage.TemplateFileStorageManager;
import com.antiaction.common.templateengine.storage.TemplateStorageManager;

@RunWith(JUnit4.class)
public class TestTemplate {

	@Test
	public void test_template() {
		File file = TestUtils.getTestResourceFile("");

		TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath(), "UTF-8" );
		TemplateMaster tplMaster = TemplateMaster.getInstance( "default" );
		tplMaster.addTemplateStorage( tplStorMan );

		/*
		 * Missing.
		 */

		Template tpl = tplMaster.getTemplate( "missing.html" );
		Assert.assertNotNull( tpl );
		Assert.assertNull( tpl.getHtmlItems() );

		tpl.check_reload();
		Assert.assertNull( tpl.getHtmlItems() );

		/*
		 * Template.
		 */

		tpl = tplMaster.getTemplate( "templatefile.html" );
		Assert.assertNotNull( tpl );
		Assert.assertNotNull( tpl.getHtmlItems() );

		tpl.check_reload();
		Assert.assertNotNull( tpl.getHtmlItems() );
	}

	@Test
	public void test_template_master() {
		File file = TestUtils.getTestResourceFile("");

		try {
			TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath(), "UTF-8" );
			TemplateMaster tplMaster = TemplateMaster.getInstance( "default" );
			tplMaster.addTemplateStorage( tplStorMan );

			String master;
			String child1a;
			String child1b;
			String child2a;

			master = "<html>";
			master += "templatemaster";
			master += "<placeholder id=\"ph1\" />";
			master += "</html>";

			child1a = "<@master file=\"masterfile.html\">";
			child1a += "<place placeholder=\"ph1\">";
			child1a += "b41a";
			child1a += "<placeholder id=\"ph1\" />";
			child1a += "after1a";
			child1a += "</place>";

			child1b = "<@master file=\"masterfile.html\">";
			child1b += "<place placeholder=\"ph1\">";
			child1b += "b41b";
			child1b += "<placeholder id=\"ph1\" />";
			child1b += "after1b";
			child1b += "</place>";

			child2a = "<@master file=\"child1afile.html\">";
			child2a += "<place placeholder=\"ph1\">";
			child2a += "b42a";
			child2a += "<placeholder id=\"ph2\" />";
			child2a += "after2a";
			child2a += "</place>";

			File masterFile = new File( file, "masterfile.html" );
			File child1aFile = new File( file, "child1afile.html" );
			File child1bFile = new File( file, "child1bfile.html" );
			File child2aFile = new File( file, "child2afile.html" );

			TestUtils.saveBytes( masterFile, master.getBytes() );
			TestUtils.saveBytes( child1aFile, child1a.getBytes() );
			TestUtils.saveBytes( child1bFile, child1b.getBytes() );
			TestUtils.saveBytes( child2aFile, child2a.getBytes() );

			Template master_tpl = tplMaster.getTemplate( "masterfile.html" );
			Template child1a_tpl = tplMaster.getTemplate( "child1afile.html" );
			Template child1b_tpl = tplMaster.getTemplate( "child1bfile.html" );
			Template child2a_tpl = tplMaster.getTemplate( "child2afile.html" );

			Assert.assertFalse( master_tpl.check_reload() );
			Assert.assertFalse( child1a_tpl.check_reload() );
			Assert.assertFalse( child1b_tpl.check_reload() );
			Assert.assertFalse( child2a_tpl.check_reload() );

			Assert.assertNotNull( master_tpl );
			Assert.assertNotNull( master_tpl.getHtmlItems() );
			Assert.assertNotNull( child1a_tpl );
			Assert.assertNotNull( child1a_tpl.getHtmlItems() );
			Assert.assertNotNull( child1b_tpl );
			Assert.assertNotNull( child1b_tpl.getHtmlItems() );
			Assert.assertNotNull( child2a_tpl );
			Assert.assertNotNull( child2a_tpl.getHtmlItems() );

			masterFile.setLastModified( masterFile.lastModified() + 1000 );
			child1aFile.setLastModified( child1aFile.lastModified() + 1000 );
			child1bFile.setLastModified( child1bFile.lastModified() + 1000 );
			child2aFile.setLastModified( child2aFile.lastModified() + 1000 );

			Assert.assertTrue( master_tpl.check_reload() );
			Assert.assertTrue( child1a_tpl.check_reload() );
			Assert.assertTrue( child1b_tpl.check_reload() );
			Assert.assertTrue( child2a_tpl.check_reload() );

			masterFile.setLastModified( masterFile.lastModified() + 1000 );
			child1aFile.setLastModified( child1aFile.lastModified() + 1000 );
			child1bFile.setLastModified( child1bFile.lastModified() + 1000 );
			child2aFile.setLastModified( child2aFile.lastModified() + 1000 );

			Assert.assertTrue( child2a_tpl.check_reload() );
			Assert.assertTrue( child1b_tpl.check_reload() );
			Assert.assertFalse( child1a_tpl.check_reload() );
			Assert.assertFalse( master_tpl.check_reload() );
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

	@Test
	public void test_template_prepare() {
		File file = TestUtils.getTestResourceFile("");

		try {
			TemplateStorageManager tplStorMan = TemplateFileStorageManager.getInstance( file.getPath(), "UTF-8" );
			TemplateMaster tplMaster = TemplateMaster.getInstance( "default" );
			tplMaster.addTemplateStorage( tplStorMan );

			String template;
			File templateFile;

			Template template_tpl;
			List<HtmlItem> htmlItems;
			int idx;
			int reductions;
			Object[][] expected;

			/*
			 * No template.
			 */

			template_tpl = tplMaster.getTemplate( "unknown.html" );

			htmlItems = template_tpl.getHtmlItems();
			Assert.assertNull( htmlItems );

			reductions = template_tpl.reduce( null );
			Assert.assertEquals( 0, reductions );

			reductions = template_tpl.reduce( null );
			Assert.assertEquals( 0, reductions );

			/*
			 * No tags.
			 */

			template = "<@hola>";
			template += "<html lang=\"en\">";
			template += "<!-- comment -->";
			template += "<!exclamation!>";
			template += "<?processing?>";
			template += "<input name=\"1\" />";
			template += "<input name=\"2\" />";
			template += "<input id=\"3\" />";
			template += "<input id=\"4\" />";
			template += "<a name=\"5\">";
			template += "<a id=\"6\">";
			template += "templatemaster";
			template += "<i18n text_id=\"id\" />";
			template += "<placeholder id=\"ph1\" />";
			template += "<placeholder id=\"ph2\" />";
			template += "</html>";
			template += "<placeholder id=\"ph3\" />";

			templateFile = new File( file, "templatepreparefile.html" );
			TestUtils.saveBytes( templateFile, template.getBytes() );

			template_tpl = tplMaster.getTemplate( "templatepreparefile.html" );

			expected = new Object[][] {
					//{ HtmlItem.T_DIRECTIVE, "<@hola>" },
					{ HtmlItem.T_DIRECTIVE, null },
					{ HtmlItem.T_TAG, "<html lang=\"en\">" },
					{ HtmlItem.T_COMMENT, "<!-- comment -->" },
					{ HtmlItem.T_EXCLAMATION, "<!exclamation!>" },
					{ HtmlItem.T_PROCESSING, "<?processing?>" },
					{ HtmlItem.T_TAG, "<input name=\"1\" />" },
					{ HtmlItem.T_TAG, "<input name=\"2\" />" },
					{ HtmlItem.T_TAG, "<input id=\"3\" />" },
					{ HtmlItem.T_TAG, "<input id=\"4\" />" },
					{ HtmlItem.T_TAG, "<a name=\"5\">" },
					{ HtmlItem.T_TAG, "<a id=\"6\">" },
					{ HtmlItem.T_TEXT, "templatemaster" },
					{ HtmlItem.T_TAG, "<i18n text_id=\"id\" />"},
					{ HtmlItem.T_TAG, "<placeholder id=\"ph1\" />" },
					{ HtmlItem.T_TAG, "<placeholder id=\"ph2\" />" },
					{ HtmlItem.T_ENDTAG, "</html>" },
					{ HtmlItem.T_TAG, "<placeholder id=\"ph3\" />" }
			};

			htmlItems = template_tpl.getHtmlItems();

			Assert.assertEquals( expected.length, htmlItems.size() );
			for ( int i=0; i<expected.length; ++i ) {
				Assert.assertEquals( expected[ i ][ 0 ], htmlItems.get( i ).getType() );
				Assert.assertEquals( expected[ i ][ 1 ], htmlItems.get( i ).getText() );
			}

			reductions = template_tpl.reduce( null );
			Assert.assertEquals( 13, reductions );

			reductions = template_tpl.reduce( null );
			Assert.assertEquals( 2, reductions );

			expected = new Object[][] {
					{ HtmlItem.T_TEXT, "<html lang=\"en\"><!-- comment --><!exclamation!><?processing?><input name=\"1\" /><input name=\"2\" /><input id=\"3\" /><input id=\"4\" /><a name=\"5\"><a id=\"6\">templatemaster" },
					{ HtmlItem.T_TAG, "<i18n text_id=\"id\" />"},
					{ HtmlItem.T_TAG, "<placeholder id=\"ph1\" />" },
					{ HtmlItem.T_TAG, "<placeholder id=\"ph2\" />" },
					{ HtmlItem.T_TEXT, "</html>" },
					{ HtmlItem.T_TAG, "<placeholder id=\"ph3\" />" }
			};

			htmlItems = template_tpl.getHtmlItems();
			Assert.assertEquals( expected.length, htmlItems.size() );
			for ( int i=0; i<expected.length; ++i ) {
				Assert.assertEquals( expected[ i ][ 0 ], htmlItems.get( i ).getType() );
				Assert.assertEquals( expected[ i ][ 1 ], htmlItems.get( i ).getText() );
			}

			/*
			 * Tags.
			 */

			template = "<html lang=\"en\">";
			template += "<!-- comment -->";
			template += "<!exclamation!>";
			template += "<?processing?>";
			template += "<input name=\"1\" />";
			template += "<input name=\"2\" />";
			template += "<input id=\"3\" />";
			template += "<input id=\"4\" />";
			template += "<a name=\"5\">";
			template += "<a id=\"6\">";
			template += "templatemaster";
			template += "<i18n text_id=\"id\" />";
			template += "<placeholder id=\"ph1\" />";
			template += "<placeholder id=\"ph2\" />";
			template += "</html>";

			templateFile = new File( file, "templatepreparefile.html" );
			TestUtils.saveBytes( templateFile, template.getBytes() );

			template_tpl = tplMaster.getTemplate( "templatepreparefile.html" );

			expected = new Object[][] {
					{ HtmlItem.T_TAG, "<html lang=\"en\">" },
					{ HtmlItem.T_COMMENT, "<!-- comment -->" },
					{ HtmlItem.T_EXCLAMATION, "<!exclamation!>" },
					{ HtmlItem.T_PROCESSING, "<?processing?>" },
					{ HtmlItem.T_TAG, "<input name=\"1\" />" },
					{ HtmlItem.T_TAG, "<input name=\"2\" />" },
					{ HtmlItem.T_TAG, "<input id=\"3\" />" },
					{ HtmlItem.T_TAG, "<input id=\"4\" />" },
					{ HtmlItem.T_TAG, "<a name=\"5\">" },
					{ HtmlItem.T_TAG, "<a id=\"6\">" },
					{ HtmlItem.T_TEXT, "templatemaster" },
					{ HtmlItem.T_TAG, "<i18n text_id=\"id\" />"},
					{ HtmlItem.T_TAG, "<placeholder id=\"ph1\" />" },
					{ HtmlItem.T_TAG, "<placeholder id=\"ph2\" />" },
					{ HtmlItem.T_ENDTAG, "</html>" }
			};

			htmlItems = template_tpl.getHtmlItems();

			Assert.assertEquals( expected.length, htmlItems.size() );
			for ( int i=0; i<expected.length; ++i ) {
				Assert.assertEquals( expected[ i ][ 0 ], htmlItems.get( i ).getType() );
				Assert.assertEquals( expected[ i ][ 1 ], htmlItems.get( i ).getText() );
			}

			String[][] tagIdNameArr = {
					{},
					{"a"},
					{"input", "1", "3" },
					{"a"},
					{"input", "1", "3" }
			};
			Map<String, Set<String>> tagMap = template_tpl.buildTagMap( tagIdNameArr );

			reductions = template_tpl.reduce( tagMap );
			Assert.assertEquals( 8, reductions );

			reductions = template_tpl.reduce( tagMap );
			Assert.assertEquals( 5, reductions );

			expected = new Object[][] {
					{ HtmlItem.T_TEXT, "<html lang=\"en\"><!-- comment --><!exclamation!><?processing?>" },
					{ HtmlItem.T_TAG, "<input name=\"1\" />" },
					{ HtmlItem.T_TEXT, "<input name=\"2\" />" },
					{ HtmlItem.T_TAG, "<input id=\"3\" />" },
					{ HtmlItem.T_TEXT, "<input id=\"4\" />" },
					{ HtmlItem.T_TAG, "<a name=\"5\">" },
					{ HtmlItem.T_TAG, "<a id=\"6\">" },
					{ HtmlItem.T_TEXT, "templatemaster" },
					{ HtmlItem.T_TAG, "<i18n text_id=\"id\" />"},
					{ HtmlItem.T_TAG, "<placeholder id=\"ph1\" />" },
					{ HtmlItem.T_TAG, "<placeholder id=\"ph2\" />" },
					{ HtmlItem.T_TEXT, "</html>" }
			};

			htmlItems = template_tpl.getHtmlItems();
			Assert.assertEquals( expected.length, htmlItems.size() );
			for ( int i=0; i<expected.length; ++i ) {
				Assert.assertEquals( expected[ i ][ 0 ], htmlItems.get( i ).getType() );
				Assert.assertEquals( expected[ i ][ 1 ], htmlItems.get( i ).getText() );
			}
		}
		catch (IOException e) {
			e.printStackTrace();
			Assert.fail( "Unexpected exception!" );
		}
	}

}
