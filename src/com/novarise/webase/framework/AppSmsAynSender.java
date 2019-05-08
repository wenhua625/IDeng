package com.novarise.webase.framework;

import java.sql.SQLException;

import com.novarise.webase.BConstants;
import com.novarise.webase.example.KccPush;
import com.novarise.webase.xml.XmlUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;
import com.ripple.datasource.SQLUpdater;

public class AppSmsAynSender{
    
	
	String msgcontent = "";
	
	/*public void setMsgContent(String msgcontent)
	{
	   this.msgcontent = msgcontent;	
	}*/
	
    public void sendMsg(String _msgtoken,String _msgtype,String _msgtel,String _sendtel,String _appname,String _appkey,String _masterSecret,String _msgcontent,String _ordercode){
    	
    	
    	String msgurl="";
    	String msgtype=_msgtype;
    	String msgtel=_msgtel;
    	String msgtoken=_msgtel;
    	String appname=_appname;
    	String msgtitle="";
    	String sendtel=_sendtel;
    	String appkey = _appkey;
    	String masterSecret = _masterSecret;
    	String ordercode=_ordercode;
    	this.msgcontent = _msgcontent;
		
    	 String colamount = null;
		 String coltype =  null;
		try {
			
			
			if(msgtype.equals("收款通知")){
				coltype =_msgtoken;
				colamount = _sendtel;
				sendtel = "";
				double d ;
				d=Double.parseDouble(colamount);
				if( d < 0){
					coltype = "退款";
				}
				
				
			}
			
			
			
			
			
			SQLUpdater updater = DSManager.getSQLUpdater();
			String jz_sql = "insert into msglist(msgtitle,msgcontent,msgdate,msgurl,msgtype,msgtel,msgtoken,sendtel,appname,order_code,arr_man,Arr_Tel,arr_address,col_amount,col_memo) "+
	                 "select '"+msgtitle+"','"+msgcontent+"',getdate(),'"+msgurl+"','"+msgtype+"','"+msgtel+"','"+msgtoken+"','"+sendtel+"','"+appname+"','"+ordercode+"' ,arr_man,Arr_Tel,ISNULL(arr_address,'')+ISNULL(arr_address_detail,'') ,'"+colamount+"','"+coltype+"' from orderlist where ORDER_CODE= '"+ordercode+"' ";	
				System.out.println(jz_sql);
			
			 
				 updater.executeUpdate(jz_sql);
			
			
			String sql="";
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(msgtoken.length()>0)
		{
//		   
//		   if(result.equals("0")){
//			 System.out.println("消息推送失败:"+msgtoken+"  "+msgcontent);
//		   }
			
			
			KccPush kcc= new KccPush();
			kcc.pushPayment(msgtel, "", "" ,msgtype,msgcontent);//
			
			
		}
		
    }
    
public void sendMsgForSql(String sql){
	
	
    
		try {
//			String aglappkey = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.AGLAPPKEY);
//			String aglappsecret = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.AGLAPPSECRET);
//			String kccappkey = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.KCCAPPKEY);
//			String kccappsecret = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.KCCAPPSECRET);
//			
			
			SQLUpdater updater = DSManager.getSQLUpdater();
			SQLQuery query = DSManager.getSQLQuery();
			String nr[][]= query.ArrayQuery(sql);
			String appkey1 ="";
			String masterSecret1 = "";
			for(int i=0;i<nr.length;i++)
			{
				 String tsnr = nr[i][0];
				 String device = nr[i][1];
				// String device = nr[i][2];
				 String  msgTel = nr[i][2];
				/* String appkey1 = nr[0][2];
				 String masterSecret1 = nr[0][3];*/
				 String sendTel = nr[i][3];
				 String appname1 = nr[i][4];
				 String ordercode = nr[i][5];
				 String msgtype=nr[i][6];
				 
//				 if (appname1.equals("艾管理"))
//				 {
//					appkey1=aglappkey;
//					masterSecret1=aglappsecret;
//				 }else
//				 {
//					 appkey1=kccappkey;
//				     masterSecret1=kccappsecret; 
//				 }
				 
				 this.sendMsg(device, msgtype, msgTel, sendTel, appname1, appkey1, masterSecret1,tsnr,ordercode);
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("sql:"+sql);
			e.printStackTrace();
		}
		
		
    }



}
