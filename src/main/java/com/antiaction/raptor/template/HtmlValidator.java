/*
 * Created on 15/07/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.antiaction.common.html.HTMLItem;

public class HtmlValidator {

	public static Set<String> noselfchild = new HashSet<String>();

	static {
		noselfchild.add( "meta" );
		noselfchild.add( "img" );
		noselfchild.add( "br" );
		noselfchild.add( "input" );
	}

	public static void validate(List<HTMLItem> html_items) {
		if ( html_items == null || html_items.size() == 0 ) {
			return;
		}

		int state = 0;

		HTMLItem htmlItem;
		int type;
		HTMLItem stackItem;

		List<HTMLItem> stack = new ArrayList<HTMLItem>();

		boolean b = true;
		boolean s;
		int idx = 0;
		while ( b ) {
			if ( idx < html_items.size() ) {
				htmlItem = html_items.get( idx );
				type = htmlItem.getType();
				switch ( state ) {
				case 0:
					if ( type == HTMLItem.T_PROCESSING ) {
						// debug
						//System.out.println( "0: " + htmlItem.getText() );

						++idx;
						state = 1;
					}
					else if ( type == HTMLItem.T_EXCLAMATION ) {
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
					if ( type == HTMLItem.T_EXCLAMATION ) {
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
					case HTMLItem.T_TAG:
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
					case HTMLItem.T_ENDTAG:
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
					case HTMLItem.T_TEXT:
					case HTMLItem.T_PROCESSING:
					case HTMLItem.T_EXCLAMATION:
					case HTMLItem.T_COMMENT:
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
