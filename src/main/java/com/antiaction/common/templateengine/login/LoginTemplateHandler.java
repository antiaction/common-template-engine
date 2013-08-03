/*
 * Created on 15/07/2011
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */

package com.antiaction.common.templateengine.login;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.antiaction.common.filter.Caching;
import com.antiaction.common.html.HtmlEntity;
import com.antiaction.common.templateengine.Template;
import com.antiaction.common.templateengine.TemplateMaster;
import com.antiaction.common.templateengine.TemplateParts;
import com.antiaction.common.templateengine.TemplatePlaceBase;
import com.antiaction.common.templateengine.TemplatePlaceHolder;
import com.antiaction.common.templateengine.TemplatePlaceTag;

public class LoginTemplateHandler<UserType extends LoginTemplateUser> {

	public TemplateMaster templateMaster = null;

	public String templateName = null;

	public String title = null;

	public String adminPath = "/admin/";

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

		resp.sendRedirect( adminPath );
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
				//TemplatePlaceTag title = TemplatePlaceBase.getTemplatePlaceTag( "title", null );
				TemplatePlaceHolder titlePlace = TemplatePlaceBase.getTemplatePlaceHolder( "title" );
				TemplatePlaceHolder errorPlace = TemplatePlaceBase.getTemplatePlaceHolder( "error" );
				TemplatePlaceTag loginUser = TemplatePlaceBase.getTemplatePlaceTag( "input", "login_user" );
				TemplatePlaceTag loginRemember = TemplatePlaceBase.getTemplatePlaceTag( "input", "login_rememberme" );
				TemplatePlaceTag loginToUrl = TemplatePlaceBase.getTemplatePlaceTag( "input", "login_tourl" );

				List<TemplatePlaceBase> placeHolders = new ArrayList<TemplatePlaceBase>();
				placeHolders.add( titlePlace );
				placeHolders.add( errorPlace );
				placeHolders.add( loginUser );
				placeHolders.add( loginRemember );
				placeHolders.add( loginToUrl );

				TemplateParts templateParts = template.filterTemplate( placeHolders, resp.getCharacterEncoding() );

				//loginUser.htmlItem.setAttribute( "value", HTMLEntity.encodeHtmlEntities( "'\"&<>" ).toString() );

				if ( titlePlace != null ) {
					titlePlace.setText( HtmlEntity.encodeHtmlEntities( title ).toString() );
				}

				if ( errorPlace != null && bError ) {
					String invalid = callback.getTranslated( "raptor.login.invalid" );
					errorPlace.setText( "<img width=\"16\" height=\"16\" src=\"/images/login/error.gif\" alt=\""+ invalid + "\">" + invalid );
				}

				if ( loginUser != null && user != null ) {
					loginUser.setAttribute( "value", HtmlEntity.encodeHtmlEntities( user ).toString() );
				}

				if ( loginRemember != null && rememberMe != null ) {
					loginRemember.setAttribute( "checked", null );
				}

				if ( loginToUrl != null && toUrl != null ) {
					loginToUrl.setAttribute( "value", HtmlEntity.encodeHtmlEntities( toUrl ).toString() );
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
						loginToUrl.setAttribute( "value", HtmlEntity.encodeHtmlEntities( url ).toString() );
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
				resp.sendRedirect( adminPath );
			}
		}
	}

}
