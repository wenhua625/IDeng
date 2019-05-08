package com.novarise.webase.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.novarise.webase.BConstants;
import com.novarise.webase.framework.HtmlFunction;
import com.novarise.webase.framework.SystemFunction;
import com.novarise.webase.framework.WebControl;
import com.novarise.webase.xml.XmlUtil;



public class Display extends HttpServlet {

	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final String CONTENT_TYPE = "text/html; charset=GBK";
	
	private static String root = "";
	private static String title = "";
	
	/**
	 * Constructor of the object.
	 */
	public Display() {
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

		response.setContentType(CONTENT_TYPE);
        //response.setHeader("Cache-Control", "no-cache");
		//将公用信息存入Session中
		HttpSession session = request.getSession();
		if (session.getAttribute(BConstants.SESSION_TITLE) == null){
			session.setAttribute(BConstants.SESSION_TITLE, title);
		}
		
		PrintWriter out = response.getWriter();
		
		//为了防止串画面
		WebControl control = new WebControl();
		String htmlfile = "";
		
		if (root == null || root.trim().length() == 0) {
			out.println("错误!请在dsconfig.xml文件中配置rootdir!");
			return;
		}
		
		String proname = request.getParameter("proname");
		if (proname == null || proname.trim().length() == 0) {
			String error = SystemFunction.showError(101, "错误!没有要处理的页面", "错误!没有要处理的页面",request);
			out.println(error);
			return;
		}
		if (proname.indexOf(".") == -1) proname += ".htm";
		
		//读取页面
		try {
			htmlfile = SystemFunction.readFile(root+proname);
		} catch (Exception e) {
			out.print(SystemFunction.showError(102, "文件没找到!", e.toString(),request));
			return;
		}
		//处理Import文件
		try {
			htmlfile = control.parseDisplayIM(htmlfile);
		} catch (Exception e) {
			out.println(SystemFunction.showError(104, "处理Import文件时出错", e.toString(),request));
			return;
		}
		
		//处理request或session变量(@@)
		try {
			htmlfile = HtmlFunction.parseVar(htmlfile, request, " ");
		} catch (Exception e) {
			out.println(SystemFunction.showError(103, "替换 request或session变量时出错!", e.toString(),request));
			return;
		}
		//取页面控件集
		List kjm = new ArrayList();
		kjm = HtmlFunction.getPageMarket(kjm,htmlfile, "$$");
		kjm = HtmlFunction.getPageMarket(kjm, htmlfile, "!!");
		
		//处理开始
		
		for ( int i=0;i<kjm.size(); i ++){
			String kj = (String)kjm.get(i);
			try {
				if (kj.substring(0, 2).equals("MJ")) {
					htmlfile = control.parseDisplayMJ(htmlfile,kj,request);
				} 
				if (kj.substring(0, 2).equals("ZQ")) {
					htmlfile = control.parseDisplayZQ(htmlfile, kj,request, response);
					
				}
				if (kj.substring(0, 2).equals("XL")) {
					htmlfile = control.parseDisplayXL(htmlfile, kj,request, response);
				}
                if (kj.substring(0, 2).equals("EX")) {
                    htmlfile = control.parseDisplayEX(htmlfile, kj,request, response);
                }
                if (kj.substring(0, 2).equals("AP")) {
                    htmlfile = control.parseDisplayAP(htmlfile, kj,request, response);
                }
				
                if (kj.substring(0, 3).equals("GEN")) {
                    htmlfile = control.parseGenSeq(htmlfile, kj);
                }
                //产生报表
                if (kj.substring(0, 2).equals("RP")) {
                    htmlfile = control.parseReport(htmlfile, kj, request);
                } 
                if (kj.substring(0, 2).equals("UE")) {
                    try {
                      htmlfile = control.parseUpLoad(htmlfile, kj, request, response);
                    }
                    catch (Exception e) {
                      htmlfile = "error:" + e.toString();
                    }

                  }
			}catch(Exception e){
				e.printStackTrace();
				htmlfile = SystemFunction.showError(101, kj + "处理时出错!", e.toString(),request);
			}
			
		}
		 
		out.print(htmlfile);
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		
		try{
			title = XmlUtil.readXml(BConstants.CONFIG_FILE,BConstants.SYSTEM_TITLE );
			root  = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT);
			//auth  = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_AUTH);
		}catch(Exception e){
			System.out.println("读取配置文件出错!\n" + e.toString());
		}
	}

}
