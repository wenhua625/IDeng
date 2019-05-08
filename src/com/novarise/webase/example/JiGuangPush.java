package com.novarise.webase.example;



import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
/*import org.slf4j.Logger;
import org.slf4j.LoggerFactory;*/


import com.alibaba.fastjson.JSONArray;
import com.novarise.webase.BConstants;
import com.novarise.webase.xml.XmlUtil;

import net.sf.json.JSONObject;
import sun.misc.BASE64Encoder;

/**
 * java后台极光推送方式一：使用Http API
 * 此种方式需要自定义http请求发送客户端:HttpClient
 */

public class JiGuangPush {
 //   private static final Logger log = LoggerFactory.getLogger(JiguangPush.class);
  
//    private static String appKey ="4e7af97f2b1bda3ee6251bb2";// "60521845a0e6639a24c8daf6";  
//    private static String masterSecret = "68f374c3657f2bffef9ee7e0";
    private static String pushUrl = "https://api.jpush.cn/v3/push";    
    private static boolean apns_production = true;    
    private static int time_to_live = 86400;
   // private static final String ALERT = "推送信息";  
    
    public static void main(String[] args) {
		//String alias="3B57A62C04F14AD6A61045D281C2F7AE";
    	String alias ="aimei868734031212470";
		//String alias="aimei99000692116276";
		String aa= buildPushObject_all_alias_alert(alias,"00000000000000000000","0" ,"你的订单一发货","物流信息");
		System.out.println("aa:"+aa);
	}

    /**
     * 极光推送
     * @param alias   别名
     * @param order_code  订单号
     * @param type   类型
     * @param title	 标题
     * @param ALERT  内容
     * @return
     */
    public static String  buildPushObject_all_alias_alert(String alias,String order_code,String type,String  title,String ALERT){
        
    	
        try{
        	String appKey = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.JGAPPKEY);
        	String masterSecret = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.JGMASTERSECRET);
        	
        	
            String result = push(pushUrl,alias,ALERT,appKey,masterSecret,apns_production,time_to_live,order_code, type,title);
            JSONObject resData = JSONObject.fromObject(result);
                if(resData.containsKey("error")){
                    System.out.println("针对别名为" + alias + "的信息推送失败！");
                    JSONObject error = JSONObject.fromObject(resData.get("error"));
                    System.out.println("错误信息为：" + error.get("message").toString());
                    return error.get("message").toString();
                }else{
                 	System.out.println("针对别名为" + alias + "的信息推送成功！"+resData);
                	return "ok";
                }
            
        }catch(Exception e){
        	System.out.println("针对别名为" + alias + "的信息推送失败！"+e);
            return  e.toString();
        }
    }
    
    /**
     * 组装极光推送专用json串
     * @param alias
     * @param alert
     * @return json
     */
    public static JSONObject generateJson(String alias,String alert,boolean apns_production,int time_to_live,String order_code,String type,String title){
        JSONObject json = new JSONObject();
        JSONArray platform = new JSONArray();//平台
        platform.add("android");
        platform.add("ios");
        
        JSONObject audience = new JSONObject();//推送目标
        JSONArray alias1 = new JSONArray();
        alias1.add(alias);
        audience.put("alias", alias1);
        
        JSONObject notification = new JSONObject();//通知内容
        JSONObject android = new JSONObject();//android通知内容
        android.put("alert", alert); 
        android.put("title", title);
        android.put("builder_id", 1);
        JSONObject android_extras = new JSONObject();//android额外参数
        android_extras.put("type", type);
        android_extras.put("order_code", order_code);
        
        android.put("extras", android_extras);
        
        JSONObject ios = new JSONObject();//ios通知内容
        ios.put("alert", alert);
        ios.put("sound", "default");
        ios.put("badge", "+1");
        JSONObject ios_extras = new JSONObject();//ios额外参数
        android_extras.put("type", type);
        android_extras.put("order_code", order_code);
        
        ios.put("extras", ios_extras);
        notification.put("android", android);
        notification.put("ios", ios);
        
        JSONObject options = new JSONObject();//设置参数
        options.put("time_to_live", Integer.valueOf(time_to_live));
        options.put("apns_production", apns_production);
        
        json.put("platform", platform);
        json.put("audience", audience);
        json.put("notification", notification);
        json.put("options", options);
        return json;
        
    }
    
    /**
     * 推送方法-调用极光API
     * @param reqUrl
     * @param alias
     * @param alert
     * @return result
     */
    public static String push(String reqUrl,String alias,String alert,String appKey,String masterSecret,boolean apns_production,int time_to_live,String order_code,String type,String title){
        String base64_auth_string = encryptBASE64(appKey + ":" + masterSecret);
        String authorization = "Basic " + base64_auth_string;
        return sendPostRequest(reqUrl,generateJson(alias,alert,apns_production,time_to_live,order_code,type,title).toString(),"UTF-8",authorization);
    }
    
    /**
     * 发送Post请求（json格式）
     * @param reqURL
     * @param data
     * @param encodeCharset
     * @param authorization
     * @return result
     */

    public static String sendPostRequest(String reqURL, String data, String encodeCharset,String authorization){
        HttpPost httpPost = new HttpPost(reqURL);
        HttpClient client = new DefaultHttpClient();
        HttpResponse response = null;
        String result = "";
        try {
             StringEntity entity = new StringEntity(data, encodeCharset);
             entity.setContentType("application/json");
             httpPost.setEntity(entity);
             httpPost.setHeader("Authorization",authorization.trim());
             response = client.execute(httpPost);
             result = EntityUtils.toString(response.getEntity(), encodeCharset);
        } catch (Exception e) {
        	System.out.println("请求通信[" + reqURL + "]时偶遇异常,堆栈轨迹如下"+e);  
        }finally{
            client.getConnectionManager().shutdown();
        }
        return result;
    }
     /** 
　　　　* BASE64加密工具
　　　　*/
     public static String encryptBASE64(String str) {
         byte[] key = str.getBytes();
       BASE64Encoder base64Encoder = new BASE64Encoder();
       String strs = base64Encoder.encodeBuffer(key);
         return strs;
     }
}
