/*
 * Created on 11/08/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import java.util.ArrayList;
import java.util.List;

public class TemplatePlaceHolderList {

	public List<TemplatePlaceBase> placeHolders = new ArrayList<TemplatePlaceBase>();

	public TemplatePlaceHolder addTemplatePlaceHolder(String idName) {
		TemplatePlaceHolder placeHolder = TemplatePlaceBase.getTemplatePlaceHolder( idName );
		placeHolders.add( placeHolder );
		return placeHolder;
	}

	public TemplatePlaceTag addTemplatePlaceTag(String tagName, String idName) {
		TemplatePlaceTag placeHolder = TemplatePlaceBase.getTemplatePlaceTag( "title", null );
		placeHolders.add( placeHolder );
		return placeHolder;
	}

}
