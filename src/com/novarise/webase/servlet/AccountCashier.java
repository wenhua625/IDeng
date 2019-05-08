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
		//�����̻�������Ϣ
		request.setCharacterEncoding("UTF-8");
		String version = "1.0.0.0";
		String merId = "hyfsl";
		String password = "lsWKy5UXowmJWPdihmuOL9bqyDGbvJ76";
		String reqdata = "";
		String signature = "";
		//����ͳһ֧��ƽ̨���ص�����֪ͨ����
		try{
		reqdata = request.getParameter("reqdata").replaceAll(" ","+");
		System.out.println("reqData:"+reqdata);
		signature = request.getParameter("signature");
		System.out.println("signature:"+signature);
		}catch(Exception e){
			//e.printStackTrace();
			System.out.print("û����Ϣ����,ԭ��:"+e.toString());
			
		}
		//System.out.println("reqdata=" + reqdata);
		//System.out.println("signature=" + signature);
		if (reqdata == null || signature == null) {
			System.out.print("����ʧ�ܣ�������ϢΪ��!");
		} else {
			//��reqdata���н���ΪXML����
			byte[] decode = ReturnValue.base64dec(reqdata
					.getBytes("UTF-8"));
			String bt2 = new String(decode, "UTF-8");
		
			//��XML��ʽ��ȡreqdata����
			//���ķ���ʱ��yyyyMMddHHmmss �̻�����֤��ʱ����Լ�������ʱ����ǰ�󲻳���15����
			String transtime = "";
			//���ױ��
			String orderid = "";
			//�ܱ���
			String total = "";
			//����
			String batch = "";
			//���֧��ָ���տ���
			String cardno = "";
			//���׽��
			String transamt = "";
			//����ʱ��
			String transtime1 = "";
			//���ױ��
			String transno = "";
			//�����˺�
			String payacct="";
			//������
			String payname="";
			//��ұ��
			String userid="";
			//�����̻����
			String sellerid="";
			//��;
			String usage="";
			//ժҪ��Ϣ
			String abstractinfo="";
			//û��ȥ����ŵĶ������
			String orderidRev = "";
			//ȥ����ŵĶ������
			String orderid1="";
			

			try {
				SAXReader saxreader = new SAXReader();
				Document document = saxreader.read(new ByteArrayInputStream(bt2.getBytes("UTF-8")));
				//logger.debug(document);
				//������д��xml�ļ�
				XMLWriter writer = new XMLWriter(new FileWriter(dir+"onCountoutput.xml"));
				writer.write(document);
				writer.close();
				//System.out.println();
				//��ȡ����д��txt�ļ�
				Element root = document.getRootElement();//���ڵ�GYJANS
				//System.out.println("���ڵ㣺" + root.getName()); // �õ����ڵ������
				transtime = root.element("transtime").getText();//ʱ��
				//System.out.println("real-transtime2="+transtime);
		        transtime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());//����ʱ��
		      //  System.out.println("real-transtime="+transtime);
				if (Tools.checkTranstime(transtime)) {//ʱ����Լ�������ʱ����ǰ�󲻳���15����
					//orderidRev = root.element("orderid").getText();//�������
					orderid = root.element("orderid").getText();//���ױ��
					total = root.element("total").getText();//�ܱ���
					batch = root.element("batch").getText();//����
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
					
					//�������ĵ����ĸ�ʽ���ַ���,����md5����,���õ����ַ����ʹ��͹�����signature�ַ����Ƚ�,��ͬ����ǩͨ�� 
					String md5Signature = Tools.returnMd5(bt2, merId,orderidRev, "UTF-8", password, transtime);
					//System.out.println("md5Signature:" + md5Signature);
					
				
						if (md5Signature.equals(signature)) {//md5ǩ����֤
							try{
							String demo = "����";
							SQLUpdater updater = DSManager.getSQLUpdater();
                            String sql = "{ call sp_icbc_cwsk('"+transno+"','"+userid+"','"+transamt+"','"+demo+"','"+abstractinfo+"') };";
                            System.out.println("SQL:"+sql);
                            //updater.executeUpdate(sql);
                             response.setStatus(200);
                          
							}catch(Exception e){
							System.out.print("���ݲ����쳣"+"/\r/\n");
							System.out.print("ԭ��"+e.toString());
							
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
				System.out.println("û�н��ܵ�����"+"\r\n");
				System.out.println("ԭ��:"+e.toString());
			}
		
		
		
		
	}
		
	}

}


