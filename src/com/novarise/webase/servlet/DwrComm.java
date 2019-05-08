package com.novarise.webase.servlet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONObject;

import org.directwebremoting.ScriptSession;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.directwebremoting.proxy.dwr.Util;

import com.novarise.webase.framework.DwrControl;
import com.novarise.webase.util.GenerateSequenceUtil;

public class DwrComm {

	DwrControl control = new DwrControl();
	
	//推送消息
	public void sengMsg(String msg)
	{
		WebContext wctx = WebContextFactory.get(); // 这里是获取WebContext上下文
		ScriptSession scriptSession = wctx.getScriptSession(); 
		  
        String clientPage = "http://127.0.0.1/kind/display?proname=mainframe.htm";//BConstants.LOGIN_MAIN;// 推到哪个页面
         // 要求的必须方式
        Collection sessions = wctx.getScriptSessionsByPage(clientPage); // 在一个page中可能存在多个ScriptSessions,
        //Util utilAll = new Util(sessions); // Util 是DWR 在Server端模拟Brower端
        Util utilAll = new Util(scriptSession); 
        String  ss = msg;
        utilAll.setValue("AA", ss);       
        //utilAll.setStyle("bb", "display", "inline");
        //utilAll.setValue("bb", "<p>"+u.getContext()+"</p>", false);
        //ScriptBuffer sb = new ScriptBuffer();
        utilAll.addFunctionCall("divShow", msg); 
        System.out.println("fdsfsdfsdddd");
	}

	// 系统提示
	public String parseTip(String param) {
		String tipStr = "";
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest request = wctx.getHttpServletRequest();
		try {
			tipStr = control.parseDisplayTip(request);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tipStr;
	}
	
//	 处理下拉框,列表等
	public List parseXL(String param,Map formMap) {
		List result = new ArrayList();
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest request = wctx.getHttpServletRequest();
		try {
			result = control.parseDisplayXL(param, request,formMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}


	// 处理下拉框,列表等
	/*public List parseAL(String param) {
		List result = new ArrayList();
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest request = wctx.getHttpServletRequest();
		try {
			result = control.parseDisplayAL(param, request);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}*/
	
//	 处理下拉框,列表等
	public List parseAL(String param,Map formMap) {
		List result = new ArrayList();
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest request = wctx.getHttpServletRequest();
		try {
			result = control.parseDisplayAL(param, request,formMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}

	// 生成主键
	public String parseKey(String param, Map formMap) {
		String key = "";
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest request = wctx.getHttpServletRequest();
		try {

			key = control.parseDisplayKEY(param, request, formMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return key;
	}

	// 处理数据插入操作
	public String parseIN(String param, Map formMap, String action) {
		String result = "";
		//System.out.println("Starinnggg...");
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest request = wctx.getHttpServletRequest();
		try {

			result = control.parseModifyIN(param, request, action, formMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	// 处理列表MJ
	public List parseMJ(String kjname, Map formMap) {
		List result = new ArrayList();
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest request = wctx.getHttpServletRequest();

		try {

			result = control.parseDisplayMJ(kjname, request, formMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return result;
	}
	
	// 处理列表JJ 处理JSON数据
		public JSONObject parseJJ(String kjname, Map formMap) {
			JSONObject result = new JSONObject();
			WebContext wctx = WebContextFactory.get();
			HttpServletRequest request = wctx.getHttpServletRequest();

			try {

				result = control.parseDisplayJJ(kjname, request, formMap);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return result;
		}

	// 生成基础主键
	public String parseBaseKey(String codeType) {
		String key = "";

		try {
			key = control.parseDisplayBaseKEY(codeType);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return key;
	}
	// 生成基础主键
		public String parseRandKey() {
			String key = "";

			try {
				
				key = GenerateSequenceUtil.generateSequenceNo();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return key;
		}
		
		// 生成基础主键
				public String parseRandHZKey() {
					String key = "";

					try {
						
						key = GenerateSequenceUtil.generateHZSequenceNo();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return key;
				}
	
//	 发送短信
	public String parseSendSMS(String param, Map formMap,String dxmb) {
		String key = "";
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest request = wctx.getHttpServletRequest();
		try {

			key = control.parseDisplaySMS(param, request, dxmb, formMap);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return key;
	}
	
//	 发送短信
	public String parseSendSMS(String hm,String dxmb) {
		String key = "";
		WebContext wctx = WebContextFactory.get();
		HttpServletRequest request = wctx.getHttpServletRequest();
		try {

			key = control.parseDisplaySMS(request, dxmb, hm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println(e.toString());
			e.printStackTrace();
		}
		return key;
	}


	public List getList(String sts) {
		List l = new ArrayList();
		
		Map m = new HashMap();
		m.put("id", "101");
		m.put("text", "上海专卖店");
		m.put("leaf", true);
		m.put("parentnode", "0");
		l.add(m);
		m = new HashMap();
		m.put("id", "102");
		m.put("text", "绍兴片区");
		m.put("leaf", false);
		m.put("parentnode", "0");
		l.add(m);
		m = new HashMap();
		m.put("id", "1021");
		m.put("text", "绍兴一店");
		m.put("leaf", true);
		m.put("parentnode", "102");
		l.add(m);
	
		// l.add("dfdsf");
		// l.add("['AL', 'Alabama', 'The Heart of Dixie']");
		// l.add("['IA', 'Iowa', 'The Corn State']");
		// l.add("['NY', 'New York', 'Empire State']");
		return l;
	}
}
