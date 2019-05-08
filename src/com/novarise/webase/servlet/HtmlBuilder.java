package com.novarise.webase.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.novarise.webase.util.BuildHTML;

public class HtmlBuilder extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public HtmlBuilder() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=utf-8");
		 request.setCharacterEncoding("utf-8");
		 
		 Map<String, Object> conf  = new HashMap();
	    
		
		BuildHTML bh = new BuildHTML();
		
		long maxSize = 2000000000;
		String rootPath = "";
		
		try{
		
		conf.put("maxSize", maxSize);
	    String time = String.valueOf(System.currentTimeMillis());
		conf.put("savePath", "news/"+time);
		conf.put("rootPath", this.getServletContext().getRealPath("/"));
		}catch(Exception e){
			e.printStackTrace();
		}
		try{
		String save = bh.save(request, conf);
		PrintWriter out = response.getWriter();
		out.println(save);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	
	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
