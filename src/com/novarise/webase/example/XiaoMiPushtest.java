package com.novarise.webase.example;


import com.novarise.webase.BConstants;
import com.novarise.webase.xml.XmlUtil;
import com.xiaomi.xmpush.server.Constants;
import com.xiaomi.xmpush.server.Message;
import com.xiaomi.xmpush.server.Result;
import com.xiaomi.xmpush.server.Sender;

import net.sf.json.JSONObject;









import java.io.IOException;

import org.json.simple.parser.ParseException;
/**
 * author: Administrator
 * created on: 2017/11/6 10:53
 * description:
 */
public class XiaoMiPushtest {


	 /*   private static String APP_SECRET = "AJHOVx8oKSbsLoAaTceTGQ==";

	    private static String MY_PACKAGE_NAME = "com.example.iDengBao.PlaceOrder";
	   */
	
	
	
	
	
    public static String  sendMessage(String order,String lxfs,String type,String title ,String description) {
    	
//    	String APP_SECRET = "AJHOVx8oKSbsLoAaTceTGQ==";
//    	String MY_PACKAGE_NAME = "com.example.iDengBao.PlaceOrder";
    	 String APP_SECRET = "";
         String MY_PACKAGE_NAME = "";
     	try {
     		APP_SECRET =    XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.XIAOMIAPPSECRET);
     		MY_PACKAGE_NAME = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.XIAOMIMYPACKAGENAME);
 		} catch (Exception e) {
 			// TODO Auto-generated catch block
 			e.printStackTrace();
 		}
        Constants.useOfficial();
        Sender sender = new Sender(APP_SECRET);
        JSONObject jsoncon = new JSONObject();
        jsoncon.put("type",type);
        jsoncon.put("order_code",order);
        String messagePayload = "This is a message";
        String intent = "intent://com.agent/notification?action="+jsoncon+"#Intent;scheme=wonderfullpush;action=android.intent.action.VIEW;launchFlags=0x10000000;end";
      
      
       
        		
        Message message = new Message.Builder()
                .title(title)
                .description(description).payload(messagePayload)
                .restrictedPackageName(MY_PACKAGE_NAME)
                .passThrough(0)     // 设置消息是否通过透传的方式至App, 1表示透传消息, 0表示通知栏消息(默认是通知栏消息)
                .notifyType(-1)      // 设置通知类型, type类型(-1, 1-使用默认提示音提示, 2-使用默认震动提示, 3-使用默认led灯光提示)
                .extra(Constants.EXTRA_PARAM_NOTIFY_EFFECT, Constants.NOTIFY_ACTIVITY)
                .extra(Constants.EXTRA_PARAM_INTENT_URI, intent)
                .build();
      
        try {            

        	Result result= sender.sendToAlias(message, lxfs, 1);  //自定义别名
       	
        	if((result.getErrorCode().getValue())==0){
        		return "ok";
        	}else{
        		return "no";
        	}
          //  sender.send(message, "asasas", 1); //发送消息到一组设备上, regids个数不得超过1000个
          //  sender.sendToAlias(message, alias, 3); 
            //sender.broadcastAll(message, 1); //向所有设备发送消息，retries为发送失败之后重试的次数。
        } catch (IOException e) {
            e.printStackTrace();
    		return "no";
        } catch (ParseException e) {
            e.printStackTrace();
    		return "no";
        }
    }
  
    
 
}

