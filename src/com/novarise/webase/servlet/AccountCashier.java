package com.novarise.webase.servlet;


import java.io.ByteArrayInputStream;
import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

import cn.com.infosec.icbc.ReturnValue;

import com.novarise.webase.util.Tools;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLUpdater;

public class AccountCashier extends HttpServlet {
	public void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	    Logger logger = Logger.getLogger(AccountCashier.class);
	    response.setContentType("text/html;charset=GBK");
		//PrintWriter out  = response.getWriter(); 
		
		String path = request.getRealPath(getServletName());
		String dir = new File(path).getParent();
		//System.out.println(dir);
		//设置商户基本信息
		request.setCharacterEncoding("UTF-8");
		String version = "1.0.0.0";
		String merId = "hyfsl";
		String password = "lsWKy5UXowmJWPdihmuOL9bqyDGbvJ76";
		String reqdata = "";
		String signature = "";
		//接收统一支付平台返回的主动通知报文
		try{
		reqdata = request.getParameter("reqdata").replaceAll(" ","+");
		System.out.println("reqData:"+reqdata);
		signature = request.getParameter("signature");
		System.out.println("signature:"+signature);
		}catch(Exception e){
			//e.printStackTrace();
			System.out.print("没有信息返回,原因:"+e.toString());
			
		}
		//System.out.println("reqdata=" + reqdata);
		//System.out.println("signature=" + signature);
		if (reqdata == null || signature == null) {
			System.out.print("接收失败，报文信息为空!");
		} else {
			//对reqdata进行解码为XML明文
			byte[] decode = ReturnValue.base64dec(reqdata
					.getBytes("UTF-8"));
			String bt2 = new String(decode, "UTF-8");
		
			//以XML方式读取reqdata数据
			//报文返回时间yyyyMMddHHmmss 商户需验证该时间和自己服务器时间间隔前后不超过15分钟
			String transtime = "";
			//交易编号
			String orderid = "";
			//总笔数
			String total = "";
			//批次
			String batch = "";
			//汇款支付指定收款帐
			String cardno = "";
			//交易金额
			String transamt = "";
			//交易时间
			String transtime1 = "";
			//交易编号
			String transno = "";
			//付款账号
			String payacct="";
			//付款人
			String payname="";
			//买家编号
			String userid="";
			//二级商户编号
			String sellerid="";
			//用途
			String usage="";
			//摘要信息
			String abstractinfo="";
			//没有去掉序号的订单编号
			String orderidRev = "";
			//去掉序号的订单编号
			String orderid1="";
			

			try {
				SAXReader saxreader = new SAXReader();
				Document document = saxreader.read(new ByteArrayInputStream(bt2.getBytes("UTF-8")));
				//logger.debug(document);
				//将回文写入xml文件
				XMLWriter writer = new XMLWriter(new FileWriter(dir+"onCountoutput.xml"));
				writer.write(document);
				writer.close();
				//System.out.println();
				//读取回文写入txt文件
				Element root = document.getRootElement();//根节点GYJANS
				//System.out.println("根节点：" + root.getName()); // 拿到根节点的名称
				transtime = root.element("transtime").getText();//时间
				//System.out.println("real-transtime2="+transtime);
		        transtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//交易时间
		      //  System.out.println("real-transtime="+transtime);
				if (Tools.checkTranstime(transtime)) {//时间和自己服务器时间间隔前后不超过15分钟
					//orderidRev = root.element("orderid").getText();//订单编号
					orderid = root.element("orderid").getText();//交易编号
					total = root.element("total").getText();//总笔数
					batch = root.element("batch").getText();//批次
					if(root.element("rds")!=null){
						Element rd = root.element("rds").element("rd");
						if(rd!=null){
							for ( Iterator iterInner = rd.elementIterator(); iterInner.hasNext(); ){
								Element elementInner = (Element) iterInner.next();
								if("cardno".equals(elementInner.getName())){
									cardno = elementInner.getText(); 
								}
								if("transamt".equals(elementInner.getName())){
									transamt = elementInner.getText(); 
								}
								if("transtime".equals(elementInner.getName())){
									transtime1 = elementInner.getText(); 
								}
								if("transno".equals(elementInner.getName())){
									transno = elementInner.getText(); 
								}
								if("payacct".equals(elementInner.getName())){
									payacct = elementInner.getText(); 
								}
								if("payname".equals(elementInner.getName())){
									payname = elementInner.getText(); 
								}
								if("userid".equals(elementInner.getName())){
									userid = elementInner.getText(); 
								}
								if("sellerid".equals(elementInner.getName())){
									sellerid = elementInner.getText(); 
								}
								if("usage".equals(elementInner.getName())){
									usage = elementInner.getText(); 
								}
								if("abstractinfo".equals(elementInner.getName())){
									abstractinfo = elementInner.getText(); 
								}if("orderid".equals(elementInner.getName())){
									orderidRev = elementInner.getText(); 
									orderid1 = orderidRev;
								}
								
								
							}
						}
					}
					
					//按开发文档给的格式组字符串,进行md5编码,将得到的字符串和传送过来的signature字符串比较,相同则验签通过 
					String md5Signature = Tools.returnMd5(bt2, merId,orderidRev, "UTF-8", password, transtime);
					//System.out.println("md5Signature:" + md5Signature);
					
				
						if (md5Signature.equals(signature)) {//md5签名验证
							try{
							String demo = "货款";
							SQLUpdater updater = DSManager.getSQLUpdater();
                            String sql = "{ call sp_icbc_cwsk('"+transno+"','"+userid+"','"+transamt+"','"+demo+"','"+abstractinfo+"') };";
                            System.out.println("SQL:"+sql);
                            //updater.executeUpdate(sql);
                             response.setStatus(200);
                          
							}catch(Exception e){
							System.out.print("数据插入异常"+"/\r/\n");
							System.out.print("原因"+e.toString());
							
							}
				  
				  
				 
						}
				else {
					System.out.print("Signature error !");
						}
					}
				 else {
					 System.out.print("Transtime error !");
				}

			} catch (Exception e) {
				
				//e.printStackTrace();
				System.out.println("没有接受到回文"+"\r\n");
				System.out.println("原因:"+e.toString());
			}
		
		
		
		
	}
		
	}

}


