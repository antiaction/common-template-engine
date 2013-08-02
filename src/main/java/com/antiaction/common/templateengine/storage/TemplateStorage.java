/*
 * Created on 01/08/2013
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine.storage;

import java.util.List;

import com.antiaction.common.html.HtmlItem;

public interface TemplateStorage {

	public boolean exists();

	public void checkReload();

	public long lastModified();

	public long length();

	public List<HtmlItem> getHtmlItems();

}
