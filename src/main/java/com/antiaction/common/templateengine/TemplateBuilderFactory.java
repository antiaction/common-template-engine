package com.antiaction.common.templateengine;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.antiaction.common.templateengine.Template;
import com.antiaction.common.templateengine.TemplateMaster;
import com.antiaction.common.templateengine.TemplatePlaceBase;

public class TemplateBuilderFactory<T extends TemplateBuilderBase> {

	public Template template;

	public String charset;

	public Class<? extends T> tplBuilderClass;

	public TemplateBuilderPlaceFactory[] placeHoldersArr;

	protected TemplateBuilderFactory() {
	}

	public static <T extends TemplateBuilderBase> TemplateBuilderFactory<T> getInstance(TemplateMaster templateMaster, String name, String charset, Class<? extends T> tplBuilderClass) throws IOException {
		TemplateBuilderFactory<T> tb = new TemplateBuilderFactory<T>();
		tb.template = templateMaster.getTemplate( name );
		tb.charset = charset;
		tb.tplBuilderClass = tplBuilderClass;

		List<TemplateBuilderPlaceFactory> placeHolders = new ArrayList<TemplateBuilderPlaceFactory>();
		Field[] fields = tplBuilderClass.getFields();
		Field field;
		Class<?> fieldType;
		Class<?> tmpClass;
		if ( fields != null && fields.length > 0 ) {
			for ( int i=0; i<fields.length; ++i ) {
				field = fields[i];
				fieldType = field.getType();
				if ( !fieldType.isArray() && !fieldType.isPrimitive() ) {
					tmpClass = fieldType;
					while ( tmpClass != null && tmpClass != TemplatePlaceBase.class ) {
						tmpClass = tmpClass.getSuperclass();
					}
					if ( tmpClass != null ) {
						TemplateBuilderTagPlace ttpA = field.getAnnotation( TemplateBuilderTagPlace.class );
						TemplateBuilderPlaceHolder tphA = field.getAnnotation( TemplateBuilderPlaceHolder.class );
						if ( ttpA != null ) {
							placeHolders.add( TemplateBuilderPlaceFactory.getTemplateTagPlaceFactory( ttpA.tagName(), ttpA.idName(), field ) );
						}
						else if ( tphA != null ) {
							placeHolders.add( TemplateBuilderPlaceFactory.getTemplatePlaceHolderFactory( tphA.value(), field ) );
						}
					}
				}
			}
		}
		tb.placeHoldersArr = placeHolders.toArray( new TemplateBuilderPlaceFactory[ placeHolders.size() ] );
		return tb;
	}

	public T getTemplateBuilder() throws UnsupportedEncodingException, IOException {
		T tplBuilder = null;
		TemplateBuilderPlaceFactory tpf;
		TemplatePlaceBase tpb;
		try {
			tplBuilder = tplBuilderClass.newInstance();
			for ( int i=0; i<placeHoldersArr.length; ++i ) {
				tpf = placeHoldersArr[ i ];
				tpb = tpf.getInstance();
				tpf.field.set( tplBuilder, tpb );
				tplBuilder.placeHolders.add( tpb );
			}
			tplBuilder.templateParts = template.filterTemplate( tplBuilder.placeHolders, charset );
		}
		catch (InstantiationException e) {
		}
		catch (IllegalAccessException e) {
		}
		return tplBuilder;
	}

}
