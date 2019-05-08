package com.novarise.webase.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.novarise.webase.framework.SystemFunction;
import com.novarise.webase.framework.WebControl;

public class DownLoadServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    ServletConfig config=null;

    
    /**
     * Constructor of the object.
     */
    public DownLoadServlet() {
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
     * The doPost method of the servlet. <br>
     *
     * This method is called when a form has its tag value method equals to post.
     * 
     * @param request the request send by the client to the server
     * @param response the response send by the server to the client
     * @throws ServletException if an error occurred
     * @throws IOException if an error occurred
     */
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            response.setContentType("text/html;charset=gb2312");
            WebControl control = new WebControl();
            String proname = request.getParameter("proname");
            if (proname == null || proname.trim().length() == 0) {
                String error = SystemFunction.showError(101, "错误!没有要处理的事件", "错误!没有要处理的事件",request);
                response.getWriter().println(error);
                return;
            }
            if(proname.substring(0, 2).equals("EX")){
                WebControl.downLoadExcel(proname, request, response, config);
            }
            if(proname.substring(0, 2).equals("KQ")){
            	if(request.getParameter("isdown") == null || request.getParameter("isdown").equals("") )
            	{
            		PrintWriter out = response.getWriter();
                    out.println(WebControl.downLoadExcelKQ(proname, request, response, config));
            	}else  WebControl.downLoadExcelKQ(proname, request, response, config);
            	
            }
            if(proname.substring(0, 2).equals("TX")){
                control.downLoadTxt(request, response, config);
            }
            if(proname.substring(0,2).equals("UP")){
                control.upLoad(request, response, config);
            }
            if(proname.substring(0,2).equals("FL")){
            	 WebControl.downLoadFile(proname, request, response, config);
            }
            
            
            
            
        } catch (Exception e) {
            PrintWriter out = response.getWriter();
            out.println(SystemFunction.showError(104, "文件上传/下载时出错", e.toString(), request));
        }
    }

    /**
     * Initialization of the servlet. <br>
     *
     * @throws ServletException if an error occure
     */
    public void init(ServletConfig config) throws ServletException {
       super.init(config);
       this.config = config;
       
    }

}
