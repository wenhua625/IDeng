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
		String  transtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//����ʱ��
		String orderid="XS2015010309";
		
		/*String GYJPAY="<GYJ>"+
				"<transtime>"+transtime+"</transtime>"+        //<!--�ͷ�����ʱ����ǰ�󲻳���15����,����-->
				"<orderid>"+orderid+"</orderid>"+	           //<!--���Ȳ�����35λ,һ���̻������ظ�,����-->
				"</GYJ>";*/

		String GYJPAY = "<GYJ>" + "<transtime>"+transtime+"</transtime>"
				+ "<orderid>"+"XS2015010309"+"</orderid>" + "<mername>test1</mername>"
				+ "<userid>2232</userid>" + "<username>�㽭�����</username>"
				+ "<sellerid>�����̻����</sellerid>"
				+ "<sellername>�����̻�����</sellername>"
				+ "<selleracct>B2B�տ�˺�</selleracct>"
				+ "<selleracctname>B2B�տ����</selleracctname>"
				+ "<selleracctc>C2C�տ�˺�</selleracctc>"
				+ "<selleracctnamec>C2C�տ�˺�</selleracctnamec>"
				+ "<settleacct>�����̻�����</settleacct>"
				+ "<payamount>10</payamount>" + "<jumpurl>http://www.baidu.com</jumpurl>"
				+ "<noticeurl>http://fsilon.dderp.cn</noticeurl>" + "<goodsid>��Ʒ���</goodsid>"
				+ "<goodsinfo>��Ʒ��Ϣ</goodsinfo>" + "<amtinfo>�����Ϣ</amtinfo>"
				+ "<b2bdate>����ʱ��</b2bdate>" + "<mervar>�̻��Զ����ֶ�</mervar>"
				+ "<enabledpmd>1100000000000</enabledpmd>"
				+ "<installment>�Ƿ����</installment>" + "<remark1>��ע�ֶ�</remark1>"
				+ "<remark2>��ע�ֶ�</remark2>" + "<remark3>��ע�ֶ�</remark3>"
				+ "</GYJ>";

	
	byte[] tranbyte;
	try {
		tranbyte = GYJPAY.getBytes("utf-8");
		byte[] tranbyte2 = ReturnValue.base64enc(tranbyte);
		String b64Reqdata = new String(tranbyte2, "utf-8");
		b64Reqdata = b64Reqdata.replaceAll("[\n\r]", "");//base64����(xml����)
		
		
		String signature=Tools.sendMd5(version, merId, transCode, GYJPAY, "utf-8", "111111", transtime);
		
		Map<String,String> paramMap = new HashMap<String,String>();
		paramMap.put("version", version);// �ӿڰ汾,1.0.0.0
		paramMap.put("merid", merId);// �̻�ID
		paramMap.put("trancode", transCode);// ������
		paramMap.put("reqdata", b64Reqdata);// ���׶�Ӧ�ı���,���ڵ�ΪGYJ��������base64����
		paramMap.put("signature", signature);// md5ǩ��
		paramMap.put("charset", "utf-8");// �����ʽ
		//GYJODRQRY
		String respStr = pay.getResponseStr("http://web.zj.icbc.com.cn/cashiertest/gyj.pay",paramMap);
		System.out.println("res"+respStr);
		// ��ȡ���صı�������reqdata��md5ǩ�����������retStrContent������
		String[] retStrContent = respStr.split("GYJSEPARATOR");
		if (retStrContent.length != 2) {
			System.out.println("ͳһ֧��ƽ̨������Ϣ��ȫ��");
		}else {
			String reqdataR = retStrContent[0].substring(retStrContent[0].indexOf("=") + 1);
			String signatureR = retStrContent[1];
			//System.out.println("reqdata=" + reqdataR);
			//System.out.println("signature=" + signatureR);
			
			// ��reqdata���н���ΪXML����
			byte[] jiema = ReturnValue.base64dec(reqdataR
					.getBytes("UTF-8"));
			String bt2 = new String(jiema, "UTF-8");
			System.out.println("reqdata.����:" + bt2);
			
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
	 * ��ȡͳһ֧��ƽ̨���ر���
	 * 
	 * @param configHm
	 *            �����ļ�Map
	 * @param paramMap
	 *            ���Ĳ���Map
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
		// ���ó�ʱʱ��
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
			// �ͷ�����
			method.releaseConnection();
		}
		return respStr;
	}
	
	
	
	/**
	
	 * @Description ��xml��ʽ�ַ���ת����List<Map>����
	
	 * @author ����-temdy
	
	 * @Date 2014-11-19
	
	 * @param xmlDoc xml��ʽ�ַ���

	 * @return
	
	 */
	
	public static Map xmlElements(String xmlDoc) {
	
		Map<String,String> nodeMap=new HashMap<String,String>();  
	
	    try {
	
	    	SAXReader saxreader = new SAXReader();
	       // ͨ������Դ����һ��Document
	      Document doc = saxreader.read(new ByteArrayInputStream(xmlDoc.getBytes("UTF-8")));
	      // ȡ�ĸ�Ԫ��
	      Element root =doc.getRootElement();
	      
	      
	      
	      List elementList = root.elements();    
	      
	      
          Iterator child=elementList.iterator();  
            
          while(child.hasNext()){  
               //��ÿ������ת��Ϊһ���������ԣ�Ȼ���ȡ�����ֺ�ֵ     
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
