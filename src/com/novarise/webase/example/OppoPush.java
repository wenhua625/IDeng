package com.novarise.webase.example;



import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.codec.binary.Hex;

import com.alibaba.fastjson.JSONObject;
import com.novarise.webase.BConstants;
import com.novarise.webase.xml.XmlUtil;

/**
 * oppo 推送
 * @author Admininistrator
 *
 */
public class OppoPush {
		private static String  oppoUrl = "https://api.push.oppomobile.com/";
	/*	private static String  appKey = "90jmx6m9b688c00c4C4Cck8Sg";
		private static String  mastersecret  = "11d86c5bf68f0C9417905Deee6d89a7C";*/
	
		private static String  authTokenUrl = "server/v1/auth";  //鉴权
		private static String  danTuiUrl ="server/v1/message/notification/unicast";   //单推
		private static long tokenExpiredTime ;
		private static String  authToken ;
		
		/**
		 * 获取oppo token
		 * @return
		 */
		 private static boolean refreshToken() {  
			 
			 
			 String appKey = "";
		        String mastersecret = "";
		    	try {
		    		appKey = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.OPPOAPPKEY);
		    		mastersecret =    XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.OPPOMASTERSECRET);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			 
			 String timestamp = ""+ System.currentTimeMillis();   
			 String sign=appKey + timestamp + mastersecret;
			 String sha256= sha256(sign); 
			 String msgBody;
			 String response ="";
			try {
				msgBody = MessageFormat.format(            
						 "app_key={0}&sign={1}&timestamp={2}",            
						 URLEncoder.encode(appKey,   "UTF-8"),            
						 URLEncoder.encode(sha256,    "UTF-8"),            
						 URLEncoder.encode(timestamp, "UTF-8")); 
				response = OppoHttpUtil.doPostOppo(oppoUrl+authTokenUrl, msgBody); 
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     
			   
		//	 System.out.println("response="+response);    
			 if (response.indexOf("Not Allowed") > 0)    {        
				 return false;    
			 }     
			 JSONObject obj = JSONObject.parseObject(response).getJSONObject("data");    
			// System.out.println("JSONObject="+obj);    
			 authToken = obj.getString("auth_token");   
			 tokenExpiredTime = 24*60*60*1000+obj.getLong("create_time") - 5*60*1000;    
			 return true;
			 }
		
		   /**
		     *  利用Apache的工具类实现SHA-256加密
		     * @param sign 要加密的数据
		     * @return 加密后的报文
		     */
		private static String sha256(String sign) {
			// TODO Auto-generated method stub
			 MessageDigest messageDigest;        
			 String encdeStr = "";        
			 try {            
				 messageDigest = MessageDigest.getInstance("SHA-256");            
				 byte[] hash = messageDigest.digest(sign.getBytes("UTF-8"));            
				 encdeStr = Hex.encodeHexString(hash);        
			} catch (NoSuchAlgorithmException e) {            
				e.printStackTrace();        
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();        
			}        
			 return encdeStr;
			
		}
		
		

public static  String  sendNcPushMessage(String token, String order,String type, String username,String mobile ) {

    if (tokenExpiredTime <= System.currentTimeMillis()){
        refreshToken();
    }
    /*
    String username = "物流信息" ;
    String mobile = "您的订单"+order+"已发货，请注意查收";*/
    
    
    JSONObject intent = new JSONObject();
 	intent.put("type",type);
 	intent.put("order_code",order);
    
    JSONObject notification = new JSONObject();
    notification.put("title",      username );//消息标题
    notification.put("sub_title",   mobile.length() > 10 ? mobile.substring(0, 10) : mobile);
    notification.put("content",     mobile);//消息内容体
    notification.put("click_action_type", 4);
   // notification.put("click_action_activity","intent://com.agent/notification?action="+intent+"#Intent;scheme=wonderfullpush;action=android.intent.action.VIEW;launchFlags=0x10000000;end");
    notification.put("click_action_activity","com.agent.HWPushTranslateActivity");
    
    JSONObject message = new JSONObject();
    message.put("target_type",      2);
    message.put("target_value",     token);
    message.put("notification",     notification); 

    //postBody之后不需要再编码。

    
    String response ="";
	try {
		String postBody = MessageFormat.format("auth_token={0}&message={1}",
            authToken,
            URLEncoder.encode(message.toString(), "UTF-8"));
		response = OppoHttpUtil.doPostOppo(oppoUrl+danTuiUrl, postBody);
	//	System.out.println("response:"+response);
		JSONObject jsonObject = JSONObject.parseObject(response);
		if(jsonObject.getString("code").equals("0")){
			return "ok";
		}else{
			return "no";
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
		return "no";
	}

}

		
		
		
		public static void main(String[] args) throws Exception {
			//refreshToken();
			String order="XS0000002";
			String  token="CN_efda444f84c451050b9195376d806359";
			String username="物流信息";
			String mobile= "你的订单已发货";
			String result = sendNcPushMessage( token,  order,"0",  username, mobile );
			System.out.println("result:"+result);
		}
		
}
