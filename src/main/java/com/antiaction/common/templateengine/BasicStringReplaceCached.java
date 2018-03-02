package com.antiaction.common.templateengine;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Similar to the <code>BasicStringReplace<&code> class with the addition of caching.
 * Builds a reusable array of static and dynamic elements from a template text.
 * The dynamic parts must be mapped to an index value.
 * The index values are used in the array of dynamic values which is used to call the untemplate method.
 * Only use this class when to need to use the template many times.
 */
public class BasicStringReplaceCached {

	/**
	 * Abstract base element class.
	 */
	public static abstract class Element {
		/** Text type. */
		public static final int T_TEXT = 0;
		/** Dynamic type. */
		public static final int T_DYNAMIC = 1;
		/** Element type. */
		public int type;
	}

	/**
	 * Static text element class.
	 */
	public static class TextElement extends Element {
		/** Static text. */
		public String text;
		/**
		 * Construct a text element.
		 * @param text text string
		 */
		public TextElement(String text) {
			this.type = T_TEXT;
			this.text = text;
		}
	}

	/**
	 * Dynamic element class.
	 */
	public static class DynamicElement extends Element {
		/** Label text. */
		public String label;
		/** Env array index. */
		public Integer index;
		/**
		 * Construct a dynamic element.
		 * @param label label string
		 */
		public DynamicElement(String label) {
			this.type = T_DYNAMIC;
			this.label = label;
		}
	}

	/** Template text split into an array of static and dynamic elements. */
	protected Element[] elementsArr;

	/** Lookup map of dynamic element labels. (${label}) */
	protected Map<String, DynamicElement> labelsMap;

	/**
	 * Construct an optimised reusable template object from an array of elements and a map of labels.
	 * @param elementsArr array of static and dynamic elements
	 * @param labelsMap map of dynamic element labels
	 */
	protected BasicStringReplaceCached(Element[] elementsArr, Map<String, DynamicElement> labelsMap) {
		this.elementsArr = elementsArr;
		this.labelsMap = labelsMap;
	}

	/**
	 * Parse a template text and return an optimised reusable template object.
	 * @param str template string
	 * @return reusable template object
	 */
	public static BasicStringReplaceCached cache(String str) {
		int strLen = str.length();
		String lookupStr;
		StringBuilder sb = new StringBuilder();
		TextElement textElement;
		List<Element> elements = new LinkedList<Element>();
		DynamicElement dynamicElement;
		Map<String, DynamicElement> labelsMap = new HashMap<String, DynamicElement>();
		int pIdx = 0;
		int sIdx = 0;
		int fIdx;
		int tIdx;
		int c;
		while (sIdx != -1) {
			sIdx = str.indexOf('$', pIdx);
			if (sIdx != -1) {
				if (sIdx + 1 < strLen) {
					c = str.charAt(sIdx + 1);
					if (c == '$') {
						sb.append(str, pIdx, sIdx);
						sb.append('$');
						sIdx += 2;
						pIdx = sIdx;
					}
					else if (c == '{') {
						fIdx = sIdx + 2;
						tIdx = str.indexOf('}', fIdx);
						if (tIdx != -1) {
							sb.append(str, pIdx, sIdx);
							lookupStr = str.substring(fIdx, tIdx);
							if (sb.length() > 0) {
								textElement = new TextElement(sb.toString());
								elements.add(textElement);
								sb.setLength(0);
							}
							dynamicElement = new DynamicElement(lookupStr);
							elements.add(dynamicElement);
							labelsMap.put(lookupStr, dynamicElement);
							sIdx = tIdx + 1;
							pIdx = sIdx;
						}
						else {
							sb.append(str, pIdx, fIdx);
							sIdx = fIdx;
							pIdx = sIdx;
						}
					}
					else {
						sIdx = -1;
					}
				}
				else {
					sIdx = -1;
				}
			}
		}
		if (strLen > pIdx) {
			sb.append(str, pIdx, strLen);
		}
		if (sb.length() > 0) {
			textElement = new TextElement(sb.toString());
			elements.add(textElement);
			sb.setLength(0);
		}
		Element[] elementsArr = new Element[elements.size()];
		elements.toArray(elementsArr);
		return new BasicStringReplaceCached(elementsArr, labelsMap);
	}

	/**
	 * Map a dynamic element label to an untemplate index value.
	 * @param label dynamic label string
	 * @param index untemplate index value
	 */
	public void map(String label, int index) {
		DynamicElement dynamicElement = labelsMap.get(label);
		if (dynamicElement == null) {
			throw new IllegalArgumentException("Unknown replacement label: " + label);
		}
		dynamicElement.index = index;
	}

	/**
	 * Map an array of dynamic element labels to untemplate index values.
	 * The untemplate index values equals the index value in the input label array.
	 * @param labelsArr array of dynamic element labels
	 */
	public void map(String... labelsArr) {
		for (int i=0; i<labelsArr.length; ++i) {
			map(labelsArr[i], i);
		}
	}

	/**
	 * Build a string using the array of elements and inserting all the dynamic parts from the supplied env array. 
	 * @param bFailOnMissing if true throw an exception if a dynamic element is not mapped to a value
	 * @param envArr array of dynamic values to insert in the output string
	 * @return a string built from the static elements and the dynamic values supplied
	 */
	public String untemplate(boolean bFailOnMissing, String... envArr) {
		StringBuilder sb = new StringBuilder();
		Element element;
		TextElement textElement;
		DynamicElement dynamicElement;
		Integer index;
		String replaceStr;
		for (int i=0; i<elementsArr.length; ++i) {
			element = elementsArr[i];
			switch (element.type) {
			case Element.T_TEXT:
				textElement = (TextElement)element;
				sb.append(textElement.text);
				break;
			case Element.T_DYNAMIC:
				dynamicElement = (DynamicElement)element;
				index = dynamicElement.index;
				if (index != null && index < envArr.length) {
					replaceStr = envArr[index];
				}
				else {
					replaceStr = null;
				}
				if (replaceStr == null) {
					if (!bFailOnMissing) {
						replaceStr = "";
					}
					else {
						throw new IllegalArgumentException("Env is missing replacement for: " + dynamicElement.label);
					}
				}
				sb.append(replaceStr);
				break;
			}
		}
		return sb.toString();
	}

}
