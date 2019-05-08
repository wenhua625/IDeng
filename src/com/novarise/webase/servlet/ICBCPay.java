package com.novarise.webase.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import cn.com.infosec.icbc.ReturnValue;

import com.novarise.webase.util.Tools;

public class ICBCPay extends HttpServlet {

	/**
	 * Constructor of the object.
	 */
	public ICBCPay() {
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
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		
		ICBCPay pay= new ICBCPay();
		String version = "1.0.0.0";
		String merId = "test1";
		String transCode="GYJPAY";
		String  transtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//交易时间
		String orderid="XS2015010309";
		
		/*String GYJPAY="<GYJ>"+
				"<transtime>"+transtime+"</transtime>"+        //<!--和服务器时间间隔前后不超过15分钟,必输-->
				"<orderid>"+orderid+"</orderid>"+	           //<!--长度不超过35位,一个商户永不重复,必输-->
				"</GYJ>";*/

		String GYJPAY = "<GYJ>" + "<transtime>"+transtime+"</transtime>"
				+ "<orderid>"+"XS2015010309"+"</orderid>" + "<mername>test1</mername>"
				+ "<userid>2232</userid>" + "<username>浙江海洋店</username>"
				+ "<sellerid>二级商户编号</sellerid>"
				+ "<sellername>二级商户名称</sellername>"
				+ "<selleracct>B2B收款方账号</selleracct>"
				+ "<selleracctname>B2B收款方户名</selleracctname>"
				+ "<selleracctc>C2C收款方账号</selleracctc>"
				+ "<selleracctnamec>C2C收款方账号</selleracctnamec>"
				+ "<settleacct>清算商户代码</settleacct>"
				+ "<payamount>10</payamount>" + "<jumpurl>http://www.baidu.com</jumpurl>"
				+ "<noticeurl>http://fsilon.dderp.cn</noticeurl>" + "<goodsid>商品编号</goodsid>"
				+ "<goodsinfo>商品信息</goodsinfo>" + "<amtinfo>金额信息</amtinfo>"
				+ "<b2bdate>订单时间</b2bdate>" + "<mervar>商户自定义字段</mervar>"
				+ "<enabledpmd>1100000000000</enabledpmd>"
				+ "<installment>是否分期</installment>" + "<remark1>备注字段</remark1>"
				+ "<remark2>备注字段</remark2>" + "<remark3>备注字段</remark3>"
				+ "</GYJ>";

	
	byte[] tranbyte;
	try {
		tranbyte = GYJPAY.getBytes("utf-8");
		byte[] tranbyte2 = ReturnValue.base64enc(tranbyte);
		String b64Reqdata = new String(tranbyte2, "utf-8");
		b64Reqdata = b64Reqdata.replaceAll("[\n\r]", "");//base64编码(xml报文)
		
		
		String signature=Tools.sendMd5(version, merId, transCode, GYJPAY, "utf-8", "111111", transtime);
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("version", version);// 接口版本,1.0.0.0
		paramMap.put("merid", merId);// 商户ID
		paramMap.put("trancode", transCode);// 交易名
		paramMap.put("reqdata", b64Reqdata);// 交易对应的报文,根节点为GYJ，并进行base64编码
		paramMap.put("signature", signature);// md5签名
		paramMap.put("charset", "utf-8");// 编码格式
		//GYJODRQRY
		String respStr = pay.getResponseStr("http://web.zj.icbc.com.cn/cashiertest/gyj.pay",paramMap);
		System.out.println("res"+respStr);
		// 获取返回的报文内容reqdata和md5签名结果，存入retStrContent数组中
		String[] retStrContent = respStr.split("GYJSEPARATOR");
		if (retStrContent.length != 2) {
			System.out.println("统一支付平台返回信息不全！");
		}else {
			String reqdataR = retStrContent[0].substring(retStrContent[0].indexOf("=") + 1);
			String signatureR = retStrContent[1];
			//System.out.println("reqdata=" + reqdataR);
			//System.out.println("signature=" + signatureR);
			
			// 对reqdata进行解码为XML明文
			byte[] jiema = ReturnValue.base64dec(reqdataR
					.getBytes("UTF-8"));
			String bt2 = new String(jiema, "UTF-8");
			System.out.println("reqdata.解密:" + bt2);
			
			Map a=pay.xmlElements(bt2);
			System.out.println("trans="+a.get("transtime"));
			
			
			
			//root.get
		}
		
		
		
	} catch (UnsupportedEncodingException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
		
		
		out.flush();
		out.close();
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
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		out.println("<HTML>");
		out.println("  <HEAD><TITLE>A Servlet</TITLE></HEAD>");
		out.println("  <BODY>");
		out.print("    This is ");
		out.print(this.getClass());
		out.println(", using the POST method");
		out.println("  </BODY>");
		out.println("</HTML>");
		out.flush();
		out.close();
	}
	
	
	/**
	 * 获取统一支付平台返回报文
	 * 
	 * @param configHm
	 *            配置文件Map
	 * @param paramMap
	 *            报文参数Map
	 * @return
	 */
	private String getResponseStr(String  url,Map<String,String> paramMap){
		String respStr="";
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(url
				.toString());
		method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET,"UTF-8");
		Iterator iterator = paramMap.keySet().iterator();

		while (iterator.hasNext()) {
			String key = (String) iterator.next();
			method.setParameter(key, (String) paramMap.get(key));
		}
		int statusCode;
		// 设置超时时间
		client.getHttpConnectionManager().getParams()
				.setConnectionTimeout(5000);
		try {
			statusCode = client.executeMethod(method);
			if (statusCode != HttpStatus.SC_OK) {
				System.out.println("Method failed: " + method.getStatusLine());
			}
			respStr = new String(method.getResponseBodyAsString().getBytes("UTF-8"), "UTF-8");
		} catch (HttpException e) {
			System.out.println("Please check your provided http address!");
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 释放连接
			method.releaseConnection();
		}
		return respStr;
	}
	
	
	
	/**
	
	 * @Description 把xml格式字符串转化成List<Map>集合
	
	 * @author 漫画-temdy
	
	 * @Date 2014-11-19
	
	 * @param xmlDoc xml格式字符串

	 * @return
	
	 */
	
	public static Map xmlElements(String xmlDoc) {
	
		Map<String,String> nodeMap=new HashMap<String,String>();  
	
	    try {
	
	    	SAXReader saxreader = new SAXReader();
	       // 通过输入源构造一个Document
	      Document doc = saxreader.read(new ByteArrayInputStream(xmlDoc.getBytes("UTF-8")));
	      // 取的根元素
	      Element root =doc.getRootElement();
	      
	      
	      
	      List elementList = root.elements();    
	      
	      
          Iterator child=elementList.iterator();  
            
          while(child.hasNext()){  
               //将每个属性转化为一个抽象属性，然后获取其名字和值     
              Element aa=(Element) child.next();  
              nodeMap.put(aa.getName(),root.elementText(aa.getName()));  
          }  
	      
	     
	    } catch (Exception e) {
	
	        e.printStackTrace();
	
	        return null;
	
	    }
	
	    return nodeMap;
	
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
