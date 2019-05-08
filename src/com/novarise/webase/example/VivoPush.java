package com.novarise.webase.example;





import java.util.TimerTask;

import com.alibaba.fastjson.JSONObject;
import com.novarise.webase.BConstants;
import com.novarise.webase.util.MD5Util;
import com.novarise.webase.xml.XmlUtil;
/**
 * vivo 推送
 * @author Admininistrator
 *
 */
public class VivoPush {
		
//		private static String appId = "10892";
//		private static String appKey = "ce939904-9d65-4fc0-ba20-536098f5de3e";
//		private static String appSecret = "abd29987-6528-464d-934f-e6468d3e8065";
	  private static  String accessToken  ;//下发通知消息的认证Token
	  private static String tokenUrl = "https://api-push.vivo.com.cn";
	  private static String authTokenUrl = "/message/auth"; // 获取下发通知消息的认证Token  的请求路径
	  private static String danUrl = "/message/send"; //单推接口
	 public static  void refreshToken() 
	    {
		 	String appId = "";
		 	String appKey = "";
			String appSecret  = "";
			try {
				appId = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.VIVOAPPID);
				appKey = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.VIVOAPPKEY);
				appSecret = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.VIVOAPPSECRET);
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			};
			
	        JSONObject  msgBody  = new JSONObject();
	        msgBody.put("appId",appId);
	        msgBody.put("appKey",appKey);
	        long timestamp = System.currentTimeMillis();
	        msgBody.put("timestamp",timestamp);
	           String sign=  appId + appKey + timestamp + appSecret;
	           
	           msgBody.put("sign",MD5Util.string2MD5(sign));
	        String response="";
			try {
				response = OppoHttpUtil.doPostJson(tokenUrl+authTokenUrl,msgBody.toString());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	        JSONObject obj = JSONObject.parseObject(response);
	     //   System.out.println("obj:"+obj);
	        
	        accessToken = obj.getString("authToken");
	        
	    }
	    
	 
	 public static String sendMessage(String order,String lxfs,String type,String title,String content)  {
	       
		 	JSONObject intent = new JSONObject();
		 	intent.put("type",type);
		 	intent.put("order_code",order);
		 	
	        JSONObject jsoncon = new JSONObject();
	        jsoncon.put("alias",lxfs);
	        jsoncon.put("notifyType","4");
	        jsoncon.put("title",title);
	        jsoncon.put("content",content);
	        jsoncon.put("timeToLive","86400");
	        jsoncon.put("skipType","4");
	        jsoncon.put("skipContent","intent://com.agent/notification?action="+intent+"#Intent;scheme=wonderfullpush;action=android.intent.action.VIEW;launchFlags=0x10000000;end");
	        jsoncon.put("requestId",accessToken);
	      //  jsoncon.put("authToken",accessToken);
	        String response = "";
			try {
				response = OppoHttpUtil.doVivoPost(tokenUrl+danUrl,jsoncon.toString(),accessToken);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		    JSONObject obj = JSONObject.parseObject(response);
		    System.out.println("obj:"+obj);
	        if(obj.getString("result").equals("0")){
	        	return "ok";
	        }
	        return obj.toJSONString();
	        
	    }
	 
	 public static void main(String[] args) throws Exception {
		 refreshToken();
		/* new java.util.Timer().schedule(new TimerTask(){
			 public void run() {
				 sendMessage("76543123456","18600000010","1","物流推送","您的订单76543123456已发货");
			
			 this.cancel();  //只延迟一次，用cancel方法取消掉．

			 }}, 500);*/
		 Thread.currentThread().sleep(500);
		 sendMessage("76543123456","18600000010","1","物流推送","您的订单76543123456已发货");
			
		 
	}
}
