package com.novarise.webase.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;

public class APITest {
     protected static String url = "http://gw.api.tbsandbox.com/router/rest";//沙箱环境调用地址
     //正式环境需要设置为:http://gw.api.taobao.com/router/rest
     protected static String appkey = "12129701";
     protected static String appSecret = "ae4cb0e3fef76b52b5db85f13a7c1747";
     public static void testUserGet() {
    	  String s_time=new Long(System.currentTimeMillis()).toString();
    	  
          String sign=appkey+"app_key"+appkey+"method"+"taobao.itemcats.get"+"timestap"+s_time+"v2.0"+"parent_cid"+"29"+"fields"+"cid,parent_cid,name,is_parent"+"formatjson"+appkey;
          sign = MD5(sign);
          String param="sign="+sign.toUpperCase()+"&timestamp="+s_time+"&v=2.0&app_key="+appkey+"&method=taobao.itemcats.get&format=json&parent_cid=29&fields=cid,parent_cid,name,is_parent";
          String t_url=url+"?"+param;
          System.out.println(t_url);
          try {
			System.out.println(send_url(t_url,""));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
          System.out.println(sign.toUpperCase());
     }
     
     public final static String MD5(String s){
         char hexDigits[] = {
             '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
             'e', 'f'};
         try {
           byte[] strTemp = s.getBytes();
           MessageDigest mdTemp = MessageDigest.getInstance("MD5");
           mdTemp.update(strTemp);
           byte[] md = mdTemp.digest();
           int j = md.length;
           char str[] = new char[j * 2];
           int k = 0;
           for (int i = 0; i < j; i++) {
             byte byte0 = md[i];
             str[k++] = hexDigits[byte0 >>> 4 & 0xf];
             str[k++] = hexDigits[byte0 & 0xf];
             }
             return new String(str);
           }
           catch (Exception e){
             return null;
           }
    }
     
     public  static String send_url(String urlStr, String param) throws Exception {
         StringBuilder tempStr;
         String result="";
         HttpURLConnection url_con=null;
         try {
             URL url = new URL(urlStr);
             url_con = (HttpURLConnection) url.openConnection();
             url_con.setRequestMethod("POST");
             url_con.setDoOutput(true);

             url_con.getOutputStream().write(param.getBytes());
             url_con.getOutputStream().flush();
             url_con.getOutputStream().close();
             InputStream in = url_con.getInputStream();
             BufferedReader rd = new BufferedReader(new InputStreamReader(in));
             tempStr = new StringBuilder();
             
             while ((result=rd.readLine()) != null) {
             	
                 tempStr.append(result);
             }

         } finally {
             if (url_con != null)
                 url_con.disconnect();
         }
         return new String(tempStr);
     }
     public static void main(String[] args) {
    	 APITest.testUserGet();
    	 //try {
			//System.out.print(send_url("http://gw.api.tbsandbox.com/router/rest?sign=1775EE32D0E46AD354C98F712681188D&timestamp=2012-12-06+15%3A15%3A32&v=2.0&app_key=1012129701&method=taobao.itemcats.get&partner_id=top-apitools&format=json&parent_cid=29&fields=cid,parent_cid,name,is_parent",""));
		//} catch (Exception e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
		//}
     }
 
}
