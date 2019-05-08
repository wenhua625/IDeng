package com.novarise.webase.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



public class CharacterEncodingFilter implements Filter
{
	protected String encoding = null;
	protected FilterConfig filterConfig = null;
	protected boolean ignore = true;
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init( FilterConfig filterConfig ) throws ServletException
	{
		this.filterConfig = filterConfig;
		this.encoding = filterConfig.getInitParameter( "encoding" );
		String value = filterConfig.getInitParameter( "ignore" );
		if (value == null)
			this.ignore = true;
		else if ("true".equalsIgnoreCase( value ))
			this.ignore = true;
		else if ("yes".equalsIgnoreCase( value ))
			this.ignore = true;
		else
			this.ignore = false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter( ServletRequest request, ServletResponse response,
			FilterChain chain ) throws IOException, ServletException
	{
		
		
		 	
		
		if (ignore || (request.getCharacterEncoding() == null))
		{
			String encoding = selectEncoding( request );
			if (encoding != null)
				request.setCharacterEncoding( encoding );
		}
		chain.doFilter( request, response );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy()
	{
		this.encoding = null;
		this.filterConfig = null;
	}

	protected String selectEncoding( ServletRequest request )
	{
		return (this.encoding);
	}
}