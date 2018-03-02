package com.antiaction.common.templateengine;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import com.antiaction.common.templateengine.BasicStringReplaceCached.DynamicElement;
import com.antiaction.common.templateengine.BasicStringReplaceCached.Element;
import com.antiaction.common.templateengine.BasicStringReplaceCached.TextElement;

@RunWith(JUnit4.class)
public class TestBasicStringReplaceCached {

	@Test
	public void test_template() {
		String str;
		Object[][] elements;
		String expected;
		boolean bFailOnMissing;
		boolean bExceptionExpected;
		String expectedMessage;
		Object[][] testCases;

		BasicStringReplaceCached cached;
		TextElement textElement;
		DynamicElement dynamicElement;

		testCases = new Object[][] {
				{
					"${STR}", new Object[][] {
						{Element.T_DYNAMIC, "STR"}
					}, "TemplateEngine", true, false, null
				},
				{
					"100$", new Object[][] {
						{Element.T_TEXT, "100$"}
					}, "100$", true, false, null
				},
				{
					"$TR", new Object[][] {
						{Element.T_TEXT, "$TR"}
					}, "$TR", true, false, null
				},
				{
					"$$TR", new Object[][] {
						{Element.T_TEXT, "$TR"}
					}, "$TR", true, false, null
				},
				{
					"TMP$TR", new Object[][] {
						{Element.T_TEXT, "TMP$TR"}
					}, "TMP$TR", true, false, null
				},
				{
					"TMP$$TR", new Object[][] {
						{Element.T_TEXT, "TMP$TR"}
					}, "TMP$TR", true, false, null
				},
				{
					"Welcome to the ${", new Object[][] {
						{Element.T_TEXT, "Welcome to the ${"}
					}, "Welcome to the ${", true, false, null
				},
				{
					"${${STR}}", new Object[][] {
						{Element.T_DYNAMIC, "${STR"},
						{Element.T_TEXT, "}"}
					}, "}", false, false, null
				},
				{
					"${${STR}}", new Object[][] {
						{Element.T_DYNAMIC, "${STR"},
						{Element.T_TEXT, "}"}
					}, "}", true, true, "Env is missing replacement for: ${STR"
				},
				{
					"$${STR}", new Object[][] {
						{Element.T_TEXT, "${STR}"}
					}, "${STR}", true, false, null
				},
				{
					"Find ${STR} at ${str}", new Object[][] {
						{Element.T_TEXT, "Find "},
						{Element.T_DYNAMIC, "STR"},
						{Element.T_TEXT, " at "},
						{Element.T_DYNAMIC, "str"}
					}, "Find TemplateEngine at antiaction.com", true, false, null
				},
				{
					"Find ${STR} at ${sTr}", new Object[][] {
						{Element.T_TEXT, "Find "},
						{Element.T_DYNAMIC, "STR"},
						{Element.T_TEXT, " at "},
						{Element.T_DYNAMIC, "sTr"}
					}, "Find TemplateEngine at ", false, false, null
				},
				{
					"Find ${StR} at ${str}", new Object[][] {
						{Element.T_TEXT, "Find "},
						{Element.T_DYNAMIC, "StR"},
						{Element.T_TEXT, " at "},
						{Element.T_DYNAMIC, "str"}
					}, "Find  at antiaction.com", true, true, "Env is missing replacement for: StR"
				}
		};

		for (int i = 0; i < testCases.length; ++i) {
			str = (String)testCases[i][0];
			elements = (Object[][])testCases[i][1];
			expected = (String)testCases[i][2];
			bFailOnMissing = (Boolean)testCases[i][3];
			bExceptionExpected = (Boolean)testCases[i][4];
			expectedMessage = (String)testCases[i][5];
			try {
				cached = BasicStringReplaceCached.cache(str);
				try {
					cached.map("STR", "str");
				}
				catch (IllegalArgumentException e) {
				}
				// debug
				//System.out.println(str);
				//System.out.println(cached.elementsArr.length);
				for (int j=0; j<elements.length; ++j) {
					Assert.assertEquals(elements[j][0], cached.elementsArr[j].type);
					switch (cached.elementsArr[j].type) {
					case Element.T_TEXT:
						textElement = (TextElement)cached.elementsArr[j];
						Assert.assertEquals(elements[j][1], textElement.text);
						break;
					case Element.T_DYNAMIC:
						dynamicElement = (DynamicElement)cached.elementsArr[j];
						Assert.assertEquals(elements[j][1], dynamicElement.label);
						break;
					}
				}
				str = cached.untemplate(bFailOnMissing, "TemplateEngine", "antiaction.com");
				Assert.assertEquals(expected, str);
				Assert.assertFalse(bExceptionExpected);
			}
			catch (Throwable t) {
				if (t instanceof IllegalArgumentException) {
					Assert.assertTrue(bExceptionExpected);
					Assert.assertEquals(expectedMessage, t.getMessage());
				}
				else {
					t.printStackTrace();
					Assert.fail("Unexpected exception type!");
				}
			}
		}

		cached = BasicStringReplaceCached.cache("${A} to the ${b}");
		cached.map("A", "b");
		Assert.assertEquals(2, cached.labelsMap.size());
		Assert.assertEquals(new Integer(0), cached.labelsMap.get("A").index);
		Assert.assertEquals(new Integer(1), cached.labelsMap.get("b").index);

		cached = BasicStringReplaceCached.cache("${A} to the ${b}");
		cached.map(new String[] {"A", "b"});
		Assert.assertEquals(2, cached.labelsMap.size());
		Assert.assertEquals(new Integer(0), cached.labelsMap.get("A").index);
		Assert.assertEquals(new Integer(1), cached.labelsMap.get("b").index);

		cached = BasicStringReplaceCached.cache("${A} to the ${b}");
		try {
			cached.map(new String[] {"A", "C"});
		}
		catch (IllegalArgumentException e) {
			Assert.assertEquals("Unknown replacement label: C", e.getMessage());
		}
		Assert.assertEquals(2, cached.labelsMap.size());
		Assert.assertEquals(new Integer(0), cached.labelsMap.get("A").index);
		Assert.assertEquals(null, cached.labelsMap.get("b").index);

		cached = BasicStringReplaceCached.cache("${A} to the ${b}");
		cached.map("A", "b");
		str = cached.untemplate(false, new String[] {});
		Assert.assertEquals(" to the ", str);
		str = cached.untemplate(false, new String[] {"Welcome"});
		Assert.assertEquals("Welcome to the ", str);
		str = cached.untemplate(false, new String[] {"Welcome", null});
		Assert.assertEquals("Welcome to the ", str);
		str = cached.untemplate(false, new String[] {null, "Jungle"});
		Assert.assertEquals(" to the Jungle", str);
		str = cached.untemplate(false, new String[] {"Welcome", "Jungle"});
		Assert.assertEquals("Welcome to the Jungle", str);

		cached = BasicStringReplaceCached.cache("${A} to the ${b}");
		cached.map("b", 1);
		cached.map("A", 0);
		str = cached.untemplate(false, new String[] {});
		Assert.assertEquals(" to the ", str);
		str = cached.untemplate(false, new String[] {"Welcome"});
		Assert.assertEquals("Welcome to the ", str);
		str = cached.untemplate(false, new String[] {"Welcome", null});
		Assert.assertEquals("Welcome to the ", str);
		str = cached.untemplate(false, new String[] {null, "Jungle"});
		Assert.assertEquals(" to the Jungle", str);
		str = cached.untemplate(false, new String[] {"Welcome", "Jungle"});
		Assert.assertEquals("Welcome to the Jungle", str);

		cached = BasicStringReplaceCached.cache("${A} to the ${b}");
		cached.map("A", "b");
		try {
			str = cached.untemplate(true, new String[] {});
			Assert.fail("Exception expected!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertEquals("Env is missing replacement for: A", e.getMessage());
		}
		try {
			str = cached.untemplate(true, new String[] {"Welcome"});
			Assert.fail("Exception expected!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertEquals("Env is missing replacement for: b", e.getMessage());
		}
		try {
			str = cached.untemplate(true, new String[] {"Welcome", null});
			Assert.fail("Exception expected!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertEquals("Env is missing replacement for: b", e.getMessage());
		}
		try {
			str = cached.untemplate(true, new String[] {null, "Jungle"});
			Assert.fail("Exception expected!");
		}
		catch (IllegalArgumentException e) {
			Assert.assertEquals("Env is missing replacement for: A", e.getMessage());
		}
		str = cached.untemplate(false, new String[] {"Welcome", "Jungle"});
		Assert.assertEquals("Welcome to the Jungle", str);
	}

}
