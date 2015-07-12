package com.antiaction.common.templateengine;

import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestBasicStringReplace {

	@Test
	public void test_template() {
		String[] strArray;
		Map<String, String> env = new HashMap<String, String>();
		String str;
		String separator;
		String expected;
		boolean bFailOnMissing;
		boolean bExceptionExpected;
		String expectedMessage;
		Object[][] testCases;

		BasicStringReplace tpl = new BasicStringReplace();
		Assert.assertNotNull(tpl);

		testCases = new Object[][] { {new String[] {}, null, ""}, {new String[] {""}, null, ""},
				{new String[] {"Antiaction.com", "TemplateEngine"}, null, "Antiaction.comTemplateEngine"},
				{new String[] {"Antiaction.com", "TemplateEngine"}, "", "Antiaction.comTemplateEngine"},
				{new String[] {"Antiaction.com", "TemplateEngine"}, "\r\n", "Antiaction.com\r\nTemplateEngine\r\n"},
				{new String[] {}, "\r\n", ""},
				{null, "\r\n", ""}
		};

		for (int i = 0; i < testCases.length; ++i) {
			strArray = (String[]) testCases[i][0];
			separator = (String) testCases[i][1];
			expected = (String) testCases[i][2];
			str = BasicStringReplace.untemplate(strArray, env, false, separator);
			Assert.assertEquals(expected, str);
		}

		env.put("STR", "TemplateEngine");
		env.put("str", "antiaction.com");

		testCases = new Object[][] {
				{"${STR}", "TemplateEngine", true, false, null},
				{"100$", "100$", true, false, null},
				{"$TR", "$TR", true, false, null},
				{"$$TR", "$TR", true, false, null},
				{"TMP$TR", "TMP$TR", true, false, null},
				{"TMP$$TR", "TMP$TR", true, false, null},
				{"Welcome to the ${", "Welcome to the ${", true, false, null},
				{"${${STR}}", "}", false, false, null},
				{"${${STR}}", "}", true, true, "Env is missing replacement for: ${STR"},
				{"$${STR}", "${STR}", true, false, null},
				{"Find ${STR} at ${str}", "Find TemplateEngine at antiaction.com", true, false, null},
				{"Find ${STR} at ${sTr}", "Find TemplateEngine at ", false, false, null},
				{"Find ${StR} at ${str}", "Find  at antiaction.com", true, true, "Env is missing replacement for: StR"}
		};

		for (int i = 0; i < testCases.length; ++i) {
			str = (String) testCases[i][0];
			expected = (String) testCases[i][1];
			bFailOnMissing = (Boolean) testCases[i][2];
			bExceptionExpected = (Boolean) testCases[i][3];
			expectedMessage = (String) testCases[i][4];
			try {
				str = BasicStringReplace.untemplate(str, env, bFailOnMissing);
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
	}

}
