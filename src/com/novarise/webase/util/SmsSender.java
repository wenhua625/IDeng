package com.novarise.webase.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;





public class SmsSender {
//	自身类
	static SmsSender smwps=null;
	//内部属性
	private static URL url;
	private static HttpURLConnection url_con;
	private static String sms_url;
	private static String sms_user;
	private static String sms_pwd;
	private static String sms_type;
	private static String sms_signName="";
	
	
//	数据库柄
	//static SQLQuery query=null;
	//static SQLUpdater updater =null; 
	
	
//构造类的实例，只产生一个对象实例
	
	public static SmsSender newInstance(String sms_url,String sms_user,String sms_pwd,String sms_type) {
		if(smwps==null){
			smwps=new SmsSender();
			smwps.sms_url=sms_url;
			smwps.sms_user=sms_user;
			smwps.sms_pwd=sms_pwd;
			smwps.sms_type=sms_type;
			
		}
		return smwps;
	}
	/**
	 * 短信发送，传入短信内容和号码，
	 * @param nr 内容
	 * @param hm 号码，多个号码用,分隔
	 * @return
	 * @throws Exception
	 */
	public static String send_url( String nr,String hm) throws Exception {
        StringBuilder tempStr;
        //北京亿度
        //String param="zh="+sms_user+"&mm="+sms_pwd+"&dxlbid="+sms_type;
        //param+= "&nr="+nr+"&hm="+hm;
        //杭州普讯
        //String param="usr="+sms_user+"&pwd="+sms_pwd+"&extdsrcid="+sms_type;
        //param+= "&sms="+nr+"&mobile="+hm;
        //上海移通
       /* nr = new String(Hex.encodeHex(nr.getBytes("GBK")));
        String command="MULTI_MT_REQUEST";//"MULTI_MT_REQUEST";
        String param="spid="+sms_user+"&sppassword="+sms_pwd+"&dc=15"+"&command="+command;
        param+= "&sm="+nr+"&das="+hm;*/
        //北京有信通
       // nr = java.net.URLEncoder.encode(nr); //new String(Hex.encodeHex(nr.getBytes("GBK")));
        nr = sms_signName+nr;
        try {
        	nr =java.net.URLEncoder.encode(nr,"utf-8");
		} catch (UnsupportedEncodingException e) {
			//value="";
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        //String command="MULTI_MT_REQUEST";//"MULTI_MT_REQUEST";
        //String param="username="+sms_user+"&password="+sms_pwd+"&dc=15";
        //param+= "&content="+nr+"&mobile="+hm;
        
        //发送http请求，并接收http响应
        String sms_url1=sms_url;
        //String sms_url1=sms_url;
       // System.out.println(sms_url1);
        Map params=new HashMap();
        params.put("username", sms_user);
        params.put("password", sms_pwd);
        params.put("dc", "15");
        params.put("content", nr);
        params.put("mobile", hm);
        
        //String resStr = doPostRequest(sms_url1,params);
        String resStr = doPost(sms_url1,params);
        
        //解析响应字符串
        //上海移通
        /*HashMap<String,String> pp = parseResStr(resStr);
        
        String rst_sts=pp.get("mterrcode");
        
        return rst_sts;*/
        return resStr;
    }
	
    public static HttpPostModel submitByPost(String urlAddress, Map<String, String> map) {
		
		HttpPostModel model=new HttpPostModel();
		StringBuffer params = new StringBuffer();

		Iterator<Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String, String> element = (Entry<String, String>) it.next();
			params.append(element.getKey());
			params.append("=");
			params.append(element.getValue());
			params.append("&");
		}

		if (params.length() > 0) {
			params.deleteCharAt(params.length() - 1);
		}
		
		HttpURLConnection conn = null;
		try {
			URL url = new URL(urlAddress);
			conn = (HttpURLConnection) url.openConnection();

			conn.setDoOutput(true);
			conn.setRequestMethod("POST");
			conn.setUseCaches(false);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			conn.setRequestProperty("Content-Length", String.valueOf(params.length()));
			conn.setDoInput(true);
			conn.connect();

			OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
			out.write(params.toString());
			out.flush();
			out.close();
			model.set_ResponseCode(conn.getResponseCode()); 
			
			BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			
			model.set_ResponseString(br.readLine());
			
			br.close();			
			
		} catch (Exception ex) {
			System.out.println("url:"+urlAddress);
			ex.printStackTrace();
		} finally {
			conn.disconnect();
		}
		return model;
	}
	
	public static String send_batch_url( String nr,String hm) throws Exception {
        StringBuilder tempStr;
        //北京亿度
        //String param="zh="+sms_user+"&mm="+sms_pwd+"&dxlbid="+sms_type;
        //param+= "&nr="+nr+"&hm="+hm;
        //杭州普讯
        //String param="usr="+sms_user+"&pwd="+sms_pwd+"&extdsrcid="+sms_type;
        //param+= "&sms="+nr+"&mobile="+hm;
        //上海移通
        nr = new String(Hex.encodeHex(nr.getBytes("GBK")));
        String command="BATCH_MT_REQUEST";//"MULTI_MT_REQUEST";
        String param="spid="+sms_user+"&sppassword="+sms_pwd+"&dc=15"+"&command="+command;
        param+="&bmttype=1"  + "&sm=" + nr + "&url=" + hm + "&priority=1";
        //param+= "&sm="+nr+"&das="+hm;
        
        //发送http请求，并接收http响应
        String sms_url1=sms_url+"?"+param;
        System.out.println(sms_url1);
        String resStr = doGetRequest(sms_url1);
        
        //解析响应字符串
        //上海移通
        HashMap<String,String> pp = parseResStr(resStr);
        
        String rst_sts=pp.get("mterrcode");
        
        return rst_sts;
    }
	
	 /**
     * 将 短信下行 请求响应字符串解析到一个HashMap中
     * @param resStr
     * @return
     */
    public static HashMap parseResStr(String resStr) {
        HashMap pp = new HashMap();
        try {
            String[] ps = resStr.split("&");
            for (int i = 0; i < ps.length; i++) {
                int ix = ps[i].indexOf("=");
                if (ix != -1) {
                    pp.put(ps[i].substring(0, ix), ps[i].substring(ix + 1));
                }
            }
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        return pp;
    }
    
    /**
     * 发送http GET请求，并返回http响应字符串
     * 
     * @param urlstr 完整的请求url字符串
     * @return
     */
    public static String doGetRequest(String urlstr) {
    	//System.out.print(urlstr);
        String res = null;
        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        client.getParams().setIntParameter("http.socket.timeout", 10000);
        client.getParams().setIntParameter("http.connection.timeout", 5000);

        HttpMethod httpmethod = new GetMethod(urlstr);
        try {
            int statusCode = client.executeMethod(httpmethod);
            if (statusCode == HttpStatus.SC_OK) {
                res = httpmethod.getResponseBodyAsString();
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpmethod.releaseConnection();
        }
        return res;
    }

    /**
     * 发送http POST请求，并返回http响应字符串
     * 
     * @param urlstr 完整的请求url字符串
     * @return
     */
    public static String doPostRequest(String urlstr,Map<String,String> params) {
        String res = null;
        HttpClient client = new HttpClient(new MultiThreadedHttpConnectionManager());
        client.getParams().setIntParameter("http.socket.timeout", 10000);
        client.getParams().setIntParameter("http.connection.timeout", 5000);
        
      
        HttpMethod httpmethod =  new PostMethod(urlstr);
       
        //设置Http Post数据 
        if (params != null) { 
                HttpMethodParams p = new HttpMethodParams(); 
                for (Map.Entry<String, String> entry : params.entrySet()) { 
                        p.setParameter(entry.getKey(), entry.getValue()); 
                } 
                httpmethod.setParams(p); 
        } 
        
        try {
            int statusCode = client.executeMethod(httpmethod);
            if (statusCode == HttpStatus.SC_OK) {
                res = httpmethod.getResponseBodyAsString();
            }
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            httpmethod.releaseConnection();
        }
        return res;
    }
    
    //批量发送短信
    public static String doPost(String urlstr,Map<String,String> params) {
        
        String res="";
       
        	 HttpPostModel model=submitByPost(urlstr, params);
            int statusCode = model.get_ResonseCode();
            if (statusCode == HttpStatus.SC_OK) {
                res =model.get_ResponseString();
            }
       
        return res;
    }
    
   
    
	public  static void main(String args[]){
		SmsSender sms= SmsSender.newInstance("http://116.213.72.20/SMSHttpService/send.aspx","guodsy","guoding","34");
		String url_result="";
		try {
			url_result=sms.send_url("【国鼎石油】尊敬的客户您,5.7发货.四川夹江叶慧秀.5.6.良朋156XXXXXXXX.1-20.售后单","18616989375");
			
			//url_result=sms.send_url("http://www.dxqf.name/smsComputer/smsComputersend.asp", "");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.print(url_result);
	}
}
