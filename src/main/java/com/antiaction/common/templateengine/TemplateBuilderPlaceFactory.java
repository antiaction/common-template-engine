package com.antiaction.common.templateengine;

import java.lang.reflect.Field;

import com.antiaction.common.templateengine.TemplatePlaceBase;
import com.antiaction.common.templateengine.TemplatePlaceHolder;
import com.antiaction.common.templateengine.TemplatePlaceTag;

public abstract class TemplateBuilderPlaceFactory {

	public static TemplateBuilderPlaceFactory getTemplateTagPlaceFactory(String tagName, String idName, Field field) {
		return new TemplaceBuilderTagPlaceFactory( tagName, idName, field );
	}

	public static TemplateBuilderPlaceFactory getTemplatePlaceHolderFactory(String idName, Field field) {
		return new TemplateBuilderPlaceHolderFactory( idName, field );
	}

	public Field field;

	public abstract TemplatePlaceBase getInstance();

	public static class TemplaceBuilderTagPlaceFactory extends TemplateBuilderPlaceFactory {
		public String tagName;
		public String idName;
		public TemplaceBuilderTagPlaceFactory(String tagName, String idName, Field field) {
			this.tagName = tagName;
			this.idName = idName;
			this.field = field;
		}
		@Override
		public TemplatePlaceBase getInstance() {
			return TemplatePlaceTag.getInstance( tagName, idName );
		}
	}

	public static class TemplateBuilderPlaceHolderFactory extends TemplateBuilderPlaceFactory {
		public String idName;
		public TemplateBuilderPlaceHolderFactory(String idName, Field field) {
			this.idName = idName;
			this.field = field;
		}
		@Override
		public TemplatePlaceBase getInstance() {
			return TemplatePlaceHolder.getInstance( idName );
		}
	}

}
