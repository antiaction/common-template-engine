package com.antiaction.common.templateengine;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public abstract class TemplateBuilderBase {

	public List<TemplatePlaceBase> placeHolders = new LinkedList<TemplatePlaceBase>();

	public TemplateParts templateParts;

	public void write(OutputStream out) throws IOException {
		TemplatePartBase part;
		if ( out != null ) {
			for ( int i = 0; i < templateParts.parts.size(); ++i ) {
				part = templateParts.parts.get( i );
				if ( part != null ) {
					out.write( part.getBytes() );
				}
			}
		}
	}

}
