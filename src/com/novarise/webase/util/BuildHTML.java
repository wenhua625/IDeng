package com.novarise.webase.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import com.baidu.ueditor.PathFormat;
import com.baidu.ueditor.define.State;
import com.baidu.ueditor.upload.StorageManager;

public class BuildHTML {

	public static String  save(HttpServletRequest request,
			Map<String, Object> conf) {
		
		String re = "";
		String savePath = (String) conf.get("savePath");
		String suffix = ".html";
		long maxSize = ((Long) conf.get("maxSize")).longValue();
		String content = request.getParameter("content")==null?"":request.getParameter("content");
		try {
			content = formatHTML(content);
			savePath = savePath + suffix;
			savePath = PathFormat.parse(savePath, "");	// 根据日期生成目录和文件名
			String physicalPath = (String) conf.get("rootPath") + savePath;	// 生成保存路径全称
			InputStream is = new   ByteArrayInputStream(content.getBytes("UTF-8")); 
			
			State storageState = StorageManager.saveFileByInputStream(is,
					physicalPath, maxSize);

			is.close();

			if (storageState.isSuccess()) {
				re =  formatSiteUrl(request,PathFormat.format(savePath));
			}
		} catch (IOException e) {
			System.out.println(e);
		}

		return re;
	}
	
	/**
	 * 将保存的路径加上网址
	 * @param request
	 * @param savePath
	 * @return
	 */
	private static String  formatSiteUrl(HttpServletRequest request,String savePath){
//		String path1 = request.getContextPath();
		String path = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()+request.getContextPath()+"/";
		path = path +savePath;
//		StringBuffer path3 = request.getRequestURL();
		return path;
	}
	
	/**
	 * 增加HTML标准标签格式
	 * @param content
	 * @return
	 */
	private static String formatHTML(String content){
		/*<!doctype html>
		<html>
		<head>
		<meta charset="utf-8">
		<title></title>
		</head>
		<body>
		</body>
		</html>*/
		StringBuffer sf = new StringBuffer();
		sf.append("<!doctype html>");
		sf.append("<html>");
		sf.append("<head>");
		sf.append("<meta charset=\"utf-8\">");
		sf.append("<title></title>");
		sf.append("<meta name=\"viewport\"content=\"width=device-width, initial-scale=1\"/>");
		sf.append(getBridgeReady());//增加禁止转载控制
		sf.append("</head>");
		sf.append("<body>");
		sf.append(formatScript(content));
		sf.append("</body>");
		sf.append("</html>");
		
		return sf.toString();
	}
	
	private static String getBridgeReady(){
		StringBuffer sf = new StringBuffer();
		sf.append("<script>function onBridgeReady(){ ");
		sf.append("     WeixinJSBridge.call('hideOptionMenu');}");
		sf.append("     if (typeof WeixinJSBridge == \"undefined\")");
		sf.append("     {   if( document.addEventListener )");
		sf.append("	 {  document.addEventListener('WeixinJSBridgeReady', onBridgeReady, false);");
		sf.append("	 }else if (document.attachEvent)");
		sf.append("	 {  document.attachEvent('WeixinJSBridgeReady', onBridgeReady);");
		sf.append("	    document.attachEvent('onWeixinJSBridgeReady', onBridgeReady);");
		sf.append("	 }");
		sf.append("     }else{ onBridgeReady();}");
		sf.append("</script>");

		return sf.toString();
	}
	
	/**
	 * 脚本注入还原
	 * @param value
	 * @return
	 */
		
	private static String formatScript(String value){
		if(value != null && !"".equals(value)){
			value = value.replaceAll("&lt;","<");
	        value = value.replaceAll("&gt;",">");
	        value = value.replaceAll("&#40;","\\(");
	        value = value.replaceAll("&#41;","\\)");
	        value = value.replaceAll("&#39;","'");
	        value = value.replaceAll("&quot;","\"");
		}
        return value;
	}
}
