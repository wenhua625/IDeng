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
import javax.servlet.http.HttpSession;

import com.novarise.webase.servlet.SessionListener;

public class PermissionFilter implements Filter
{
	protected FilterConfig config;
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig config) throws ServletException
	{
		this.config = config;
		
		
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
	 *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException
	{
		   
		    //不允许同一用户在俩个端口同时登陆
		    /*HttpServletRequest reqeust = (HttpServletRequest)req;
		    HttpSession session = reqeust.getSession();
            HttpServletResponse response = (HttpServletResponse)resp;
		 	String loginPage = this.config.getInitParameter("LOGIN_PAGE");
		 	String  requesturi = reqeust.getRequestURI()+"?"+reqeust.getQueryString();
            boolean forwardFlag = true;
            if(requesturi.indexOf("login") != -1 ){
                forwardFlag = false;
            }else if (requesturi.indexOf("LOGON") != -1){
                forwardFlag = false;
            }else if (requesturi.indexOf("QUIT") != -1){
                forwardFlag = false;
            }
	        if (forwardFlag) {
	        	boolean isOnline = SessionListener.isOnline(session);
	        	if(!isOnline){
	        		 reqeust.getRequestDispatcher(loginPage).forward(reqeust, response);  
	                
	        	}else
	        	{
	        		chain.doFilter(req, resp);
	        	}
	        }else
	        {
	        	chain.doFilter(req, resp);
	        }*/
		
		HttpServletRequest reqeust = (HttpServletRequest)req;
        HttpServletResponse response = (HttpServletResponse)resp;
	 	String loginPage = this.config.getInitParameter("LOGIN_PAGE");
	 	String  requesturi = reqeust.getRequestURI()+"?"+reqeust.getQueryString();
	 	
	 	//System.out.println(requesturi);
        boolean forwardFlag = true;
        if(requesturi.indexOf("login") != -1 ){
            forwardFlag = false;
        }else if (requesturi.indexOf("LOGON") != -1){
            forwardFlag = false;
        }else if (requesturi.indexOf("QUIT") != -1){
            forwardFlag = false;
        }
	 	
        
        if (forwardFlag) {
        	
        	String loginFlag = (String)(reqeust.getSession().getAttribute("LS.YHDM"));
        	
        	if(loginFlag == null){
                //System.out.println("LoginFlag:"+loginFlag);
        		 reqeust.getRequestDispatcher(loginPage).forward(reqeust, response);  
                
        	}else
        	{
        		chain.doFilter(req, resp);
        	}
        	

            
        }else
        {
        	chain.doFilter(req, resp);
        } 
	        
		 	
		
		
		
	}
	
	

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy()
	{
		this.config = null;
		
		
	}
}
