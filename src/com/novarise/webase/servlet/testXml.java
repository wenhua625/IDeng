package com.novarise.webase.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.novarise.webase.xml.XmlUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.DSSession;

public class testXml extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public testXml() {
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

		response.setContentType("text/html;charset=GB2312");
		PrintWriter out = response.getWriter();
		/*String id=request.getParameter("id");
		if (id==null) id="";
		String xml_str="";
		if (id.equals("1")){
		 xml_str="<?xml version=\"1.0\" encoding=\"GBK\"?>"
					   +"<TreeNode>"
	                   +"<TreeNode Title=\"订单中心\" NodeId=\"user_root\" NodeXmlSrc=\"/webase/test\"  Icon=\"images/loadtree/users.gif\" Target=\"main\"/>"
                       +"</TreeNode>";
		}else{
			 xml_str="<?xml version=\"1.0\" encoding=\"GBK\"?>"
				   +"<TreeNode>"
                 +"<TreeNode Title=\"下订单\" NodeId=\"order_root\" Href=\"display?proname=agent/order/tab/orderTab.htm\"  Icon=\"images/loadtree/rnode.gif\" Target=\"main\"/>"
                 +"</TreeNode>";
		}
		out.println(xml_str);*/
		DSSession dss = null;
		   String sql="SELECT MenuId, ParentMenuId, MenuName, MenuUrl, MenuTarget, MenuIcon, IsPms,isLeaf FROM TJ_SYS_MENU";
		   try
		   {
			   dss = DSManager.getDSSession();
			  
			   XmlUtil.doc2XmlFile(dss.xmlQuery(sql),"zq4mxConfig.xml");
			  /* if(XmlUtil.data2TxtFile(sql,"c:\\product.txt")){
				   out.println("成功");
			   }else
			   {
				   out.println("失败");
			   }*/
		   }catch (Exception e){
			   e.printStackTrace();
		   }finally{
			   if(dss != null)
			   {
				   try{
					   dss.close(); 
				   }catch(Exception e){
					 e.printStackTrace();   
				   }
			   }
			   
			  
		   }
		out.flush();
		out.close();
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occure
	 */
	public void init() throws ServletException {
		// Put your code here
	}

}
