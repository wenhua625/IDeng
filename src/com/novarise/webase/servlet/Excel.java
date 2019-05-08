package com.novarise.webase.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Excel extends HttpServlet {
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html; charset=utf-8");
		PrintWriter out = response.getWriter();
		response.setHeader("Content-Type","application/force-download"); 
		response.setHeader("Content-Type","application/vnd.ms-excel"); 
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		response.setHeader("Content-Disposition","attachment;filename="+ts+".xls"); 
		out.print(request.getParameter("exportContent")); 
	}

}
