package com.novarise.webase.servlet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.jdom.JDOMException;

import com.novarise.webase.BConstants;
import com.novarise.webase.util.XMLUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLUpdater;

public class WeiXinPayMonitor extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public WeiXinPayMonitor() {
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


		//response.setContentType("text/html");
		
		//System.out.println("-------------开始");
		//request = ServletActionContext.getRequest();
	    // response = ServletActionContext.getResponse();
	    InputStream inStream = request.getInputStream();
	    ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
	    byte[] buffer = new byte[1024];
	    int len = 0;
	    while ((len = inStream.read(buffer)) != -1) {
	        outSteam.write(buffer, 0, len);
	    }
	    
	    outSteam.close();
	    inStream.close();
	    String result  = new String(outSteam.toByteArray(),"utf-8");//获取微信调用我们notify_url的返回信息
	   // System.out.println("result:"+result);
	    Map<Object, Object> map=null;
		try {
			map = XMLUtil.doXMLParse(result);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    /*for(Object keyValue : map.keySet()){
	        System.out.println(keyValue+"="+map.get(keyValue));
	    }*/
	    if (map.get("result_code").toString().equalsIgnoreCase("SUCCESS")) {
	        //TODO 对数据库的操作
	    	
	        response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>");   //告诉微信服务器，我收到信息了，不要在调用回调action了
	        
	        String out_trade_no = map.get("out_trade_no").toString();
	        String transaction_id =map.get("transaction_id").toString();
	        String openid =map.get("openid").toString();
	        String total_fee =map.get("total_fee").toString();
	        
	        int i_fee=Integer.parseInt(total_fee)/100;
	        
	       int pos= out_trade_no.indexOf("H");
	       String agent_code=out_trade_no.substring(pos+1, out_trade_no.length());
	       String months = out_trade_no.substring(0,2);
	       
	       String sql="if not exists (select * from paylist where payment_no='@@payment_no,') begin insert into paylist(payment_no,pay_amount,pay_agentcode,pay_date) values ('@@payment_no,','@@charge_amount,','@@agent_code,',GETDATE()) UPDATE TJ_SYS_YH set yxq=dbo.getdatestr1(DATEADD(month,@@months,,isnull(yxq,getdate()))),qyrq=getdate() where yhh='@@agent_code,' end";
	       
	       sql = sql.replaceAll("@@charge_amount,", String.valueOf(i_fee) );
	       sql = sql.replaceAll("@@months,", String.valueOf(months));
	        
	       //sql = sql.replaceAll("@@charge_memo,", "充值");
	       //sql = sql.replaceAll("@@charge_man,", "JSAPI");
	       sql = sql.replaceAll("@@payment_no,", transaction_id);
	      // sql = sql.replaceAll("@@open_id,", openid);
	       sql = sql.replaceAll("@@agent_code,", agent_code);
	      
	       try {
			SQLUpdater updater = DSManager.getSQLUpdater();
			updater.executeUpdate(sql);
			response.getWriter().write("<xml><return_code><![CDATA[SUCCESS]]></return_code><return_msg><![CDATA[OK]]></return_msg></xml>"); 
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	       
	      System.out.println("-------------");
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
