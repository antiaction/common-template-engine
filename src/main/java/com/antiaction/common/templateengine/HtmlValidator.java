/*
 * Created on 15/07/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.antiaction.common.html.HtmlItem;

public class HtmlValidator {

	public static Set<String> noselfchild = new HashSet<String>();

	static {
		noselfchild.add( "meta" );
		noselfchild.add( "img" );
		noselfchild.add( "br" );
		noselfchild.add( "input" );
	}

	public static void validate(List<HtmlItem> html_items) {
		if ( html_items == null || html_items.size() == 0 ) {
			return;
		}

		int state = 0;

		HtmlItem htmlItem;
		int type;
		HtmlItem stackItem;

		List<HtmlItem> stack = new ArrayList<HtmlItem>();

		boolean b = true;
		boolean s;
		int idx = 0;
		while ( b ) {
			if ( idx < html_items.size() ) {
				htmlItem = html_items.get( idx );
				type = htmlItem.getType();
				switch ( state ) {
				case 0:
					if ( type == HtmlItem.T_PROCESSING ) {
						// debug
						//System.out.println( "0: " + htmlItem.getText() );

						++idx;
						state = 1;
					}
					else if ( type == HtmlItem.T_EXCLAMATION ) {
						// debug
						//System.out.println( "1: " + htmlItem.getText() );

						++idx;
						state = 2;
					}
					else {
						state = 2;
					}
					break;
				case 1:
					if ( type == HtmlItem.T_EXCLAMATION ) {
						// debug
						System.out.println( "1: " + htmlItem.getText() );

						++idx;
						state = 2;
					}
					else {
						state = 2;
					}
					break;
				case 2:
					switch ( type ) {
					case HtmlItem.T_TAG:
						if ( !htmlItem.getClosed() ) {
							if ( stack.size() > 0 ) {
								if ( noselfchild.contains( htmlItem.getTagname().toLowerCase() ) ) {
									stackItem = stack.get( stack.size() - 1 );
									if ( htmlItem.getTagname().compareToIgnoreCase( stackItem.getTagname() ) == 0 ) {
										stack.remove( stack.size() - 1 );
									}
								}
							}
							stack.add( htmlItem );
						}
						break;
					case HtmlItem.T_ENDTAG:
						s = true;
						while ( s ) {
							if ( stack.size() > 0 ) {
								stackItem = stack.get( stack.size() - 1 );
								if ( htmlItem.getTagname().compareToIgnoreCase( stackItem.getTagname() ) == 0 ) {
									stack.remove( stack.size() - 1 );
									s = false;
								}
								else {
									if ( noselfchild.contains( stackItem.getTagname().toLowerCase() ) ) {
										stack.remove( stack.size() - 1 );
									}
									else {
										stack.remove( stack.size() - 1 );
										// debug
										System.out.println( "Expected: " + htmlItem.getTagname() + ", got: " + stackItem.getTagname() );
									}
								}
							}
							else {
								s = false;
							}
						}
						break;
					case HtmlItem.T_TEXT:
					case HtmlItem.T_PROCESSING:
					case HtmlItem.T_EXCLAMATION:
					case HtmlItem.T_COMMENT:
					default:
						break;
					}
					++idx;
					break;
				default:
					b = false;
					break;
				}
			}
			else {
				if ( stack.size() > 0 ) {
					// debug
					System.out.println( "Htlm Epic fail!" );
				}
				b = false;
			}
		}
	}

}
