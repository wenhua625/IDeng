package com.novarise.webase.framework;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import com.novarise.webase.BConstants;
import com.novarise.webase.xml.XmlUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;

public class QueryControl {
	
	/**
	 * 
	 * @param s_kjname
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public JSONObject parseDisplayMJJSON(String s_kjname,HttpServletRequest request) throws Exception {

		List fy_result =null;
		List allList=null;
		//连接数据库
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("控件名："+s_kjname +" 控件没找到。");
			}
				
		} catch (Exception e) {
			throw new Exception("找控件时出错："+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //数据连接号
		String cs = y_sql[0][2].trim(); //用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); //业务用的SQL
		String sql2 = y_sql[0][4].trim(); //业务用的SQL2,用于连接上面的SQL
		String xsfs = y_sql[0][5].trim(); //如果页码需显示时，1:全显示 2:只显示下面 3:只显示上面
		String sfxsym = y_sql[0][6].trim(); //是否显示页码,1:显示 0:不显示
		String myhs = y_sql[0][7].trim(); //每页行数
		String defaults = y_sql[0][8].trim(); //数值为NULL时缺省值
		String msjts = y_sql[0][10].trim(); //没有找到数据时提示
		
//		SQL语名句中的查询条件
		String sql_tj = gettjsql(request,s_kjname);
		//SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVarEasyui(sql, request, "");
        //sql = HtmlFunction.decode(sql);
		//String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");
		
        String start=request.getParameter("page");
        if (start == null) start="";
        String limit=request.getParameter("rows");
        if (limit == null) start="";
		//取出符合条件的值
		String all_result[][] = new String[0][0];
		int pageid = 0,pagenum=0,i_rownum = 0; //定义第几页，总页数，总记录数
		int i_myhs = 0;
		
		
	    try
	    {
	       if(allList == null)
	       {
	    	   allList = query.ListQuery(sql);
	    	   
	       }
	       i_rownum=allList.size();
	       
	       if(!start.equals("")){
	    	  fy_result = new ArrayList();
	    	  pageid = Integer.parseInt(start);
	    	  i_myhs = Integer.parseInt(limit);
	    	  pagenum=pageid*i_myhs;
	    	  if(pagenum>i_rownum) pagenum=i_rownum;
	    	  int begin=(pageid-1)*i_myhs;
	    	  //fy_result = query.ListPageQuery(sql, pageid, i_myhs);
	    	  //pagenum = pageid+i_myhs;
	    	  //if (pagenum>i_rownum) pagenum=i_rownum;
	    	  for(int i=begin;i<pagenum;i++)
	    	  {
	    		  fy_result.add(allList.get(i));  
	    	  }
	          
	       }
              
		
	    }catch(Exception ex)
	    {
	    	throw new Exception("控件名："+s_kjname +" 查询时出错。SQL="+sql+"\n\n"+ex.toString());
	    }
	    
	   
			
	    JSONObject json_result = new JSONObject();
		json_result.put("total", Integer.valueOf(i_rownum).toString());
		if(fy_result == null){
			JSONArray json_arr = JSONArray.fromObject(allList);
			json_result.put("rows", json_arr);
		}else{
			JSONArray json_arr = JSONArray.fromObject(fy_result);
			json_result.put("rows", json_arr);
		}
		//处理合计
		y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname.substring(0, 2)+"H"+s_kjname.substring(3,s_kjname.length()));
		if (y_sql == null || y_sql.length == 0)
		{
		}else{
			 ljh = y_sql[0][1].trim(); //数据连接号
			 cs = y_sql[0][2].trim(); //用于加入order by ,group by 语句
			 sql = y_sql[0][3].trim(); //业务用的SQL
			 sql2 = y_sql[0][4].trim(); //业务用的SQL2,用于连接上面的SQL
			 xsfs = y_sql[0][5].trim(); //如果页码需显示时，1:全显示 2:只显示下面 3:只显示上面
			 sfxsym = y_sql[0][6].trim(); //是否显示页码,1:显示 0:不显示
			 myhs = y_sql[0][7].trim(); //每页行数
			 defaults = y_sql[0][8].trim(); //数值为NULL时缺省值
			 msjts = y_sql[0][10].trim(); //没有找到数据时提示
			
            //SQL语名句中的查询条件
			 sql_tj = gettjsql(request,s_kjname);
			//SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
			sql = sql + sql2 + sql_tj + cs;
			sql = HtmlFunction.parseVar(sql, request, "");
			
			try{
				allList = query.ListQuery(sql);
				if (allList.size()>0)
				{
					JSONArray hj_json = JSONArray.fromObject(allList);
					json_result.put("footer", hj_json);
				}
			}catch(Exception ex)
			{
				throw new Exception("控件名："+s_kjname +" 合计查询时出错。SQL="+sql+"\n\n"+ex.toString());
			}
			
			
			
		}
		//System.out.println(json_result.toString());
		

		
		return json_result;
	}
	
	public JSONObject parseDisplayJJJSON(String s_kjname,HttpServletRequest request) throws Exception {

		List fy_result =null;
		List allList=null;
		//连接数据库
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("控件名："+s_kjname +" 控件没找到。");
			}
				
		} catch (Exception e) {
			throw new Exception("找控件时出错："+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //数据连接号
		String cs = y_sql[0][2].trim(); //用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); //业务用的SQL
		String sql2 = y_sql[0][4].trim(); //业务用的SQL2,用于连接上面的SQL
		String xsfs = y_sql[0][5].trim(); //如果页码需显示时，1:全显示 2:只显示下面 3:只显示上面
		String sfxsym = y_sql[0][6].trim(); //是否显示页码,1:显示 0:不显示
		String myhs = y_sql[0][7].trim(); //每页行数
		String defaults = y_sql[0][8].trim(); //数值为NULL时缺省值
		String msjts = y_sql[0][10].trim(); //没有找到数据时提示
		
		if (ljh.equals("group"))
		{
			return  parseDisplayJJGroupJSON( s_kjname, request);
		}
		
		if (myhs == null || myhs.length() == 0)
		{
			myhs="0";
		}
		
//		SQL语名句中的查询条件
		String sql_tj = gettjsql(request,s_kjname);
		//SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVarEasyui(sql, request, "");
        //sql = HtmlFunction.decode(sql);
		//String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");
		
        String start=request.getParameter("page");
        if (start == null) start="";
        String limit=myhs;
        if (limit == null) start="";
		//取出符合条件的值
		String all_result[][] = new String[0][0];
		int pageid = 0,pagenum=0,i_rownum = 0; //定义第几页，总页数，总记录数
		int i_myhs = 0;
		
		
	    try
	    {
	       if(allList == null)
	       {
	    	   allList = query.ListQuery(sql);
	    	   
	       }
	       i_rownum=allList.size();
	       
	       if(Integer.parseInt(myhs) >0){ 
			      if(!start.equals("")){
			    	  
			    	  fy_result = new ArrayList();
			    	  pageid = Integer.parseInt(start);
			    	  i_myhs = Integer.parseInt(limit);
			    	  pagenum = pageid+i_myhs;
			    	  if (pagenum>i_rownum) pagenum=i_rownum;
			    	  for(int i1=pageid;i1<pagenum;i1++)
			    	  {
			    		  fy_result.add(allList.get(i1));  
			    	  }
			       }
			      } 
              
		
	    }catch(Exception ex)
	    {
	    	throw new Exception("控件名："+s_kjname +" 查询时出错。SQL="+sql+"\n\n"+ex.toString());
	    }
	    if (fy_result == null) fy_result = allList;
	    JSONObject json_result = new JSONObject();
	    JSONArray json_arr = JSONArray.fromObject(fy_result);
		json_result.put("rows", json_arr);
		
		return json_result;
			
		}
	
	
	public JSONObject parseDisplayJJGroupJSON(String s_kjname,HttpServletRequest request) throws Exception {

		
		List allList=null;
		//连接数据库
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("控件名："+s_kjname +" 控件没找到。");
			}
				
		} catch (Exception e) {
			throw new Exception("找控件时出错："+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //数据连接号
		String cs = y_sql[0][2].trim(); //用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); //业务用的SQL
		String sql2 = y_sql[0][4].trim(); //业务用的SQL2,用于连接上面的SQL
		String xsfs = y_sql[0][5].trim(); //如果页码需显示时，1:全显示 2:只显示下面 3:只显示上面
		String sfxsym = y_sql[0][6].trim(); //是否显示页码,1:显示 0:不显示
		String myhs = y_sql[0][7].trim(); //每页行数
		String defaults = y_sql[0][8].trim(); //数值为NULL时缺省值
		String msjts = y_sql[0][10].trim(); //没有找到数据时提示
		
		
		
//		SQL语名句中的查询条件
		String sql_tj = gettjsql(request,s_kjname);
		//SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql  + sql_tj + cs;
		sql = HtmlFunction.parseVarEasyui(sql, request, "");
        //sql = HtmlFunction.decode(sql);
		//String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");
		 JSONObject json_result = new JSONObject();
		allList = query.ListQuery(sql);
		
		for(int i_group=0;i_group<allList.size();i_group++)
		{
			
			
			JSONArray a=new JSONArray();
			Map group_map=((Map)allList.get(i_group));
			a.add(group_map);
			
			json_result.put("Group_Name", ((Map)allList.get(i_group)).get("group_name").toString());
			request.setAttribute("Group_Code", ((Map)allList.get(i_group)).get("group_code").toString());
			sql2= HtmlFunction.parseVarAttr(sql2, request, "");
			List z_list=query.ListQuery(sql2);
			JSONObject f=new JSONObject();
			JSONArray json_arr = JSONArray.fromObject(z_list);
			f.put("rows", json_arr);
			a.add(f);
			json_result.put("group", a);
			
			
		}
		
     
	   
		
		return json_result;
			
		}
		
		

		
	
	
	/**
	 * 处理手机端封装多个MJ的情况
	 * @param s_kjname
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public JSONObject parseDisplayMBJSON(String s_kjname,HttpServletRequest request) throws Exception {

		
		//连接数据库
		SQLQuery query = DSManager.getSQLQuery();
		
		String y_kjm[][] = new String[0][0];
		try {
			
			y_kjm = XmlUtil.find(BConstants.PAGE_MB, "KJM", s_kjname);
			if (y_kjm == null || y_kjm.length == 0){
				throw new Exception("控件名："+s_kjname +" 控件没找到。");
			}
				
		} catch (Exception e) {
			throw new Exception("找控件时出错："+e.toString() );
		}
		
		 JSONObject json_result = new JSONObject();
		
		String kjm= y_kjm[0][2];
		//Json别名
		String jsbm= y_kjm[0][3];
	
		String kjms[]= kjm.split(",");
		String jsbms[]=jsbm.split(",");
		
		for(int i=0;i<kjms.length;i++)
		{
			String y_sql[][] = new String[0][0];
			try {
				
				 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", kjms[i]);
				if (y_sql == null || y_sql.length == 0){
					throw new Exception("控件名："+s_kjname +" 控件没找到。");
				}
					
			} catch (Exception e) {
				throw new Exception("找控件时出错："+e.toString() );
			}
			
			
			String cs = y_sql[0][2].trim(); //用于加入order by ,group by 语句
			String sql = y_sql[0][3].trim(); //业务用的SQL
			String sql2 = y_sql[0][4].trim(); //业务用的SQL2,用于连接上面的SQL
			String myhs = y_sql[0][7].trim(); //每页行数
			if (myhs == null || myhs.length() == 0){
				myhs="0";
			}
			
//			SQL语名句中的查询条件
			String sql_tj = gettjsql(request,s_kjname);
			//SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
			sql = sql + sql2 + sql_tj + cs;
			sql = HtmlFunction.parseVarEasyui(sql, request, "");
			List allList=null;
			String start=request.getParameter("page");
	        if (start == null) start="";
	       
	        String limit=myhs;
	        if (limit == null) start="";
	        
	        int pageid = 0,pagenum=0,i_rownum = 0; //定义第几页，总页数，总记录数
			int i_myhs = 0;
	        List fy_result=null;
	        try
		    {
		       if(allList == null)
		       {
		    	   allList = query.ListQuery(sql);
		    	   
		       }
		       i_rownum=allList.size();
		       
		      if(Integer.parseInt(myhs) >0){ 
		      if(!start.equals("")){
		    	  
		    	  fy_result = new ArrayList();
		    	  pageid = Integer.parseInt(start);
		    	  i_myhs = Integer.parseInt(limit);
		    	  pagenum = pageid+i_myhs;
		    	  if (pagenum>i_rownum) pagenum=i_rownum;
		    	  for(int i1=pageid;i1<pagenum;i1++)
		    	  {
		    		  fy_result.add(allList.get(i1));  
		    	  }
		       }
		      }    
			
		    }catch(Exception ex)
		    {
		    	throw new Exception("控件名："+s_kjname +" 查询时出错。SQL="+sql+"\n\n"+ex.toString());
		    }
	        if (fy_result == null) fy_result = allList;

			JSONArray json_arr = JSONArray.fromObject(fy_result);
			json_result.put(jsbms[i], json_arr);
			
		}

		//System.out.println(json_result.toString());
		return json_result;
	}
	
	public JSONArray parseDisplaySigleJSON(String s_kjname,HttpServletRequest request) throws Exception {

		
		List allList=null;
		//连接数据库
		s_kjname="MJ"+s_kjname.substring(2);
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("控件名："+s_kjname +" 控件没找到。");
			}
				
		} catch (Exception e) {
			throw new Exception("找控件时出错："+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //数据连接号
		String cs = y_sql[0][2].trim(); //用于加入order by ,group by 语句
		String sql = y_sql[0][3].trim(); //业务用的SQL
		String sql2 = y_sql[0][4].trim(); //业务用的SQL2,用于连接上面的SQL
		String xsfs = y_sql[0][5].trim(); //如果页码需显示时，1:全显示 2:只显示下面 3:只显示上面
		String sfxsym = y_sql[0][6].trim(); //是否显示页码,1:显示 0:不显示
		String myhs = y_sql[0][7].trim(); //每页行数
		String defaults = y_sql[0][8].trim(); //数值为NULL时缺省值
		String msjts = y_sql[0][10].trim(); //没有找到数据时提示
		
//		SQL语名句中的查询条件
		String sql_tj = gettjsql(request,s_kjname);
		//SQL组成:SQL1+SQL2+条件+附加的语名CS(如升序，降序)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "");
       
       
		
		
	    try
	    {
	       if(allList == null)
	       {
	    	   allList = query.ListQuery(sql);  
	       }
	       
	       
	       
              
		
	    }catch(Exception ex)
	    {
	    	throw new Exception("控件名："+s_kjname +" 查询时出错。SQL="+sql+"\n\n"+ex.toString());
	    }
	    JSONArray json_arr = JSONArray.fromObject(allList);

		return json_arr;
	}
//	取条件sql语句
	private static String gettjsql(HttpServletRequest request, String kjname) throws Exception {
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
								.length() != 0)){
                        if(mjcstj[i][1].indexOf("order") != -1){
                            s_tjsql += "  " + mjcstj[i][2] + " ";
                        }else{
                            s_tjsql += " and " + mjcstj[i][2] + " ";
                        }
					
					}
				else{ if ((String)session.getAttribute(mjcstj[i][1].trim()) != null)
			    	s_tjsql += " and " + mjcstj[i][2] + " ";
				}
			}
		}
		return s_tjsql;
	}
}
