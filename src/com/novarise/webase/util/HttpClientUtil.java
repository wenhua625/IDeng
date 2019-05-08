package com.novarise.webase.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
 
public class HttpClientUtil {
	public static final String urlString = "http://....?passport=...&password=...";  //先登录保存cookie
	public static final String urlString2 = "http://......";
	public String sessionId = "";
	
	public void doGet(String urlStr) throws IOException{
		String key = "";
		String cookieVal = "";
		URL url = new URL(urlStr);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.connect(); //到此步只是建立与服务器的tcp连接，并未发送http请求
		/** 
		 * 设置cookie
		 */
		if(!sessionId.equals("")){
			connection.setRequestProperty("Cookie", sessionId);
		}
		
		for(int i=1;(key=connection.getHeaderField(i))!=null;i++){
			cookieVal = connection.getHeaderField(i);
			cookieVal = cookieVal.substring(0,cookieVal.indexOf(";")>-1?cookieVal.indexOf(";"):cookieVal.length()-1);
			sessionId = sessionId + cookieVal + ";";
		}
		
		//直到getInputStream()方法调用请求才真正发送出去
		BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		StringBuilder sb = new StringBuilder();
		String line;
		while((line=br.readLine()) != null){
			sb.append(line);
			sb.append("\n");
		}
		System.out.println(sb.toString());
		br.close();
		connection.disconnect();
	}
	
	 /**
     * 发送POST请求
     * 
     * @param url
     *            目的地址
     * @param parameters
     *            请求参数，Map类型。
     * @return 远程响应结果
     */
    public  String doPost(String url, Map<String, String> parameters) {
    	String key = "";
		String cookieVal = "";
        String result = "";// 返回的结果
        BufferedReader in = null;// 读取响应输入流
        PrintWriter out = null;
        StringBuffer sb = new StringBuffer();// 处理请求参数
        String params = "";// 编码之后的参数
        try {
            // 编码请求参数
            if (parameters.size() == 1) {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8"));
                }
                params = sb.toString();
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8")).append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL.openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            //System.out.println("sessionId="+sessionId);
            if(!sessionId.equals("")){
            	httpConn.setRequestProperty("Cookie", sessionId);
    		}


           // httpConn.addRequestProperty("Set-Cookie", sessionId);
            
           
            
            // 设置POST方式
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            
            
    		
            
            // 获取HttpURLConnection对象对应的输出流
            out = new PrintWriter(httpConn.getOutputStream());
            // 发送请求参数
            out.write(params);
          
            
            // flush输出流的缓冲
            out.flush();
           
           
           
            // 定义BufferedReader输入流来读取URL的响应，设置编码方式
            in = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    
    /**
     * 发送POST请求
     * 
     * @param url
     *            目的地址
     * @param parameters
     *            请求参数，Map类型。
     * @return 远程响应结果
     */
    public  String doUploadFilePost(String url, Map<String, String> parameters,String localFile) {
    	String video_id="";
    	 CloseableHttpClient httpClient = null;
         CloseableHttpResponse response = null;
         try {
             httpClient = HttpClients.createDefault();
             // 把一个普通参数和文件上传给下面这个地址 是一个servlet
             HttpPost httpPost = new HttpPost(url);
        
             // 把文件转换成流对象FileBody
             FileBody bin = new FileBody(new File(localFile));
             HttpEntity reqEntity = MultipartEntityBuilder.create()
                     // 相当于<input type="file" name="file"/>
                     .addPart("file", bin)
                     // 相当于<input type="text" name="userName" value=userName>
                     .build();
           //  System.out.println(reqEntity.getContentLength());
             httpPost.setEntity(reqEntity);
             httpPost.setHeader("Cookie", sessionId);
             // 发起请求 并返回请求的响应
             response = httpClient.execute(httpPost);
             //System.out.println("The response value of token:" + response.getFirstHeader("token"));
             // 获取响应对象
             HttpEntity resEntity = response.getEntity();
             if (resEntity != null) {
            	 video_id=EntityUtils.toString(resEntity, Charset.forName("UTF-8"));
           	  JSONArray jsonArray = JSONArray.parseArray(video_id);
           	  if(jsonArray.size()>0){
           		  video_id=(String)jsonArray.get(0);
           	  }
             }
             // 销毁
             EntityUtils.consume(resEntity);
         }catch (Exception e){
             e.printStackTrace();
         }finally {
             try {
                 if(response != null){
                     response.close();
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
             try {
                 if(httpClient != null){
                     httpClient.close();
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
         return video_id;
       
    }
    
    /**
     * 发送POST请求
     * 
     * @param url
     *            目的地址
     * @param parameters
     *            请求参数，Map类型。
     * @return 点播ID
     */
    public  JSONObject doUploadFilePost(String url, Map<String, String> parameters,File localFile) {
    	String video_id="";
    	JSONObject jsonArray=null;
    	CloseableHttpClient httpClient = null;
         CloseableHttpResponse response = null;
         try {
             httpClient = HttpClients.createDefault();
             // 把一个普通参数和文件上传给下面这个地址 是一个servlet
             HttpPost httpPost = new HttpPost(url);
        
             // 把文件转换成流对象FileBody
             
             FileBody bin = new FileBody(localFile);
             HttpEntity reqEntity = MultipartEntityBuilder.create()
                     // 相当于<input type="file" name="file"/>
                     .addPart("file", bin)
                     // 相当于<input type="text" name="userName" value=userName>
                     .build();
           //  System.out.println(reqEntity.getContentLength());
             httpPost.setEntity(reqEntity);
             httpPost.setHeader("Cookie", sessionId);
            // httpPost.setHeader("Content-Type", "video/mp4");
             
             // 发起请求 并返回请求的响应
             response = httpClient.execute(httpPost);
            // System.out.println("The response value of token:" + response.getFirstHeader("token"));
             // 获取响应对象
             HttpEntity resEntity = response.getEntity();
             if (resEntity != null) {
            	 //System.out.println("df:"+EntityUtils.toString(resEntity, Charset.forName("UTF-8")));
            	  video_id=EntityUtils.toString(resEntity, Charset.forName("UTF-8"));
            	  System.out.println("video_id:"+video_id);
            	   jsonArray = JSONObject.parseObject(video_id);;//JSONArray.parseArray(video_id);
            	  //video_id=(String)jsonArray.get("id");
            	 /* if(jsonArray.size()>0){
            		  video_id=(String)jsonArray.get(0);
            	  }*/
            	  //video_id=jsonArray.toJSONString();
                 // 打印响应长度
                // System.out.println("Response content length: " + resEntity.getContentLength());
                 // 打印响应内容
                 //System.out.println(EntityUtils.toString(resEntity, Charset.forName("UTF-8")));
             }
             // 销毁
             EntityUtils.consume(resEntity);
         }catch (Exception e){
             e.printStackTrace();
         }finally {
             try {
                 if(response != null){
                     response.close();
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
             try {
                 if(httpClient != null){
                     httpClient.close();
                 }
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }
         return jsonArray;
       
    }
    
    
    
    /**
     * 发送POST请求
     * 
     * @param url
     *            目的地址
     * @param parameters
     *            请求参数，Map类型。
     * @return 远程响应结果
     */
    public  String doLoginPost(String url, Map<String, String> parameters) {
    	String key = "";
		String cookieVal = "";
        String result = "";// 返回的结果
        BufferedReader in = null;// 读取响应输入流
        PrintWriter out = null;
        StringBuffer sb = new StringBuffer();// 处理请求参数
        String params = "";// 编码之后的参数
        try {
            // 编码请求参数
            if (parameters.size() == 1) {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8"));
                }
                params = sb.toString();
            } else {
                for (String name : parameters.keySet()) {
                    sb.append(name).append("=").append(java.net.URLEncoder.encode(parameters.get(name), "UTF-8")).append("&");
                }
                String temp_params = sb.toString();
                params = temp_params.substring(0, temp_params.length() - 1);
            }
            // 创建URL对象
            java.net.URL connURL = new java.net.URL(url);
            // 打开URL连接
            java.net.HttpURLConnection httpConn = (java.net.HttpURLConnection) connURL.openConnection();
            // 设置通用属性
            httpConn.setRequestProperty("Accept", "*/*");
            httpConn.setRequestProperty("Connection", "Keep-Alive");
            httpConn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1)");
            httpConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            
            


           // httpConn.addRequestProperty("Set-Cookie", sessionId);
            
           
            
            // 设置POST方式
            httpConn.setDoInput(true);
            httpConn.setDoOutput(true);
            
            
    		
            
            // 获取HttpURLConnection对象对应的输出流
            out = new PrintWriter(httpConn.getOutputStream());
            // 发送请求参数
            out.write(params);
          
            
            // flush输出流的缓冲
            out.flush();
           
            for(int i=1;(key=httpConn.getHeaderField(i))!=null;i++){
    			cookieVal = httpConn.getHeaderField(i);
    			cookieVal = cookieVal.substring(0,cookieVal.indexOf(";")>-1?cookieVal.indexOf(";"):cookieVal.length()-1);
    			
    			sessionId = sessionId + cookieVal + ";";
    		}
           
            // 定义BufferedReader输入流来读取URL的响应，设置编码方式
            in = new BufferedReader(new InputStreamReader(
                    httpConn.getInputStream(), "UTF-8"));
            String line;
            // 读取返回的内容
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }
    
    /**
     * 将请求的头信息获取到session中
     * @param request
     * @param connection
     */
    private static void getCookieToSession(URLConnection connection) {

        Map<String, List<String>> map = connection.getHeaderFields();
        for (String key : map.keySet()) {
            System.out.println(key + "--->" + map.get(key));
        }
      

        List<String> cookie=map.get("Set-Cookie");
        if(cookie!=null){

            String verCode=getCookieBySet("verCode",cookie.get(0));

            String JSESSIONID=getCookieBySet("sid",cookie.get(0));

           // if(verCode!=null){
               // session.setAttribute("verCode",verCode);
          //  }

          //  if(JSESSIONID!=null){
                //session.setAttribute("sid",JSESSIONID);
          //  }
        }

    }
    
    /**
     * 获取verCode=b5pcogZaFGikpAc1mQ+G5wOJGBWtXLsHafpf5wlgF5s=; Path=/SSODAO/; HttpOnly
     * 的cookie信息
     */
    public static String getCookieBySet(String name,String set){

        String regex=name+"=(.*?);";

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher =pattern.matcher(set);
        if(matcher.find()){
            return matcher.group();
        }
        return null;

    }


	
	public static void main(String[] args) throws IOException {
		HttpClientUtil hcu = new HttpClientUtil();
		
		//hcu.doLoginPost(url, parameters)
		//hcu.doGet(urlString);
		//hcu.doGet(urlString2);
	}
}
