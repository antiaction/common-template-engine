/*
 * Created on 27/05/2014
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.util.LinkedList;
import java.util.List;

public class StringMatcher {

	protected StringNode[] nodes;

	public class StringNode {
		protected List<StringNode> nodeList = new LinkedList<StringNode>();
	}

	public StringMatcher(boolean caseSensitive) {
		//nodes = new StringNode[ 256 ];
	}

	public void addString(String str) {
	}

}
