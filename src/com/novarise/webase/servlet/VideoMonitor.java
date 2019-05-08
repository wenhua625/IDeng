package com.novarise.webase.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.JDOMException;

import com.novarise.webase.util.XMLUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLUpdater;

public class VideoMonitor extends HttpServlet {

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}
	
	
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {


		String video_id=request.getParameter("id");
		String progress=request.getParameter("progress");
		
		if(video_id == null){
			video_id="";
		}
		
		if(progress == null){
			progress="";
		}
		
		
		if(progress .equals("100")){
			System.out.println("full:"+video_id);
		}
	    
	   
	       
	 
		
		
		
	
	}

}
