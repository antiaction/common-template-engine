/*
 * Created on 21/04/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateParts {

	public Map<String, TemplatePartPlaceHolder> placeHoldersMap = new HashMap<String, TemplatePartPlaceHolder>();

	public List<TemplatePartI18N> i18nList = new ArrayList<TemplatePartI18N>();

	public List<TemplatePartBase> parts = new ArrayList<TemplatePartBase>();

}
