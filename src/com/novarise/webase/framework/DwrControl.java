package com.novarise.webase.framework;

import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.novarise.webase.BConstants;
import com.novarise.webase.util.DateHelper;
import com.novarise.webase.util.JsonUtil;
import com.novarise.webase.util.SmsSender;
import com.novarise.webase.xml.XmlUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;
import com.ripple.datasource.SQLUpdater;

public class DwrControl {

	/**
	 * 实现HTML中数据显示的方法 实现方法: 1,分离HTML页面 2,连接数据库,取出业务SQL 3,根据业务SQL取出符合条件的记录
	 * 4,解析HTML页面 5,返回
	 */
	public List parseDisplayMJ(String s_kjname, HttpServletRequest request,
			Map formMap) throws Exception {
		// 取出业务SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("处理" + s_kjname + "出错!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("控件名" + s_kjname + "没找到!");
		}
		// 获取业务数据内容
		String ljh = y_sql[0][1].trim(); // 数据连接号
		String cs = y_sql[0][2].trim(); // 用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); // 业务用的SQL
		String sql2 = y_sql[0][4].trim(); // 业务用的SQL2,用于连接上面的SQL
		String xsfs = y_sql[0][5].trim(); // 如果页码需显示时，1:全显示 2:只显示下面 3:只显示上面
		String sfxsym = y_sql[0][6].trim(); // 是否显示页码,1:显示 0:不显示
		String myhs = y_sql[0][7].trim(); // 每页行数
		String defaults = y_sql[0][8].trim(); // 数值为NULL时缺省值
		String msjts = y_sql[0][10].trim(); // 没有找到数据时提示

		// SQL语名句中的查询条件
		String sql_tj = gettjsql(request, s_kjname, formMap);
		// SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "", formMap);

		// 开始获取实际数据
		SQLQuery query = DSManager.getSQLQuery();
		List result = new ArrayList();

		try {
			result = query.ListQuery(sql);
		} catch (Exception e) {
			//e.printStackTrace();
            System.out.println("DWRCOMM.pareMJ出错"+s_kjname+"!"+sql);
			return result;
		}
		return result;
	}
	
	public JSONObject parseDisplayJJ(String s_kjname, HttpServletRequest request,
			Map formMap) throws Exception {
		// 取出业务SQL
		JSONObject json_result = new JSONObject();
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("处理" + s_kjname + "出错!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("控件名" + s_kjname + "没找到!");
		}
		// 获取业务数据内容
		String ljh = y_sql[0][1].trim(); // 数据连接号
		String cs = y_sql[0][2].trim(); // 用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); // 业务用的SQL
		String sql2 = y_sql[0][4].trim(); // 业务用的SQL2,用于连接上面的SQL
		String xsfs = y_sql[0][5].trim(); // 如果页码需显示时，1:全显示 2:只显示下面 3:只显示上面
		String sfxsym = y_sql[0][6].trim(); // 是否显示页码,1:显示 0:不显示
		String myhs = y_sql[0][7].trim(); // 每页行数
		String defaults = y_sql[0][8].trim(); // 数值为NULL时缺省值
		String msjts = y_sql[0][10].trim(); // 没有找到数据时提示

		// SQL语名句中的查询条件
		String sql_tj = gettjsql(request, s_kjname, formMap);
		// SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "", formMap);

		// 开始获取实际数据
		SQLQuery query = DSManager.getSQLQuery();
		List result = new ArrayList();

		try {
			result = query.ListQuery(sql);
		} catch (Exception e) {
			//e.printStackTrace();
            System.out.println("DWRCOMM.pareJJ出错"+s_kjname+"!"+sql);
			//return result;
		}
		
		JSONArray json_arr = JSONArray.fromObject(result);
		json_result.put("rows", json_arr);
		//return json_result.toString();
		return json_result;
	}

	// 取条件sql语句
	private static String gettjsql(HttpServletRequest request, String kjname)
			throws Exception {
		HttpSession session = request.getSession(true);
		String[][] mjcstj = new String[0][0];
		try {
			mjcstj = XmlUtil.find(BConstants.PAGE_MJ_TJ, "KJM", kjname);
		} catch (SQLException se1) {
			throw new Exception("取条件SQL时出错！" + se1.toString());
		}
		String s_tjsql = " ";
		if (mjcstj.length != 0) {
			for (int i = 0; i < mjcstj.length; i++) {
				if ((request.getParameter(mjcstj[i][1].trim()) != null)
						&& (request.getParameter(mjcstj[i][1].trim()).trim()
								.length() != 0)) {
					if (mjcstj[i][1].indexOf("order") != -1) {
						s_tjsql += "  " + mjcstj[i][2] + " ";
					} else {
						s_tjsql += " and " + mjcstj[i][2] + " ";
					}

				} else {
					if ((String) session.getAttribute(mjcstj[i][1].trim()) != null)
						s_tjsql += " and " + mjcstj[i][2] + " ";
				}
			}
		}
		return s_tjsql;
	}

	// 取条件sql语句
	private static String gettjsql(HttpServletRequest request, String kjname,
			Map formMap) throws Exception {
		HttpSession session = request.getSession(true);
		String[][] mjcstj = new String[0][0];
		try {
			mjcstj = XmlUtil.find(BConstants.PAGE_MJ_TJ, "KJM", kjname);
		} catch (SQLException se1) {
			throw new Exception("取条件SQL时出错！" + se1.toString());
		}
		String s_tjsql = " ";
		String value;
		if (mjcstj.length != 0) {
			for (int i = 0; i < mjcstj.length; i++) {
				if (formMap.get(mjcstj[i][1].trim()) == null)
					value = "";
				else
					value = (String) formMap.get(mjcstj[i][1].trim());
				if (value.length() != 0) {
					if (mjcstj[i][1].indexOf("order") != -1) {
						s_tjsql += "  " + mjcstj[i][2] + " ";
					} else {
						s_tjsql += " and " + mjcstj[i][2] + " ";
					}

				} else {
					if ((String) session.getAttribute(mjcstj[i][1].trim()) != null)
						s_tjsql += " and " + mjcstj[i][2] + " ";
				}
			}
		}
		return s_tjsql;
	}
	
	/**
	 * 用AJAX方式处理下拉框的显示 实现方法: 1,连接数据库 取出SQL 2,根据SQL取出符合条件的值 3,解析HTML的值 4,返回
	 */

	public List parseDisplayXL(String s_kjname, HttpServletRequest request,Map formMap)
			throws Exception {

		List result = new ArrayList();

		// 连接数据库
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_XL, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0) {
				return result;
			}

		} catch (Exception e) {
			return result;
		}
//		 SQL语名句中的查询条件
		String sql_tj = gettjsql(request, s_kjname, formMap);
		// SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		String sql = y_sql[0][3]+ sql_tj ;
		sql = HtmlFunction.parseVar(sql, request, "", formMap);
		

		// 取出符合条件的值

		try {
			result = query.ListQuery(sql);
		} catch (Exception e) {
			return result;
		}

		return result;
	}


	/**
	 * 用AJAX方式处理下拉框的显示 实现方法: 1,连接数据库 取出SQL 2,根据SQL取出符合条件的值 3,解析HTML的值 4,返回
	 */

	public List parseDisplayAL(String s_kjname, HttpServletRequest request,Map formMap)
			throws Exception {

		List result = new ArrayList();

		// 连接数据库
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_XL, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0) {
				return result;
			}

		} catch (Exception e) {
			return result;
		}

		String sql = HtmlFunction.parseVar(y_sql[0][3], request, "",formMap);

		// 取出符合条件的值

		try {
			result = query.ListQuery(sql);
		} catch (Exception e) {
			return result;
		}

		return result;
	}

	

	public static synchronized String parseModifyIN(String kjname,
			HttpServletRequest request, String s_action, Map formMap)
			throws Exception {

		String tmpKj = kjname;
		kjname = kjname.substring(0, 6);// 处理控件名，控件名为6位

		SQLUpdater updater = DSManager.getSQLUpdater();
		SQLQuery query = DSManager.getSQLQuery();

		if (s_action == null || s_action.trim().length() == 0)
			s_action = "insert";
		// 取业务数据
		String[][] s_modifycs = new String[0][0];
		try {
			s_modifycs = XmlUtil.find(BConstants.PAGE_UP, "KJM", kjname);
			if (s_modifycs.length == 0)
				return "Ajax:操作对象[" + kjname + "]不存在，操作未成功！";
		} catch (SQLException e) {
			return "Ajax:操作对象[" + kjname + "]定位SQL出错!";
		}
		String s_selectsql = s_modifycs[0][2], s_insertsql = s_modifycs[0][3], s_deletesql = s_modifycs[0][5], s_updatesql = s_modifycs[0][4], s_cs = s_modifycs[0][1], s_url = s_modifycs[0][6];

		if (s_action.equals("delete")) {
			s_deletesql = HtmlFunction.parseVar(s_deletesql, request, "",
					formMap);

			try {
				updater.executeUpdate(s_deletesql);
			} catch (SQLException e) {
				System.out.println(s_deletesql);
				return "Ajax:操作对象[" + kjname + "]删除SQL出错!SQL=" + s_deletesql;
			}

		}
		if (s_action.equals("insert")) {

			
			// 判断是否需要检测,'1'为是
			if (s_cs.equals("1")) {
				s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "",
						formMap);
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "",
						formMap);
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					return "Ajax:操作对象[" + kjname + "]检测时出错!SQL=" + s_selectsql;
				}
				if (checkValue.length != 0)
					return "记录[" + checkValue[0][0] + "]已存在，操作未成功!";
				else
				{
					try {
						updater.executeUpdate(s_insertsql);
						if(s_url.length()>0)
						{
							//调用外部接口
							//System.out.println("Url:"+s_url);
							s_url = HtmlFunction.parseEncodeVar(s_url, request, "", formMap);
							//System.out.println("Url1:"+s_url);
							SmsSender.doGetRequest(s_url);
						}
					} catch (SQLException e) {
						System.out.println(s_insertsql);
						return "Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="
								+ s_insertsql;
					}
				}
			}

			else if (s_cs.equals("3")) {
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "",
						formMap);
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					return "Ajax:操作对象[" + kjname + "]检测时出错!SQL=" + s_selectsql;
				}
				if (checkValue.length == 0) {

					return "该订单中无此条形码的产品!";
				}

			}// 如果检测到标志为2,则有记录就更新记录，不再提示，无记录，直接插入
			else if (s_cs.equals("2")) {
				s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "",
						formMap);
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "",
						formMap);
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					return "Ajax:操作对象[" + kjname + "]检测时出错!SQL=" + s_selectsql;
				}
				if (checkValue.length != 0) {
					s_updatesql = HtmlFunction.parseVar(s_updatesql, request,
							"", formMap);
					updater.executeUpdate(s_updatesql);
				} else {
					updater.executeUpdate(s_insertsql);
				}

			} 
			//DSSession dsn=DSManager.getDSSession();
			else if(s_cs.equals("4")){//处理Json数据
				
				String jsonData = (String)formMap.get("d_JsonData");
				String agentcode = (String)formMap.get("Agent_Code");
				//System.out.println("dfdfdfd="+agentcode);
				if(jsonData == null) jsonData = "";
				if(!jsonData.equals(""))
				{
					try{
						//dsn.beginTransaction();
						String jsonType = (String)formMap.get("d_JsonType");
						if(jsonType == null) jsonType = "";
						if(jsonType.equalsIgnoreCase("Easyui")){
						   jsonData=java.net.URLDecoder.decode(jsonData,"utf-8");
						}
						
						s_deletesql = HtmlFunction.parseVar(s_deletesql, request, "",formMap);
						updater.executeUpdate(s_deletesql);//删除明细数据
						List jsonList = JsonUtil.getList4Json(jsonData, Map.class);
						//循环插入明细数据
						for(int i=0;i<jsonList.size();i++){
							Map map=(Map)jsonList.get(i);
							map.put("xh", i);//序号存入
							String updatesql=HtmlFunction.parseVar(s_updatesql, request,"", map,formMap);
							//System.out.println(updatesql);
							try{
							updater.executeUpdate(updatesql);
							}catch(Exception ex){
								System.out.println(updatesql);
								return "Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="
								+ updatesql;
							}
							
						}
						
						if(!s_insertsql.equals("")){
							s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "",
									formMap);
							updater.executeUpdate(s_insertsql);//插入订单数据
						}
						
						//dsn.endTransaction();
					}catch(Exception e){
						//dsn.rollback();
						System.out.println(s_insertsql+"ERROR:"+e.toString());
						e.printStackTrace();
						return "Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="
						+ s_insertsql;
						
						
					}
				}
			}
			
			else {
				try {
					s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "",
							formMap);
					updater.executeUpdate(s_insertsql);
				} catch (SQLException e) {
					System.out.println(s_insertsql);
					return "Ajax:操作对象[" + kjname + "]插入SQL出错!SQL="
							+ s_insertsql;
				}
			}
			

		}
		if (s_action.equals("update")) {
			s_updatesql = HtmlFunction.parseVar(s_updatesql, request, "",
					formMap);

			try {
				updater.executeUpdate(s_updatesql);
			} catch (SQLException e) {
				System.out.println(s_updatesql);
				return "Ajax:操作对象[" + kjname + "]更新SQL出错!SQL=" + s_updatesql;
			}

		}

		if (s_action.equals("call")) {
			s_url = HtmlFunction.parseVar(s_url, request, "", formMap);
            if(s_cs.equals("9"))
            {//开启线程处理
            	final String sql_thread = s_url;
            	Thread t=new Thread(){
				    public void run(){
				    	
				    	try {
				    		SQLUpdater updater_thread = DSManager.getSQLUpdater();
							updater_thread.executeCall(sql_thread);
						} catch (SQLException e) {
							
							e.printStackTrace();
						}
				    	
				   }
				};
				t.start();
            }else{
			try {

				updater.executeCall(s_url);
			} catch (Exception e) {
				System.out.println(s_url);
				return "Ajax:操作对象[" + kjname + "]执行存程过程出错!URL=" + s_url
						+ e.toString();
			}
            }

		}

		return "ok";
	}

	// 订单提示
	public String parseDisplayTip(HttpServletRequest request) throws Exception {

		String returnString = "";
		HttpSession session = request.getSession(true);
		
        //提醒内容权限
		List l_gndm=new ArrayList();
		String gndm = (String)session.getAttribute("LS.QXTS");
		if (gndm == null) gndm="";
		if(gndm.length()>0){
		   String gndms[]=gndm.split(",");
		   for(int i=0;i<gndms.length;i++)
		   {
			l_gndm.add(gndms[i]);
		   }
		}
//		取出业务SQL
		String y_sql[][] = new String[0][0];
		for(int i=0;i<l_gndm.size();i++){
			String s_gndm=(String)l_gndm.get(i);
			String s_kjname=s_gndm.substring(1);
			s_kjname=s_kjname.substring(0,s_kjname.length()-1);
			try {
				y_sql = XmlUtil.find(BConstants.PAGE_TP, "KJM", s_kjname);
			} catch (Exception e) {
				throw new Exception("处理" + s_kjname + "出错!" + e.toString());
			}
			
			if (y_sql == null || y_sql.length == 0) {
				throw new Exception("控件名" + s_kjname + "没找到!");
			}
			// 获取业务数据内容
			String ljh = y_sql[0][1].trim(); // 数据连接号
			String cs = y_sql[0][3].trim(); // 用于加入order by ,group by 语句
			String sql = y_sql[0][9].trim(); // 业务用的SQL
			
			sql = sql   + cs;
			sql = HtmlFunction.parseVar(sql, request, "");
			
			SQLQuery query = DSManager.getSQLQuery();
			
			String resultData[][] =null;
           try{
        	   resultData = query.ArrayQuery(sql);
			}catch(Exception ex)
			{
				System.out.println("提醒报错："+sql);
				throw new Exception(ex);
			}
			if (resultData.length == 0) {
				returnString+="";
			}else{
				for(int k=0;k<resultData.length;k++){
				    returnString += "<tr><td>"+resultData[k][0]+ "</td></tr>";
				}
			}
		}
		if(returnString.length()>0){
			returnString = "<table border=0 class=\"w01_13px_black\">" +returnString +"<tr><td height=20>&nbsp;</td></tr></table>";
		}
		return returnString;
	}

	public String parseDisplayKEY(String s_kjname, HttpServletRequest request,
			Map formMap) throws Exception {
		// 取出业务SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("处理" + s_kjname + "出错!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("控件名" + s_kjname + "没找到!");
		}
		// 获取业务数据内容

		String cs = y_sql[0][2].trim(); // 用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); // 业务用的SQL
		String sql2 = y_sql[0][4].trim(); // 业务用的SQL2,用于连接上面的SQL

		// SQL语名句中的查询条件
		String sql_tj = gettjsql(request, s_kjname);
		// SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "sql", formMap);

		// 开始获取实际数据
		SQLQuery query = DSManager.getSQLQuery();
		String result[][] = new String[0][0];
		try{
			result = query.ArrayQuery(sql);
		}catch(Exception ex)
		{
			throw new Exception(ex+"取唯一值时出错(" + s_kjname + ")!"+sql);
		}
		
		if (result.length == 0) {
			throw new Exception("没有取到的唯一键值(" + s_kjname + ")!");
		}
		return result[0][0];

	}

	public String parseDisplayBaseKEY(String codeType) throws Exception {
		// 取出业务SQL
		String sql = "select code,code_date,code_type from Code_Gen where Code_Type='"
				+ codeType
				+ "' and code_date='"
				+ DateHelper.getShortDate()
				+ "'";
		String insert_sql = " insert into Code_Gen(code_type,code,code_date) values('"
				+ codeType + "','2','" + DateHelper.getShortDate() + "')";
		String update_sql = "update Code_Gen set code=code+1 where Code_Type='"
				+ codeType + "' and code_date='" + DateHelper.getShortDate()
				+ "'";
		DecimalFormat format = new DecimalFormat("000");
		// 开始获取实际数据
		SQLQuery query = DSManager.getSQLQuery();
		SQLUpdater updater = DSManager.getSQLUpdater();
		String result[][] = new String[0][0];
		result = query.ArrayQuery(sql);
		if (result.length == 0) {
			updater.executeUpdate(insert_sql);
			return DateHelper.getShortDate() + format.format(1);
		} else {
			updater.executeUpdate(update_sql);
			return DateHelper.getShortDate()
					+ format.format(Long.parseLong(result[0][0]));
		}

	}
	
	/**
	 * 短信通知接口
	 * @param request
	 * @param formMap
	 * @return
	 * @throws Exception
	 */
	public String parseDisplaySMS(String s_kjname,HttpServletRequest request,String dxmb,Map formMap) throws Exception {

		
		 
		
       // 取出业务SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("处理" + s_kjname + "出错!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("控件名" + s_kjname + "没找到!");
		}
		
       // 获取业务数据内容
		String cs = y_sql[0][2].trim(); // 用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); // 业务用的SQL
		String sql2 = y_sql[0][4].trim(); // 业务用的SQL2,用于连接上面的SQL
		// SQL语名句中的查询条件
		String sql_tj = gettjsql(request, s_kjname);
		// SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "sql", formMap);
		
       // 开始获取消费数据
		SQLQuery query = DSManager.getSQLQuery();
		SQLUpdater updater = DSManager.getSQLUpdater();
		List result = new ArrayList();
		result = query.ListQuery(sql);
		if (result.size() == 0) {
			throw new Exception("没有要发送短信的数据(" + s_kjname + ")!"+sql);
		}
		Map data=(Map)result.get(0);
		String smsNR="";
		smsNR=HtmlFunction.parseSMSTemplate(dxmb, data);
		//SmsSender sender=SmsSender.newInstance("http://202.91.244.252:8080/qd/SMSSendYD","5084","zc888","47");
		String hm="";
		if (data.get("手机号码") == null)
		{
			hm="";
		}else hm=data.get("手机号码").toString();
		if (hm.equals(""))
		{
			return "";
		}else{
		  String jz_sql = "insert into sms_js(ckbm,dwdm,sj,xm,dxnr,rq,lb) select lb,bm,sj,mc,'"+smsNR+"',getdate(),'系统记账' from bm_wldw where sj in ('"+hm+"')";	
           //System.out.println(jz_sql);
           updater.executeUpdate(jz_sql);
           hm="86"+hm;
		   String smsResult = SmsSender.send_url(smsNR, hm);
		   return smsResult;
		}
	}
	
	/**
	 * 短信通知接口
	 * @param request
	 * @param formMap
	 * @return
	 * @throws Exception
	 */
	public String parseDisplaySMS(HttpServletRequest request,String dxmb,String hm) throws Exception {

        String smsNR=dxmb;
		
           SQLQuery query = DSManager.getSQLQuery();
		   SQLUpdater updater = DSManager.getSQLUpdater();
		   
		   
		  /* String[] hms=hm.split("\n");
		   String jz_hm="";
           String qf_hm="";
           for(int i=0;i<hms.length;i++){
        	   String s_hm=hms[i].trim();
        	   if(!s_hm.equals(""))
        	      qf_hm+="86"+hms[i].trim()+",";
        	      jz_hm+="'"+hms[i].trim()+"',";
           }
           qf_hm=qf_hm.substring(0,qf_hm.length()-1);
           jz_hm=jz_hm.substring(0,jz_hm.length()-1);*/
           
           
		   String smsResult = SmsSender.send_url(smsNR, hm);
		   if(smsResult.equals("0"))
		   {
			   String jz_sql = "insert into sms_js(sj,dxnr,rq) values ('"+hm+"','"+dxmb+"',getdate())";
	           //System.out.println(qf_hm);
	           updater.executeUpdate(jz_sql); 
	           smsResult = "ok";
		   }
		    
		   return smsResult;
		//}
	}

}
