package com.antiaction.common.templateengine;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import com.antiaction.common.templateengine.TemplateParts;
import com.antiaction.common.templateengine.TemplatePlaceBase;

public abstract class TemplateBuilderBase {

	public List<TemplatePlaceBase> placeHolders = new LinkedList<TemplatePlaceBase>();

	public TemplateParts templateParts;

	public void write(OutputStream out) throws IOException {
		for ( int i = 0; i < templateParts.parts.size(); ++i ) {
			out.write( templateParts.parts.get( i ).getBytes() );
		}
	}

}
