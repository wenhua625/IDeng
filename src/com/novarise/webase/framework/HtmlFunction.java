package com.novarise.webase.framework;



import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.novarise.webase.util.GenerateSequenceUtil;

public class HtmlFunction {
	/**
	 * 解析字符串.如URL传来的参数@@param,和Session中传来的变量
	 */
	public static String parseVar(String content, HttpServletRequest request,
			String med) {
		StringBuffer sb = new StringBuffer();

		String param = null;

		HttpSession session = request.getSession();
		boolean flag = request.getMethod().equals("POST");
		String querystring = request.getQueryString() == null ? ""
				: request.getQueryString();
		querystring = SystemFunction.converGB(querystring);//中文转换
		int pos = content.indexOf("@@"), pos2;
		while (pos != -1) {
			String value = "";
			pos2 = content.substring(pos).indexOf(",");
			if (pos2 == -1)
				break;
			param = content.substring(pos, pos + pos2);

			if (flag) {
				if (param.substring(2).startsWith("CHECK")) //为了处理复选框
				{

					String c_values[] = request.getParameterValues(param
							.substring(2));
					if (c_values == null || c_values.length == 0) {
						value = "";
					} else {
						for (int i = 0; i < c_values.length; i++) {
							value =value+c_values[i] + "','";
							//value+=c_values[i]+",";
						}
						value = value.substring(0, value.length() - 3);
						// value=value.substring(1,value.length()-1);
					}

				} else {

					value = request.getParameter(param.substring(2));
					if(param.substring(2).startsWith("PASSWORD"))
					{
						value = Encrypt.MD5(value);
					}
				}
			} else {
				value = getParam(querystring, param.substring(2));
			}
			if (value == null || value.trim().length() == 0) {
				value = (String) session.getAttribute(param.substring(2));
			}
			if (value == null || value.trim().length() == 0) {
				//value = med.equals("sql") ? "%" : "";
				value="";
			}
			
			try {
				value =java.net.URLDecoder.decode(value,"utf-8");
			} catch (UnsupportedEncodingException e) {
				//value="";
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
			//value = SystemFunction.converGB(value);//中文转换
			//value = SystemFunction.toOriginString(value);//解决dojo中文
			sb.append(content.substring(0, pos) + value);
			content = content.substring(pos + param.length() + 1);
			pos = content.indexOf("@@");

		}
		sb.append(content);

		return sb.toString();
	}
	
	
	/**
	 * 解析字符串.适用与Dwr表单传输方式
	 */
	public static String parseVar(String content, HttpServletRequest request,
			String med,Map formMap) {
		StringBuffer sb = new StringBuffer();

		String param = null;

		HttpSession session = request.getSession();
		
		int pos = content.indexOf("@@"), pos2;
		while (pos != -1) {
			String value = "";
			pos2 = content.substring(pos).indexOf(",");
			if (pos2 == -1)
				break;
			param = content.substring(pos, pos + pos2);
			if (formMap == null) break;
			
			if (formMap.get(param.substring(2)) == null){
				value="";
			}else value = formMap.get(param.substring(2)).toString();
			
			if(param.substring(2).startsWith("PASSWORD")){
				value = Encrypt.MD5(value);
			}
			
			if(param.substring(2).startsWith("KEY")){
				value = GenerateSequenceUtil.generateSequenceNo();
			}
			
			if (value == null || value.trim().length() == 0) {
				value = request.getParameter(param.substring(2));
			}
			
			if (value == null || value.trim().length() == 0) {
				value =  (String)session.getAttribute(param.substring(2));
			}
			if (value == null || value.trim().length() == 0) {
				value = med.equals("sql") ? "" : "";
			}
			//value = SystemFunction.toOriginString(value);//解决dojo中文
			sb.append(content.substring(0, pos) + value);
			content = content.substring(pos + param.length() + 1);
			pos = content.indexOf("@@");

		}
		sb.append(content);

		return sb.toString();
	}
	
	
	/**
	 * 解析字符串.适用与Dwr表单传输方式
	 */
	public static String parseVar(String content, HttpServletRequest request,
			String med,Map recordMap,Map formMap) {
		StringBuffer sb = new StringBuffer();

		String param = null;

		HttpSession session = request.getSession();
		
		int pos = content.indexOf("@@"), pos2;
		while (pos != -1) {
			String value = "";
			pos2 = content.substring(pos).indexOf(",");
			if (pos2 == -1)
				break;
			param = content.substring(pos, pos + pos2);
			if (recordMap == null) break;
			
			if (recordMap.get(param.substring(2)) == null){
				value="";
			}else value = recordMap.get(param.substring(2)).toString();
			
			if(param.substring(2).startsWith("PASSWORD")){
				value = Encrypt.MD5(value);
			}
			if(param.substring(2).startsWith("KEY")){
				value = GenerateSequenceUtil.generateSequenceNo();
			}
			if (value == null || value.trim().length() == 0) {
				value =  (String)session.getAttribute(param.substring(2));
			}
			
			if (value == null || value.trim().length() == 0) {
				value =  (String)formMap.get(param.substring(2));
			}
			if (value == null || value.trim().length() == 0) {
				value = med.equals("sql") ? "" : "";
			}
			//value = SystemFunction.toOriginString(value);//解决dojo中文
			sb.append(content.substring(0, pos) + value);
			content = content.substring(pos + param.length() + 1);
			pos = content.indexOf("@@");

		}
		sb.append(content);

		return sb.toString();
	}
	
	/**
	 * 解析字符串.适用与Dwr表单传输方式
	 */
	public static String parseEncodeVar(String content, HttpServletRequest request,
			String med,Map formMap) {
		StringBuffer sb = new StringBuffer();

		String param = null;

		HttpSession session = request.getSession();
		
		int pos = content.indexOf("@@"), pos2;
		while (pos != -1) {
			String value = "";
			pos2 = content.substring(pos).indexOf(",");
			if (pos2 == -1)
				break;
			param = content.substring(pos, pos + pos2);
			if (formMap == null) break;
			
			if (formMap.get(param.substring(2)) == null){
				value="";
			}else value = formMap.get(param.substring(2)).toString();
			
			if(param.substring(2).startsWith("PASSWORD")){
				value = Encrypt.MD5(value);
			}
			
			if(param.substring(2).startsWith("KEY")){
				value = GenerateSequenceUtil.generateSequenceNo();
			}
			
			if (value == null || value.trim().length() == 0) {
				value =  (String)session.getAttribute(param.substring(2));
			}
			if (value == null || value.trim().length() == 0) {
				value = med.equals("sql") ? "" : "";
			}
			
			try {
				value =java.net.URLEncoder.encode(value,"utf-8");
			} catch (UnsupportedEncodingException e) {
				//value="";
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//value = SystemFunction.toOriginString(value);//解决dojo中文
			sb.append(content.substring(0, pos) + value);
			content = content.substring(pos + param.length() + 1);
			pos = content.indexOf("@@");

		}
		sb.append(content);

		return sb.toString();
	}
	
	/**
	 * 解析短信模板字符串.
	 */
	public static String parseSMSTemplate(String content,Map formMap) {
		StringBuffer sb = new StringBuffer();
		String param = null;
		int pos = content.indexOf("{"), pos2;
		while (pos != -1) {
			String value = "";
			pos2 = content.substring(pos).indexOf("}");
			if (pos2 == -1)
				break;
			param = content.substring(pos, pos + pos2);
			if (formMap == null) break;
			
			if (formMap.get(param.substring(1)) == null){
				value="";
			}else 
				value = formMap.get(param.substring(1)).toString();
			sb.append(content.substring(0, pos) + value);
			content = content.substring(pos + param.length() + 1);
			pos = content.indexOf("{");

		}
		sb.append(content);
		return sb.toString();
	}
	
	/**
	 * 解析字符串.easyui传过来的参数，要做utf-8转换处理
	 */
	public static String parseVarEasyui(String content, HttpServletRequest request,
			String med) {
		StringBuffer sb = new StringBuffer();

		String param = null;

		HttpSession session = request.getSession();
		boolean flag = request.getMethod().equals("POST");
		String querystring = request.getQueryString() == null ? ""
				: request.getQueryString();
		querystring = SystemFunction.converGB(querystring);//中文转换
		int pos = content.indexOf("@@"), pos2;
		while (pos != -1) {
			String value = "";
			pos2 = content.substring(pos).indexOf(",");
			if (pos2 == -1)
				break;
			param = content.substring(pos, pos + pos2);

			if (flag) {
				if (param.substring(2).startsWith("CHECK")) //为了处理复选框
				{

					String c_values[] = request.getParameterValues(param
							.substring(2));
					if (c_values == null || c_values.length == 0) {
						value = "";
					} else {
						for (int i = 0; i < c_values.length; i++) {
							value =value+c_values[i] + "','";
							//value+=c_values[i]+",";
						}
						value = value.substring(0, value.length() - 3);
						// value=value.substring(1,value.length()-1);
					}

				} else {

					value = request.getParameter(param.substring(2));
					if(param.substring(2).startsWith("PASSWORD"))
					{
						value = Encrypt.MD5(value);
					}
				}
			} else {
				value = getParam(querystring, param.substring(2));
			}
			if (value == null || value.trim().length() == 0) {
				value = (String) session.getAttribute(param.substring(2));
			}
			if (value == null || value.trim().length() == 0) {
				//value = med.equals("sql") ? "%" : "";
				value="";
			}
			
			try {
				value =java.net.URLDecoder.decode(value,"utf-8");
			} catch (UnsupportedEncodingException e) {
				//value="";
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			//else
			//{
			//value=med.equals("sql")?value.replaceAll("'","''"):value;
			// }
			//value=this.htmljc(value.trim());
			
			//value = SystemFunction.converGB(value);//中文转换
			//value = SystemFunction.toOriginString(value);//解决dojo中文
			sb.append(content.substring(0, pos) + value);
			content = content.substring(pos + param.length() + 1);
			pos = content.indexOf("@@");

		}
		sb.append(content);

		return sb.toString();
	}
	
	
    public static ArrayList parseBatchVar(String content,HttpServletRequest request) {
        StringBuffer sb = new StringBuffer();
        ArrayList paramList = new ArrayList();
        String param = null;
        HttpSession session = request.getSession();
       boolean flag = request.getMethod().equals("POST");
      
        int pos = content.indexOf("@@"), pos2;
        while (pos != -1) {
            String value = "";
            String arrValue[];
            pos2 = content.substring(pos).indexOf(",");
            if (pos2 == -1)
                break;
            param = content.substring(pos, pos + pos2);
            paramList.add(param.substring(2));
            content = content.substring(pos + param.length() + 1);
            pos = content.indexOf("@@");

        }
        //sb.append(content);

        return paramList;
    }
	
    /**
	 * 解析字符串.用于解析通过request.Attrible方式传过来的数据
	 */
	public static String parseVarAttr(String content, HttpServletRequest request,
			String med) {
		StringBuffer sb = new StringBuffer();

		String param = null;
		HttpSession session = request.getSession();
		boolean flag = request.getMethod().equals("POST");
		String querystring = request.getQueryString() == null ? ""
				: request.getQueryString();
		querystring = SystemFunction.converGB(querystring);//中文转换
		int pos = content.indexOf("@@"), pos2;
		while (pos != -1) {
			String value = "";
			pos2 = content.substring(pos).indexOf(",");
			if (pos2 == -1)
				break;
			param = content.substring(pos, pos + pos2);

			if (flag) {
				  
					value = (String)request.getAttribute(param.substring(2));
					if (value == null || value.trim().length() == 0) {
						value = request.getParameter(param.substring(2));
					}
					if(param.substring(2).startsWith("PASSWORD"))
					{
						value = Encrypt.MD5(value);
					}
				
			} else {
				value = getParam(querystring, param.substring(2));
				if (value == null || value.trim().length() == 0) {
				    value = (String)request.getAttribute(param.substring(2));
				}
			}
			if (value == null || value.trim().length() == 0) {
				value = (String) session.getAttribute(param.substring(2));
			}
			if (value == null || value.trim().length() == 0) {
				
				value="";
			}
			
			try {
				value =java.net.URLDecoder.decode(value,"utf-8");
			} catch (UnsupportedEncodingException e) {
				
				e.printStackTrace();
			}
			sb.append(content.substring(0, pos) + value);
			content = content.substring(pos + param.length() + 1);
			pos = content.indexOf("@@");

		}
		sb.append(content);

		return sb.toString();
	}

	/**
	 * 解析HTML中的数据内容
	 */
	public static String parseHtmlContent1(String old_string, String kjname,
			String[][] s_result, String s_xsfs, String s_default) {
		StringBuffer sb = new StringBuffer();
		String s_marker = "$$" + kjname + ",", s_temp;
		if (s_result == null || s_result.length == 0) {
			s_temp = old_string;
			for (int col = 0; col < 40; col++) {
				s_temp = SystemFunction.replace(s_temp, s_marker
						+ SystemFunction.parseNormalString(col + 1), s_default);
			}
			sb.append(s_temp);
		} else {
			for (int row = 0; row < s_result.length; row++) {
				if (row == 1 && s_xsfs.equals("1"))
					break; //显示方式为单行显示
				s_temp = old_string;
				for (int col = 0; col < s_result[row].length; col++) {
					s_temp = SystemFunction.replace(s_temp, s_marker
							+ SystemFunction.parseNormalString(col + 1),
							s_result[row][col]);
				}
				sb.append(s_temp);
			}
		}
		return sb.toString();
	}
	
	/**
	 * 解析HTML中的数据内容
	 */
	public static String parseHtmlContent(String old_string, String kjname,
			String[][] s_result, String msjts,String ljh, String s_default) {
		StringBuffer sb = new StringBuffer();
		String s_marker = "$$" + kjname + ",", s_temp;
		if (s_result == null || s_result.length == 0) {
			/*s_temp = old_string;
			for(int row =0;row < myhs; row++){
				for (int col = 0; col < 40; col++) {
				s_temp = SystemFunction.replace(s_temp, s_marker
						+ SystemFunction.parseNormalString(col + 1), s_default);
			}
				
					sb.append(s_temp);
				
			
			}*/
			if( msjts.length()== 0)
				sb.append("");
			else
			   sb.append("<tr></td><table width='100%' border='0'><tr><td><font color='#FF0000'>"+msjts+"</font></td></tr></table></td></tr>");
		} else {
			for (int row = 0; row < s_result.length; row++) {
				s_temp = old_string;
				for (int col = 0; col < s_result[row].length; col++) {
					if(ljh.equals("5"))//显示为HTML,暂加
					{
					s_temp = SystemFunction.replace(s_temp, s_marker
							+ SystemFunction.parseNormalString(col + 1),
							htmljc(s_result[row][col]));
					}else
					{
						s_temp = SystemFunction.replace(s_temp, s_marker
								+ SystemFunction.parseNormalString(col + 1),
								s_result[row][col]);
					}
				}
				sb.append(s_temp);
			}
			
			
		}
		return sb.toString();
	}
	
	/**
	 * 解析HTML中的数据内容
	 */
	public static String parseTableContent(
			String[][] s_result) {
		StringBuffer sb = new StringBuffer();
		for (int row = 0; row < s_result.length; row++) {
				String  s_col="";
				for (int col = 0; col < s_result[row].length; col++) {
					s_col += "<td>"+ s_result[row][col]+"</td>";
					
				}
				sb.append("<tr>"+s_col+"</tr>");
			}
			
			
		
		return sb.toString();
	}
	

	/**
	 * 替换!!,结果为记录集
	 * 
	 * @param content
	 * @param kjname
	 * @param result
	 * @return
	 */
	public static String parseHtmlContent(String content, String kjname,
			String[][] result) {
		String s_marker = "!!" + kjname + ",";
		if (result.length == 0 || result == null) {
			for (int col = 0; col < 70; col++) {
				content = SystemFunction.replace(content, s_marker
						+ SystemFunction.parseNormalString(col + 1), "");
			}
		} else {
			for (int col = 0; col < result[0].length; col++) {
				//显示为HTML,暂加htmljc()
				content = SystemFunction.replace(content, s_marker
							+ SystemFunction.parseNormalString(col + 1),
							SystemFunction.null2str(result[0][col]));
				
			}
		}
		
		return content;
	}

	/**
	 * 将从QueryString中提取与参数名相同的字符串
	 * 
	 * @param s_querystring
	 * @param s_paramname
	 * @return 提取出来的值
	 */
	public static String getParam(String s_querystring, String s_paramname) {
		StringTokenizer st = new StringTokenizer(s_querystring, "&");
		while (st.hasMoreTokens()) {
			String para = st.nextToken();
			if (para.substring(0, para.indexOf("=")).equals(s_paramname)) {
				return para.substring(para.indexOf("=") + 1);
			}
		}
		return null;
	}

	/**
	 * 取得HTML页面得头
	 * 
	 * @param s_html
	 * @param kjname
	 * @return
	 */
	public static String getHtmlHead(String htmlfilecontext, String s_kjname) {
		String s_html = htmlfilecontext, s_marker = "$$" + s_kjname + ",";
		
		
		int i_marker = htmlfilecontext.indexOf(s_marker);
		
		if (i_marker != -1) {
			int lastTrPos = htmlfilecontext.substring(0,i_marker).lastIndexOf("<tr");
			s_html = htmlfilecontext.substring(0, lastTrPos);
		}
		return s_html;
	}

	/**
	 * 取得HTML页面的内容
	 * 
	 * @param s_html
	 * @param kjname
	 * @return
	 */
	public static String getHtmlContext(String htmlfilecontext, String s_kjname) {
		String s_html = "", s_marker = "$$" + s_kjname + ",";
		if (htmlfilecontext.indexOf(s_marker) != -1) {
			s_html = htmlfilecontext.substring(htmlfilecontext.substring(0,
					htmlfilecontext.indexOf(s_marker)).lastIndexOf("<tr"));
			if (s_html.indexOf("/table>") != -1) {
				s_html = s_html.substring(0, s_html.indexOf("/table>"));
				s_html = s_html.substring(0, s_html.indexOf("</tr>", s_html
						.lastIndexOf(s_marker)) + 5);
			}
		}
		return s_html;
	}

	/**
	 * 取得HTML页面的尾部
	 * 
	 * @param s_html
	 * @param kjname
	 * @return
	 */
	public static String getHtmlEnd(String htmlfilecontext, String s_kjname) {
		String s_html = "", s_marker = "$$" + s_kjname + ",", s_temphtml;
		if (htmlfilecontext.indexOf(s_marker) != -1) {
			s_html = htmlfilecontext.substring(htmlfilecontext
					.indexOf(s_marker));
			s_temphtml = s_html.substring(0, s_html.indexOf("</table>"));
			int weizhi = s_temphtml.indexOf("</tr>", s_temphtml
					.lastIndexOf(s_marker));
			if (weizhi != -1) {
				s_html = s_html.substring(weizhi + 5);
			} else
				s_html = "取HTML尾内容时gethtmlend时出错！请重新制作该表格！";
		}
		return s_html;
	}

	public static String gettdhead(String s_tr, String kjname) {
		String s_tdhead = s_tr, kjmarker = "$$" + kjname + ",";
		if (s_tr.indexOf(kjmarker) != -1
				&& s_tr.substring(0, s_tr.indexOf(kjmarker)).lastIndexOf("<td") != -1) {
			s_tdhead = s_tr.substring(0, s_tr.substring(0,
					s_tr.indexOf(kjmarker)).lastIndexOf("<td"));
		} else {
			s_tdhead = kjname + "表格头不符合标准！请重新制作！";
		}
		return s_tdhead;
	}

	//HTML模板内容变量中**
	// ************************************************************
	public static String gettdcontext(String s_tr, String kjname) {
		String s_tdc = "", kjmarker = "$$" + kjname + ",";
		if ((s_tr.indexOf(kjmarker) != -1)
				&& (s_tr.substring(0, s_tr.indexOf(kjmarker))
						.lastIndexOf("<td") != -1)
				&& (s_tr.substring(s_tr.indexOf(kjmarker)).indexOf("/td>") != -1)) {
			s_tr = s_tr.substring(s_tr.substring(0, s_tr.indexOf(kjmarker))
					.lastIndexOf("<td"));
			s_tdc = s_tr.substring(0, s_tr.indexOf("/td>", s_tr
					.indexOf(kjmarker)) + 4);
		} else {
			s_tdc = kjname + "表格内容不符合标准！请重新制作！";
		}
		return s_tdc;
	}

	//****************************************************************************
	public static String gettdend(String s_tr, String kjname) {

		String s_tdend = "", kjmarker = "$$" + kjname + ",";
		if (s_tr.indexOf(kjmarker) != -1
				&& s_tr.indexOf("/td>", s_tr.indexOf(kjmarker)) != -1) {
			s_tr = s_tr.substring(s_tr.indexOf(kjmarker));
			s_tdend = s_tr.substring(s_tr.indexOf("/td>") + 4);
		} else {
			s_tdend = kjname + "表格尾不符合标准！请重新制作！";
		}
		return s_tdend;
	}

	//从request获得上下页相关的链接
	public static String getURL(HttpServletRequest request) throws Exception {
		String s_url = request.getRequestURI() + "?proname="
				+ request.getParameter("proname");
		boolean b = request.getMethod().equals("POST");
		String s_queryString = request.getQueryString() == null ? ""
				: request.getQueryString();
		s_queryString = SystemFunction.replace(s_queryString,"%20"," ");
		s_queryString = SystemFunction.converGB(s_queryString);//中文转换
		Enumeration em_params = request.getParameterNames();
		String s_param, s_value;
		while (em_params.hasMoreElements()) {
			s_param = (String) em_params.nextElement();
			if (!b) {
				s_value = getParam(s_queryString, s_param);
			} else {
				s_value = request.getParameter(s_param);
			}
			//s_value = SystemFunction.converGB(s_value);
			if (s_value != null && s_value.trim().length() != 0
					&& !s_param.equals("page") && !s_param.equals("proname")) {
				s_url += "&" + s_param + "=" + s_value;
			}
		}
		
		return s_url;
	}
	
	public static List getPageMarket(List ls_kj,String htmlfile,String market){
		
		String temp = htmlfile;
		int pos = 0;
		pos = temp.indexOf(market);
		while (pos != -1) {
			if (temp.substring(pos + 2).length() < 16)
				break;
			String kjt = temp.substring(pos + 2, pos + 8);
			
			if (!ls_kj.contains(kjt)){
				
				ls_kj.add(kjt);
			}
				
			temp = temp.substring(pos + 2);
			pos = temp.indexOf(market);
		}
		return ls_kj;
	}
	
public static List getPageMarket(String htmlfile,String market){
		List ls_kj = new ArrayList();
		String temp = htmlfile;
		int pos = 0,pos2;
		pos = temp.indexOf(market);
		while (pos != -1) {
			
			pos2 = temp.substring(pos).indexOf(",");
			if (pos2  == -1) break;
			String kjt = temp.substring(pos , pos + pos2);
			
			if (!ls_kj.contains(kjt)){
				
				ls_kj.add(kjt);
			}
				
			temp = temp.substring(pos + 2);
			pos = temp.indexOf(market);
		}
		return ls_kj;
	}

	//解析
	public static String htmljc(String html) {
		String temp;
		//temp = SystemFunction.replace(html, "<", "&lt;");
		//temp = SystemFunction.replace(temp, ">", "&gt;");
		temp = SystemFunction.replace(html, "\r\n", "<p>");
		temp = SystemFunction.replace(temp, "\"", "&quot;");
		temp = SystemFunction.replace(temp, " ", "&nbsp;");
		//temp=SystemFunction.replace(temp,"'","&#np") ;
		temp = SystemFunction.replace(temp, "'", "&#39");
		temp = SystemFunction.replace(temp, "\t", "&nbsp;&nbsp;&nbsp;&nbsp;");
		
		return temp;
	}

	//反解析
	public static String rehtmljc(String html) {
		String temp;
		temp = SystemFunction.replace(html, "&lt;", "<");
		temp = SystemFunction.replace(temp, "&gt;", ">");
		temp = SystemFunction.replace(temp, "<br>", "\n");
		temp = SystemFunction.replace(temp, "&quot;", "\"");
		temp = SystemFunction.replace(temp, "&nbsp;", " ");
		//temp=SystemFunction.replace(temp,"'","&#np") ;
		temp = SystemFunction.replace(temp, "&#39", "'");
		return temp;
	}
	
	 public static String decode(String content)
	 {
	    	String value;
	    	try {
				value =java.net.URLDecoder.decode(content,"utf-8");
			} catch (Exception e) {
				value=content;
			}
			return value;
	  }
	


}
