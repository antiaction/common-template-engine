/*
 * Created on 15/07/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.raptor.template;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.antiaction.common.html.HTMLEntity;
import com.antiaction.raptor.frontend.Caching;

public class LoginTemplateHandler<UserType extends LoginTemplateUser> {

	public TemplateMaster templateMaster = null;

	public String templateName = null;

	public void logoff(HttpServletRequest req, HttpServletResponse resp, HttpSession session) throws IOException {
		Cookie rememberCookie;
		if ( session != null ) {
			// Session user.
			session.removeAttribute( "user" );
		}
		// Cookie user.
		rememberCookie = new Cookie( "usertoken", "" );
		rememberCookie.setPath( "/" );
		rememberCookie.setSecure( false );
		rememberCookie.setMaxAge( 0 );
		resp.addCookie( rememberCookie );

		resp.sendRedirect( "/admin/" );
	}

	public UserType loginFromCookie(HttpServletRequest req, HttpServletResponse resp, HttpSession session, LoginTemplateCallback<UserType> callback) {
		Cookie rememberCookie;
		UserType current_user = null;

		//String user_token, 
		Cookie[] cookies = req.getCookies();
		if ( cookies != null && cookies.length > 0 ) {
			for ( int i=0; i<cookies.length; ++i ) {
				if ( "usertoken".compareToIgnoreCase( cookies[ i ].getName() ) == 0 ) {
					// Cookie user.
					current_user = callback.validateUserCookie( cookies[ i ].getValue() );
				}
			}
		}

		if ( current_user != null ) {
			// Session user.
			session.setAttribute( "user", current_user );

			// Cookie user.
			rememberCookie = new Cookie( "usertoken", current_user.get_cookie_token( req ) );
			rememberCookie.setPath( "/" );
			rememberCookie.setSecure( false );
			rememberCookie.setMaxAge( 24 * 60 * 60 );
			resp.addCookie( rememberCookie );
		}

		return current_user;
	}

	public void loginFromForm(HttpServletRequest req, HttpServletResponse resp, HttpSession session, LoginTemplateCallback<UserType> callback) throws IOException {
		UserType current_user = null;

		String user = null;
		String pass = null;
		String rememberMe = null;
		String toUrl = null;

		boolean bError = false;

		String action = req.getParameter( "action" );

		if ( "POST".compareToIgnoreCase( req.getMethod() ) == 0 && action != null && "login".compareToIgnoreCase( action ) == 0 ) {
			user = req.getParameter( "login_user" );
			pass = req.getParameter( "login_pass" );
			rememberMe = req.getParameter( "login_rememberme" );
			toUrl = req.getParameter( "login_tourl" );

			bError = true;

			// Debug
			//System.out.println( "user: " + user + ", pass: " + pass + ", remember: " + rememberMe + ", toUrl: " + toUrl );

			Cookie rememberCookie = null;

			if ( user != null && user.length() > 0 && pass != null && pass.length() > 0 ) {
				current_user = callback.validateUserCredentials( user, pass );

				// Session user.
				session.setAttribute( "user", current_user );

				// Cookie user.
				rememberCookie = new Cookie( "usertoken", "" );
				rememberCookie.setPath( "/" );
				rememberCookie.setSecure( false );
				rememberCookie.setMaxAge( 0 );

				if ( current_user != null && rememberMe != null && rememberMe.length() > 0 ) {
					rememberCookie.setValue( current_user.get_cookie_token( req ) );
					rememberCookie.setMaxAge( 24 * 60 * 60 );

					bError = false;
				}
				resp.addCookie( rememberCookie );
			}
		}

		if ( current_user == null ) {
			ServletOutputStream out = resp.getOutputStream();
			resp.setContentType( "text/html; charset=utf-8" );

			Caching.caching_disable_headers( resp );

			// debug
			//System.out.println( templateMaster );

			//Template template = env.templateMaster.getTemplate( "login-amiga.html" );
			//Template template = env.templateMaster.getTemplate( "login-xp.html" );

			Template template = templateMaster.getTemplate( templateName );

			// debug
			//System.out.println( template );

			if ( template != null ) {
				TemplatePlaceHolder errorPlace = TemplatePlace.getTemplatePlaceHolder( "errorPlace" );
				TemplatePlaceTag loginUser = TemplatePlace.getTemplatePlaceTag( "input", "login_user" );
				//TemplateTagPlace loginPass = TemplatePlace.getTemplateTagPlace( "input", "login_pass" );
				TemplatePlaceTag loginRemember = TemplatePlace.getTemplatePlaceTag( "input", "login_rememberme" );
				TemplatePlaceTag loginToUrl = TemplatePlace.getTemplatePlaceTag( "input", "login_tourl" );

				List<TemplatePlace> placeHolders = new ArrayList<TemplatePlace>();
				placeHolders.add( errorPlace );
				placeHolders.add( loginUser );
				placeHolders.add( loginRemember );
				placeHolders.add( loginToUrl );

				TemplateParts templateParts = template.filterTemplate( placeHolders, resp.getCharacterEncoding() );

				//loginUser.htmlItem.setAttribute( "value", HTMLEntity.encodeHtmlEntities( "'\"&<>" ).toString() );

				if ( bError ) {
					String invalid = callback.getTranslated( "raptor.login.invalid" );
					errorPlace.setText( "<img width=\"16\" height=\"16\" src=\"/images/login/error.gif\" alt=\""+ invalid + "\">" + invalid );
				}

				if ( user != null ) {
					loginUser.setAttribute( "value", HTMLEntity.encodeHtmlEntities( user ).toString() );
				}

				if ( rememberMe != null ) {
					loginRemember.setAttribute( "checked", null );
				}

				if ( toUrl != null ) {
					loginToUrl.setAttribute( "value", HTMLEntity.encodeHtmlEntities( toUrl ).toString() );
				}
				else {
					String pathInfo = req.getPathInfo();
					if ( pathInfo != null && pathInfo.length() > 0 ) {
						//System.out.println( req );
						//loginToUrl.setAttribute( "value", HTMLEntity.encodeHtmlEntities( req.getRequestURL() ).toString() );

						String url = req.getRequestURI();
						String qs = req.getQueryString();
						if ( qs != null && qs.length() > 0 ) {
							url += "?" + qs;
						}
						loginToUrl.setAttribute( "value", HTMLEntity.encodeHtmlEntities( url ).toString() );
					}
				}

				for ( int i=0; i<templateParts.parts.size(); ++i ) {
					out.write( templateParts.parts.get( i ).getBytes() );
				}

				out.flush();
				out.close();
			}
			else {
				resp.sendError( HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Missing template." );
			}

			/*
			String templatePath = req.getRealPath( req.getContextPath() );
			File file = new File( templatePath, "login-amiga.html" );
			RandomAccessFile ram = new RandomAccessFile( file, "r" );
			byte[] data = new byte[ (int)ram.length() ];
			ram.readFully( data );
			out.write( data );
			*/
		}
		else {
			if ( toUrl != null && toUrl.length() > 0 ) {
				resp.sendRedirect( toUrl );
			}
			else {
				resp.sendRedirect( "/admin/" );
			}
		}
	}

}
