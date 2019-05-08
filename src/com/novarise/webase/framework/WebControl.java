package com.novarise.webase.framework;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;




import com.jspsmart.upload.SmartUpload;
import com.novarise.webase.BConstants;
import com.novarise.webase.util.ChartUtils;
import com.novarise.webase.util.DateHelper;
import com.novarise.webase.util.HttpClientUtil;
import com.novarise.webase.util.ImageCompress;
import com.novarise.webase.util.JsonUtil;
import com.novarise.webase.util.PicVideoUtils;
import com.novarise.webase.xml.XmlUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;
import com.ripple.datasource.SQLUpdater;




public class WebControl {

	/**
	 * ʵ��HTML��������ʾ�ķ��� ʵ�ַ���: 1,����HTMLҳ�� 2,�������ݿ�,ȡ��ҵ��SQL 3,����ҵ��SQLȡ�����������ļ�¼
	 * 4,����HTMLҳ�� 5,����
	 */
	public String parseDisplayMJ(String html, String s_kjname,
			HttpServletRequest request) throws Exception {

		// ��HTMLҳ��ͷ�����ݣ�β��ֵ

		String t_html = HtmlFunction.getHtmlHead(html, s_kjname);
		String c_html = HtmlFunction.getHtmlContext(html, s_kjname);
		String e_html = HtmlFunction.getHtmlEnd(html, s_kjname);

		// ȡ��ҵ��SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("����" + s_kjname + "����!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("�ؼ���" + s_kjname + "û�ҵ�!");
		}
		// ��ȡҵ����������
		String ljh = y_sql[0][1].trim(); // �������Ӻ�
		String cs = y_sql[0][2].trim(); // ���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); // ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); // ҵ���õ�SQL2,�������������SQL
		String xsfs = y_sql[0][5].trim(); // ���ҳ������ʾʱ��1:ȫ��ʾ 2:ֻ��ʾ���� 3:ֻ��ʾ����
		String sfxsym = y_sql[0][6].trim(); // �Ƿ���ʾҳ��,1:��ʾ 0:����ʾ
		String myhs = y_sql[0][7].trim(); // ÿҳ����
		String defaults = y_sql[0][8].trim(); // ��ֵΪNULLʱȱʡֵ
		String msjts = y_sql[0][10].trim(); // û���ҵ�����ʱ��ʾ

		// SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request, s_kjname);
		// SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "");

		// ��ʼ��ȡʵ������
		SQLQuery query = DSManager.getSQLQuery();
		String result[][] = new String[0][0];
		String all_result[][] = new String[0][0];

		int pageid = 1, pagenum = 1, i_rownum = 0; // ����ڼ�ҳ����ҳ�����ܼ�¼��
		int i_myhs = 0;

		if (sfxsym.equals("1")) // �Ƿ���ʾҳ�� 1:��ʾҳ�� 2:����ʾ
		{
			pageid = request.getParameter("page") != null ? Integer
					.parseInt(request.getParameter("page")) : 1;
			if (pageid < 0)
				pageid = 1;

			i_myhs = Integer.parseInt(myhs);
			try {
				all_result = query.ArrayQuery(sql);

			} catch (Exception e) {
				throw new Exception("����" + s_kjname + "����!\n" + e.toString()
						+ "\n" + sql);
			}
			i_rownum = all_result.length;

			pagenum = ((int) (i_rownum - 1) / i_myhs) + 1; // ����һ������ҳ
			result = query.ArrayPageQuery(sql, pageid, i_myhs);

		} else {
			try {
				result = query.ArrayQuery(sql);
			} catch (Exception e) {
				throw new Exception("����" + s_kjname + "����!\n" + e.toString()
						+ "\n" + sql);
			}
		}

		// ����HTMLҳ��
		t_html = HtmlFunction.parseHtmlContent(t_html, s_kjname, result);
		c_html = HtmlFunction.parseHtmlContent(c_html, s_kjname, result);
		e_html = HtmlFunction.parseHtmlContent(e_html, s_kjname, result);

		c_html = HtmlFunction.parseHtmlContent(c_html, s_kjname, result, msjts,
				ljh, defaults);
		String c_page = "";

		if (sfxsym.equals("1")) {// && i_rownum > 1
			if (result.length != 0) {
				String s_url = HtmlFunction.getURL(request);

				if (pagenum > 1) {
					if (pageid > 1) {
						c_page += "<tr><td height='20' align='right'><a href='"
								+ s_url + "&page=1'>��ҳ</a>��";
						c_page += "<a href='" + s_url + "&page=" + (pageid - 1)
								+ "'>��ҳ</a>��";
					} else {
						c_page += "<tr><td height='20' align='right'>��ҳ  ��ҳ  ";
					}
					if (pageid < pagenum) {
						c_page += "<a href='" + s_url + "&page=" + (pageid + 1)
								+ "'>��ҳ</a>��";
						c_page += "<a href='" + s_url + "&page=" + pagenum
								+ "'>βҳ</a>����";
					} else {
						c_page += "��ҳ  βҳ  ";
					}
				} else {
					c_page += "<tr><td height='20' align='right'>";
				}

				int i_endrow = (pageid * i_myhs >= i_rownum) ? i_rownum
						: pageid * i_myhs;
				c_page += "��" + ((pageid - 1) * i_myhs + 1) + "��" + i_endrow
						+ "��/��<font color=\"red\">" + i_rownum + "</font>��������"
						+ pageid + "ҳ/��" + pagenum + "ҳ</td></tr>";
			}
			e_html = SystemFunction.replace(e_html, "$$page,", c_page);
			t_html = SystemFunction.replace(t_html, "$$page,", c_page);

		}

		return t_html + c_html + e_html;
	}

	public void parseForward(String s_kjname, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		// ȡ��ҵ��SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_FW, "KJM", s_kjname);
		} catch (Exception e) {
			throw new Exception("����" + s_kjname + "����!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("�ؼ���" + s_kjname + "û�ҵ�!");
		}

		String target = y_sql[0][1];

		target = HtmlFunction.parseVar(target, request, "");

		if (target == null)
			target = "";
		if (target.length() != 0) {
			request.getRequestDispatcher(target).forward(request, response);

		}

	}

	// ȡ����sql���
	private static String gettjsql(HttpServletRequest request, String kjname)
			throws Exception {
		HttpSession session = request.getSession(true);
		String[][] mjcstj = new String[0][0];
		try {
			mjcstj = XmlUtil.find(BConstants.PAGE_MJ_TJ, "KJM", kjname);
		} catch (SQLException se1) {
			throw new Exception("ȡ����SQLʱ����" + se1.toString());
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

	// ����import�ļ�
	// ��ʽΪ<!--import=filename!-->
	// �ɽ�filenameΪimport�ļ������·����filename=import/head.htm�������ж���Ŀո�!
	public String parseDisplayIM(String s_html) throws Exception {
		String root = XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT);
		int i_wz1, i_wz2;
		String s_filename = "", s_file;
		while (((i_wz1 = s_html.indexOf("<!--$$import=")) != -1)
				&& ((i_wz2 = s_html.indexOf("!-->", i_wz1)) != -1)) {
			s_filename = s_html.substring(i_wz1 + 13, i_wz2);
			s_file = SystemFunction.readFile(root + s_filename.trim());
			s_html = s_html.substring(0, i_wz1) + s_file
					+ s_html.substring(i_wz2 + 4);
		}
		return s_html;
	}

	// �˳�ϵͳ
	public void parseDisplayQUIT(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(true);
		session.removeAttribute("LS.YHDM");
		//session.invalidate();
		request.getRequestDispatcher(BConstants.EXIT_URL).forward(request,
				response);
	}
	
//	 ������ʾ
	public String parseDisplayTip(HttpServletRequest request) throws Exception {

		String returnString = "";
		HttpSession session = request.getSession(true);
		SQLQuery query = DSManager.getSQLQuery();

		String sts = SystemFunction.null2SpaceString((String) session
				.getAttribute("LS.ZS"));
		String yhh = SystemFunction.null2SpaceString((String) session
				.getAttribute("LS.YHDM"));
		String yhz = SystemFunction.null2SpaceString((String) session
				.getAttribute("LS.YHZEG"));

		String stsStr = "";
		String stsArr[] = sts.split(",");
		for (int i = 0; i < stsArr.length; i++)
			stsStr = stsStr + "'" + stsArr[i] + "',";
		stsStr = stsStr.substring(0, stsStr.length() - 1);
		String sql = "";
		if (yhz.equals("ywy"))
			sql = "select sts_demo,count(1) sts_num  from order_list a,order_sts b where a.order_sts = b.order_sts  and a.order_sts in ("
					+ stsStr
					+ ") and agent_code in (select agent_code from agent_list where domain_man='"
					+ yhh + "') group by sts_demo";
		else
			sql = "select sts_demo,count(1) sts_num from order_list a,order_sts b where a.order_sts = b.order_sts  and a.order_sts in ("
					+ stsStr + ") group by sts_demo";
		String resultData[][] = query.ArrayQuery(sql);
		if (resultData.length == 0) {
			return returnString;
		}
		returnString = "<table border=0 class=\"w01_13px_black\">";
		for (int i = 0; i < resultData.length; i++) {
			returnString += "<tr><td>���� " + resultData[i][1] + " ������Ҫ "
					+ resultData[i][0] + "</td></tr>";
		}
		returnString += "</table>";
		return returnString;
	}

	// ��½ϵͳ
	public String parseDisplayQX(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession(true);
		SQLQuery query = DSManager.getSQLQuery();
		String[][] s_qxcs = new String[0][0];
		String s_qxsql = "";
		String s_yhdm = "", s_yhmm = "", s_ipmac = "";
		;
		if (request.getParameter("username") != null)
			s_yhdm = request.getParameter("username").trim();
		if (request.getParameter("password") != null)
			s_yhmm = request.getParameter("password").trim();
		/*if (request.getParameter("ipmac") != null)
			s_ipmac = request.getParameter("ipmac").trim();
		s_qxsql = "select Ip_Mac from tj_sys_Ip where Ip_Mac='" + s_ipmac + "'";
		try {
			s_qxcs = query.ArrayQuery(s_qxsql);
		} catch (SQLException e) {
			return SystemFunction.showError(802, "Ȩ��SQL������", e.getMessage()
					+ s_qxsql, request);
		}*/
		// if (s_qxcs.length == 0){
		// return SystemFunction.showLoginError(803,
		// "��ĵ��Ի�û�е��ܲ�����(��Ȩ).\n\n���ܷ���ϵͳ������ϵϵͳ����Ա!", s_ipmac);
		// }
		s_qxsql = "select Yhh,a.Yhz,Yhxm,Dept,Mm,a.Sts,a.yhz YhZMC,Yxq,Warecode,'' ZS,dbo.getAgentName(Dept) agent_name,a.qx_cd,a.qx_qt,a.qx_ck,a.qx_dj,a.qx_ts,a.qx_sh,a.qx_mob_dp  from tj_sys_yh a where 1=1"
				+ " and a.lxfs='"
				+ SystemFunction.replace(s_yhdm, "'", "''")
				+ "'";// STSΪ'1'ʱ����,����Ϊ�

		try {
			s_qxcs = query.ArrayQuery(s_qxsql);
		} catch (SQLException e) {
			return SystemFunction.showError(802, "Ȩ��SQL������", e.getMessage()
					+ s_qxsql, request);
		}
		if (s_qxcs.length == 0) {
			return SystemFunction.showLoginError(803, "û������û���!", s_yhdm);
		}
		// if (s_qxcs[0][11].trim().indexOf(s_mac) == -1){
		// System.out.println("dd:"+s_mac);
		// return SystemFunction.showLoginError(805, "����û�о�����˾��Ȩ,���빫˾ϵͳ����Ա��ϵ!",
		// s_mac);
		// }
		if (!(s_qxcs[0][4].equals(Encrypt.MD5(s_yhmm)))) {
			return SystemFunction.showLoginError(804, "�û��������!", "");
		}
		String s_yhh = s_qxcs[0][0].trim();
		String s_yhz = s_qxcs[0][1].trim();
		String s_yhmc = s_qxcs[0][2].trim();
		String s_dwdm = s_qxcs[0][3].trim();
		String s_sts = s_qxcs[0][5].trim();
		String s_yhzmc = s_qxcs[0][6].trim();
		String s_yxq = s_qxcs[0][7].trim();
		String s_ware = s_qxcs[0][8].trim();
		String s_zs = s_qxcs[0][9].trim();
		String s_agentName = s_qxcs[0][10].trim();
		String brand_url="";
		String brand_imageUrl="";
		String brand_code="";
		// String s_authMac = s_qxcs[0][11].trim();
		
		
//		�ն�ϵͳ�ؼ�
		try {
			if(s_yhz.equals("�ն˵���")){
				String vod_sql="select datediff(day,getdate(),convert(datetime,end_date)) valid_days,open_flag,isnull(brand_url,'') brand_url,isnull(brand_imageurl,'') brand_imageurl,a.brand_code  from agent_list a,brand_list b where a.brand_code=b.brand_name and  agent_code='"+s_dwdm+"'";
				String s_v[][]= query.ArrayQuery(vod_sql);
				String validDays=s_v[0][0];
				String openFlag=s_v[0][1];
				 brand_url=s_v[0][2];
				 brand_imageUrl=s_v[0][3];
				 brand_code = s_v[0][4];
				int i_vDays=Integer.parseInt(validDays);
				if(i_vDays<0){
					return SystemFunction.showLoginError(805, "��ͬ�Ѿ�����,����ǩ��ͬ������������ϵ!",
					"");
				}
				if(!openFlag.equals("����")){
					return SystemFunction.showLoginError(805, "�ͻ��Ѿ����ܲ�ͣ���ˣ�����ϵ��˾�ͷ���Ա!",
					"");
				}
			}
		} catch (Exception e) {
			return SystemFunction.showError(802, "��ͬ�������ڲ���ȷ���޷���½ϵͳ������ϵͳ����Ա��ϵ��", e.getMessage()
					+ s_qxsql, request);
		}

		if (s_sts.equals("1")) {
			return SystemFunction.showLoginError(805, "�ʺ��ѽ���,���Ҫ����,����ϵͳ����Ա��ϵ!",
					"");
		}

		SimpleDateFormat formater = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date d_yxq = null;
		try {
			d_yxq = formater.parse(s_yxq);
		} catch (Exception e) {
			return SystemFunction.showLoginError(900, "��ʽ�����ڳ���,����ϵϵͳ����Ա��", e
					.toString());
		}
		java.util.Date nowDate = new java.util.Date();
		long d_yxqts = (d_yxq.getTime() - nowDate.getTime())
				/ (1000 * 60 * 60 * 24);
		
	    //System.out.println("ts:"+d_yxqts);
		if(s_yhz.equals("�ն˵���")){
	         /*if(d_yxqts <= 10) {
	        	 String con=request.getParameter("con");
	        	 if(con == null ) con="";
	        	 if(!con.equals("1"))
	        	   return SystemFunction.showWeiXinPay(s_yhdm,"�����˺��뵽�ڻ��� "+String.valueOf(d_yxqts)+" �죬������Ϊ��"+SystemFunction.getDate(d_yxq)+",�����ѣ���������΢��ɨ�����¶�ά����ɽɷѣ�  ����ʹ����<a href='work?proname=LOGON&con=1&username="+s_yhdm+"&password="+s_yhmm+"'>���</a>",request.getRemoteAddr()); 
	         }*/
	         //if(d_yxqts < 0) { return
		       // SystemFunction.showWeiXinPay(s_yhdm,"�����˺��ѵ��ڣ�������Ϊ��"+SystemFunction.getDate(d_yxq)+",�����ѣ���������΢��ɨ�����¶�ά����ɽɷ�",request.getRemoteAddr()); }
	    }

		/*try {
			s_qxcs = XmlUtil.find(BConstants.PAGE_QX, "YHZ", s_yhz);
		} catch (Exception e) {
			return SystemFunction.showLoginError(806, "ȡ�û����ܴ���SQL������", e
					.toString());
		}
		String s_gndm = "000";
		for (int i = 0; i < s_qxcs.length; i++)
			s_gndm += "," + s_qxcs[i][1].trim();*/
		String s_gndm="000";
		s_gndm=s_qxcs[0][11].trim();
		String s_qxqt=s_qxcs[0][12].trim();
		String s_qxck=s_qxcs[0][13].trim();
		String s_qxdj=s_qxcs[0][14].trim();
		String s_qxts=s_qxcs[0][15].trim();
		String s_qxsh=s_qxcs[0][16].trim();
		String s_qxdp=s_qxcs[0][17].trim();
		
		
		
		//SessionListener.isAlreadyEnter(session,s_yhdm);
		/*if(SessionListener.isAlreadyEnter(session,s_yhdm))
		{
			return SystemFunction.showLoginError(807, "���˻��Ѿ��ڱ�ĵ����ϵ�½!",
			"");
		}*/
		session.setAttribute("LS.TITLE", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE));
		session.setAttribute("LS.YHDM", s_yhh);
		session.setAttribute("LS.ZS", s_zs);
		session.setAttribute("LS.BRAND", s_ware); 
		session.setAttribute("LS.WARE", s_ware); // ˮ��ר��
		session.setAttribute("LS.YHZ", s_yhzmc);
		session.setAttribute("LS.YHZEG", s_yhz);
		session.setAttribute("LS.XM", s_yhmc);
		session.setAttribute("LS.DWDM", s_dwdm);
		session.setAttribute("LS.LXFS", s_yhdm);
		
		session.setAttribute("LS.BRANDIMAGEURL", brand_imageUrl);
		session.setAttribute("LS.BRANDURL", brand_url);
		//session.setAttribute("LS.BRAND", brand_code);
		session.setAttribute("LS.GNDM", s_gndm);
		session.setAttribute("LS.QXCD", s_gndm);
		session.setAttribute("LS.QXQT", s_qxqt);
		session.setAttribute("LS.QXCK", s_qxck);//s_qxck
		session.setAttribute("LS.QXDJ", s_qxdj);
		session.setAttribute("LS.QXTS", s_qxts);
		session.setAttribute("LS.QXSH", s_qxsh);
		session.setAttribute("LS.QXDP", s_qxdp);
		session.setAttribute("LS.FLAG", "yes");
		session.setAttribute("LS.YEAR", SystemFunction.getYear());
		session.setAttribute("LS.MONTH", SystemFunction.getMonth());
		session.setAttribute("LS.DAY", SystemFunction.getDay());
		session.setAttribute("LS.IP", request.getRemoteAddr());
		session.setAttribute("LS.AGENT", s_agentName);
		session.setAttribute("LS.LASTDAYSOFCURMONTH", DateHelper
				.getLastDayOfCurrentMonth());
		session.setAttribute("LS.FIRSTDAYSOFCURMONTH", DateHelper
				.getFirstDayOfCurrentMonth());
		session.setAttribute("LS.CURDATE", DateHelper.getDate());
		session.setAttribute("LS.CURSHORTDATE", DateHelper.getShortDate1());
		session.setAttribute("LS.URL", "http://" + request.getServerName()
				+ ":" + request.getServerPort() + request.getContextPath());

		session.setAttribute("LS.BGCOLOR", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.SYSTEM_BGCOLOR));
		session.setAttribute("LS.BGIMAGE", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.SYSTEM_BGIMAGE));
		session.setAttribute("LS.SYSTEMBHD", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.BHD));
		session.setAttribute("LS.SYSTEMFHD", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.FHD));
		session.setAttribute("LS.SYSTEMTYD", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.TYD));
		
		session.setAttribute("LS.AUTHAMOUNT", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.AUTHAMOUNT));
		
		session.setAttribute("LS.ZOOM", XmlUtil.readXml(
				BConstants.CONFIG_FILE, BConstants.ZOOM));
		session.setAttribute("LS.CURTIME", DateHelper.getShowDateTime());
		session.setAttribute("LS.DATE", DateHelper.getDateCN());
		session.setAttribute("LS.DAYCN", DateHelper.getDayCN());
		session.setAttribute("NEWSDATEE", SystemFunction.getYear() + "-"
				+ SystemFunction.getMonth() + "-" + SystemFunction.getDay());
		session.setAttribute("NEWSDATE", SystemFunction.getYear() + "-"
				+ SystemFunction.getMonth() + "-" + SystemFunction.getDay());
		ServletContext context = session.getServletContext();
		
		if (!s_yhz.equals("�ն˵���")){
			context.getRequestDispatcher(BConstants.LOGIN_MAIN).forward(request,
					response);
			}else{
				context.getRequestDispatcher(BConstants.LOGIN_MAIN1).forward(request,
						response);
			}
		
		return "";

	}

	// �����û�����Ȩ��
	public String parseDisplayZQ(String s_html, String kjname,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		HttpSession session = request.getSession(true);

		String s_htmlt, s_htmlw, s_htmlc, s_tdt = "", s_tdc = "", s_tdw = "", s_temp = "", s_result = "", s_sfqxkz = "";
		s_htmlt = HtmlFunction.getHtmlHead(s_html, kjname);
		s_htmlc = HtmlFunction.getHtmlContext(s_html, kjname);
		s_htmlw = HtmlFunction.getHtmlEnd(s_html, kjname);

		String s_ywx = (String) session.getAttribute("LS.GNDM");
		if (s_ywx == null)
			s_ywx = "";

		String[][] s_zqcs = new String[0][0];
		int i_mhljs = 1;
		try {
			s_zqcs = XmlUtil.find(BConstants.PAGE_ZQ, "KJM", kjname);
			if (s_zqcs.length == 0)
				return SystemFunction.replace(s_html, "$$" + kjname + ",", "�ؼ�"
						+ kjname + "�Ҳ�����");
			i_mhljs = Integer.valueOf(s_zqcs[0][1].trim()).intValue();
		} catch (SQLException se1) {
			return SystemFunction.showError(807, kjname + "��λSQL����!", se1
					.toString(), request);
		}

		String[][] s_zqmx = new String[0][0];
		try {
			s_zqmx = XmlUtil.find(BConstants.PAGE_ZQ_MX, "KJM", kjname);
			if (s_zqcs.length == 0)
				return SystemFunction.replace(s_html, "$$" + kjname + ",", "�ؼ�"
						+ kjname + "��ϸ�Ҳ�����");
		} catch (SQLException se1) {
			return SystemFunction.showError(808, kjname + "SQL����!", se1
					.toString(), request);
		}
		if (i_mhljs == 1) {
			for (int i = 0; i < s_zqmx.length; i++) {
				s_sfqxkz = s_zqmx[i][5];
				if (s_sfqxkz.trim().length() == 0
						|| !s_sfqxkz.trim().equals("1")
						|| (s_ywx.indexOf(s_zqmx[i][0]) != -1)) {
					if (s_zqmx[i][4].indexOf("@@") != -1)
						s_zqmx[i][4] = HtmlFunction.parseVar(s_zqmx[i][4],
								request, "html");
					s_temp = "<a href=\"" + s_zqmx[i][4] + "\"";
					if (s_zqmx[i][6].trim().length() != 0)
						s_temp += " class=" + s_zqmx[i][6];
					s_temp += ">";
					s_temp += s_zqmx[i][3] + "</a>";
					s_result += SystemFunction.replace(s_htmlc, "$$" + kjname
							+ ",", s_temp)
							+ " \n";
				} else {

					if (s_zqmx[i][7].trim().length() == 0
							|| s_zqmx[i][7].trim().equals("1")) {
						s_temp = "<font  color=\"#CECECE\">" + s_zqmx[i][3]
								+ "</font>";
						s_result += SystemFunction.replace(s_htmlc, "$$"
								+ kjname + ",", s_temp)
								+ "\n";
					} else {
						s_temp = " ";
						s_result += s_temp;
					}

				}
			}
		} else {
			s_tdt = HtmlFunction.gettdhead(s_htmlc, kjname);
			s_tdc = HtmlFunction.gettdcontext(s_htmlc, kjname);
			s_tdw = HtmlFunction.gettdend(s_htmlc, kjname);
			int i = 0;
			String s_tr = "";
			while ((i < s_zqmx.length)) {
				s_tr = "";
				int i_hljs = 0; // ��������
				for (int j = 1; (j <= i_mhljs) && (i < s_zqmx.length); j++, i++) {
					s_sfqxkz = s_zqmx[i][4];
					if (s_sfqxkz.trim().length() == 0
							|| (!s_sfqxkz.trim().equals("1"))
							|| (s_ywx.indexOf(s_zqmx[i][3]) != -1)) {
						if (s_zqmx[i][4].indexOf("@@") != -1)
							s_zqmx[i][4] = HtmlFunction.parseVar(s_zqmx[i][4],
									request, "html");
						s_temp = "<a href=\"" + s_zqmx[i][4] + "\"";
						if (s_zqmx[i][6].trim().length() != 0)
							s_temp += " class=" + s_zqmx[i][6];
						s_temp += ">";
						s_temp += s_zqmx[i][3] + "</a>";
						s_tr += SystemFunction.replace(s_tdc, "$$" + kjname
								+ ",", s_temp)
								+ "\n";
						i_hljs++;
					} else {
						// s_temp = " ";
						s_temp = "<font  color=\"#CECECE\">" + s_zqmx[i][3]
								+ "</font>";

						s_tr += SystemFunction.replace(s_tdc, "$$" + kjname
								+ ",", s_temp)
								+ "\n";
						i_hljs++;
					}
				}
				if (i == s_zqmx.length)
					for (int k = i_hljs; k < i_mhljs; k++) {
						s_temp = "";
						for (int m = 0; m < s_zqmx[0][0].getBytes().length; m++) {
							s_temp += "&nbsp;";
						}
						s_tr += SystemFunction.replace(s_tdc, "$$" + kjname
								+ ",", s_temp);
					}
				s_result += s_tdt + s_tr + s_tdw;
			}
		}
		return s_htmlt + s_result + s_htmlw;
	}

	 /**
	 * �������ͽṹ����ʾ ʵ�ַ���: 1,�������ݿ� ȡ��SQL 2,����SQLȡ������������ֵ 3,����HTML��ֵ 4,����
	 * �ڵ��ʽΪ��Node(id, pid, name, url, title, target, icon, iconOpen)
	 */
	public String parseDisplayTREE( String kjname,
			HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession(true);
        kjname="E"+kjname.substring(1);
        //�õ���SQL
		String treeSql[][] = new String[0][0];
		try {
			treeSql = XmlUtil.find(BConstants.PAGE_TREE, "KJM", kjname);
			if (treeSql == null || treeSql.length == 0)
				//return new JSONArray();
				return SystemFunction.replace("", "$$" + kjname + ",", "û���ҵ��ؼ�"+ kjname + "!");
		} catch (Exception e) {
			return SystemFunction.showError(800, "��λ" + kjname + "SQL����!", e.toString(),request);
		}
		//������SQL,�õ����κ��SQL
		String sql = HtmlFunction.parseVar(treeSql[0][1], request, "sql");
		
		//������SQLȡ��������
		String[][] nodeArr = new String[0][0];
		
		
	    String parentMenuId = request.getParameter("ParentMenuId");
	    if (parentMenuId == null) parentMenuId="1";
		try {
			SQLQuery query = DSManager.getSQLQuery();
			if (treeSql[0][2].equalsIgnoreCase("xml")){
				nodeArr=XmlUtil.xmlFile2Array(sql);
				//nodeArr = XmlUtil.find(sql, "ParentMenuId", parentMenuId);
				
			}else{
				nodeArr = query.ArrayQuery(sql);
			}
			
		} catch (Exception se1) {
			se1.printStackTrace();
			
		}
		
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		
		
		for(int menu=0;menu<nodeArr.length;menu++){
            
			if (nodeArr[menu][7].equals("false")) {//����Ҷ��
            	sb.append("{ id:'"+nodeArr[menu][0]+"',leaf:"+nodeArr[menu][7].trim()+",text:'"+nodeArr[menu][2].trim()+"',cls:'"+nodeArr[menu][5]+"', hrefTarget:'"+nodeArr[menu][4]+"'");
            }else{//��Ҷ��
            	sb.append("{ id:'"+nodeArr[menu][0]+"',leaf:"+nodeArr[menu][7].trim()+",text:'"+nodeArr[menu][2].trim()+"',cls:'"+nodeArr[menu][5]+"', href:'"+nodeArr[menu][3].replaceAll("&", "&amp;")+"', hrefTarget:'"+nodeArr[menu][4]+"'");
            }
			
			//��ѡ��
			if (treeSql[0][3].equals("2"))
			{
			   sb.append(",checked:false");
			}
                
			 sb.append("},");	
                
           
			
             
		}

		String rs=sb.toString();
		
		
		//sb.append("</TreeNode>");
		
		
		 
		//System.out.println(rs.substring(0,rs.length()-1)+"]");
		//return JSONArray.fromObject(rs.substring(0,rs.length()-1)+"]");
		//System.out.println(rs.substring(0,rs.length()-1)+"]");
		return rs.substring(0,rs.length()-1)+"]";

	}
	
	
	/**
	 * 
	 * @param kjname
	 * @param request
	 * @param response
	 * @return
	 */
	public String parseDisplayJSONTREE( String kjname,
			HttpServletRequest request, HttpServletResponse response){

        HttpSession session = request.getSession(true);
        //�õ���SQL
		String treeSql[][] = new String[0][0];
		try {
			treeSql = XmlUtil.find(BConstants.PAGE_TREE, "KJM", kjname);
			if (treeSql == null || treeSql.length == 0)
				//return new JSONArray();
				return SystemFunction.replace("", "$$" + kjname + ",", "û���ҵ��ؼ�"+ kjname + "!");
		} catch (Exception e) {
			return SystemFunction.showError(800, "��λ" + kjname + "SQL����!", e.toString(),request);
		}
		String sql=treeSql[0][1];
		String ljh=treeSql[0][2];
		String cs=treeSql[0][3];
		String chk_sql=treeSql[0][4];
		
       
		
		 //�˵�Ȩ��
		Map m_gndm=new HashMap();
		//ѡ�е�ֵ
		Map m_chkValue=new HashMap();
		
		sql = HtmlFunction.parseVar(treeSql[0][1], request, "");
		
		//������SQLȡ��������
		String[][] nodeArr = new String[0][0];
	    String parentMenuId = request.getParameter("ParentMenuId");
	    if (parentMenuId == null) parentMenuId="1";
	    
		try {
			//��ȡ��������,��Ϊxml:��xml�ļ�,��������ȡ���ݿ�
			SQLQuery query = DSManager.getSQLQuery();
			if (ljh.equalsIgnoreCase("xml")){
				nodeArr=XmlUtil.xmlFile2Array(sql);
				//nodeArr = XmlUtil.find(sql, "ParentMenuId", parentMenuId);
			}else{
				nodeArr = query.ArrayQuery(sql);
			}
			//��ȡѡ�е�ֵ
			if(cs.equals("1"))
			{
				chk_sql = HtmlFunction.parseVar(chk_sql, request, "");
				String chks[][] = new String[0][0];
				chks=query.ArrayQuery(chk_sql);
				String chkValue="";
				if(chks.length >0){
				  chkValue=chks[0][0];
				  String qxs[]=chkValue.split(",");
				
				  for(int i=0;i<qxs.length ;i++)
				  {
					m_chkValue.put(i, qxs[i]);
				  }
				}
			} else if(cs.equals("0")){
				String gndm = (String)session.getAttribute("LS.QXCD");
				if (gndm == null) gndm="";
				String gndms[]=gndm.split(",");
				for(int i=0;i<gndms.length;i++)
				{
					m_gndm.put(i, gndms[i]);
				}
			}
			
		} catch (Exception se1) {
			System.out.println(se1.toString()+sql);
			return SystemFunction.showError(808, kjname + "SQL����!", se1.toString()+ sql,request);
		}
		
		//ϵͳ����Ա
		//String yhdm = (String)session.getAttribute("LS.YHDM");
		//if (yhdm == null) yhdm="";
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		
		
		for(int menu=0;menu<nodeArr.length;menu++){
			//����Ǹ��ڵ㣬ֱ������
			if(nodeArr[menu][0].trim().equals("1")) continue;
             
                
				//��ͨ��
				if(cs.equals("1"))
				{
					String name=nodeArr[menu][2];
					if(nodeArr[menu][8].length()>0)
					{
						name=name+"��"+nodeArr[menu][8]+"��";
					}
					
					if (nodeArr[menu][7].equals("0")) {//����Ҷ��
                    	sb.append("{ id:'"+nodeArr[menu][0]+"', pId:'"+nodeArr[menu][1]+"', name:'"+name+"',icon:'"+nodeArr[menu][5]+"', target:'"+nodeArr[menu][4]+"'");
                    }else{//��Ҷ��
                    	sb.append("{ id:'"+nodeArr[menu][0]+"', pId:'"+nodeArr[menu][1]+"', name:'"+name+"',icon:'"+nodeArr[menu][5]+"', url:'"+nodeArr[menu][3].replaceAll("&", "&amp;")+"', target:'"+nodeArr[menu][4]+"'");
                    }
					 //���ѡ���ˣ�������ѡ������   
	                if(m_chkValue.containsValue("'"+nodeArr[menu][0].trim()+"'"))
	                {
	                	sb.append(",checked:true");
	                }
	                sb.append("},");
				}
				//�˵���
				else if(cs.equals("0")){
					if(m_gndm.containsValue("'"+nodeArr[menu][0].trim()+"'") || nodeArr[menu][6].equals("0")){
						if (nodeArr[menu][7].equals("0")) {//����Ҷ��
	                    	sb.append("{ id:'"+nodeArr[menu][0]+"', pId:'"+nodeArr[menu][1]+"', name:'"+nodeArr[menu][2]+"',icon:'"+nodeArr[menu][5]+"', target:'"+nodeArr[menu][4]+"'");
	                    }else{//��Ҷ��
	                    	sb.append("{ id:'"+nodeArr[menu][0]+"', pId:'"+nodeArr[menu][1]+"', name:'"+nodeArr[menu][2]+"',icon:'"+nodeArr[menu][5]+"', url:'"+nodeArr[menu][3].replaceAll("&", "&amp;")+"', target:'"+nodeArr[menu][4]+"'");
	                    } 
						 sb.append("},");   
	                }
				}
               
                
               
                
           
		}
		String rs=sb.toString();
		
		//System.out.println(rs.substring(0,rs.length()-1)+"]");
		return rs.substring(0,rs.length()-1)+"]";

	}

	/**
	 * �������������ʾ ʵ�ַ���: 1,�������ݿ� ȡ��SQL 2,����SQLȡ������������ֵ 3,����HTML��ֵ 4,����
	 */

	public String parseDisplayXL(String html, String s_kjname,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String s_marker = "<option>$$" + s_kjname + ",</option>";

		// �������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_XL, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0)
				return SystemFunction.replace(html, "$$" + s_kjname + ",",
						"û���ҵ��ؼ�" + s_kjname + "!");
		} catch (Exception e) {
			return SystemFunction.showError(800, "��λ" + s_kjname + "SQL����!", e
					.toString(), request);
		}

		String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");

		// ȡ������������ֵ
		String result[][] = new String[0][0];
		try {
			result = query.ArrayQuery(sql);
		} catch (Exception e) {
			return SystemFunction.showError(801, s_kjname + "SQL����", e
					.toString()
					+ "\n" + sql, request);
		}

		// ����HTML��ֵ

		String s_selectedvalue = request
				.getParameter(y_sql[0][1].toUpperCase());

		if (s_selectedvalue == null || s_selectedvalue.length() == 0) {
			HttpSession session = request.getSession();
			s_selectedvalue = (String) session.getAttribute("LS."
					+ y_sql[0][1].toUpperCase());
		}
		String s_option = "", s_value = "";
		for (int row = 0; row < result.length; row++) {
			s_option += "<option value='";
			s_value = result[row][0];
			s_option += s_value + "'";

			if (s_selectedvalue != null
					&& s_selectedvalue.trim().equals(result[row][0])) {
				s_option += " selected";
			}
			s_option += ">" + result[row][1] + "</option>\n";

		}

		return SystemFunction.replace(html, s_marker, s_option);
	}

	public JSONArray parseDisplayXLJSON(String s_kjname,
			HttpServletRequest request) throws Exception {

		List result = new ArrayList();

		// �������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_XL, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0) {
				return JSONArray.fromObject(result);
			}

		} catch (Exception e) {
			return JSONArray.fromObject(result);
		}

		String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");

		// ȡ������������ֵ

		try {
			result = query.ListQuery(sql);
		} catch (Exception e) {
			System.out.print("XL����"+sql);
			return JSONArray.fromObject(result);
		}

		return JSONArray.fromObject(result);
	}

	public JSONObject parseDisplayMJJSON(String s_kjname,
			HttpServletRequest request) throws Exception {
        List allList=null;
		List fy_result =null;
		//�������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("�ؼ�����"+s_kjname +" �ؼ�û�ҵ���");
			}
				
		} catch (Exception e) {
			throw new Exception("�ҿؼ�ʱ����"+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //�������Ӻ�
		String cs = y_sql[0][2].trim(); //���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); //ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); //ҵ���õ�SQL2,�������������SQL
		String xsfs = y_sql[0][5].trim(); //���ҳ������ʾʱ��1:ȫ��ʾ 2:ֻ��ʾ���� 3:ֻ��ʾ����
		String sfxsym = y_sql[0][6].trim(); //�Ƿ���ʾҳ��,1:��ʾ 0:����ʾ
		String myhs = y_sql[0][7].trim(); //ÿҳ����
		String defaults = y_sql[0][8].trim(); //��ֵΪNULLʱȱʡֵ
		String msjts = y_sql[0][10].trim(); //û���ҵ�����ʱ��ʾ
		
//		SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request,s_kjname);
		//SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVarEasyui(sql, request, "");

		//String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");
        String start=request.getParameter("start");
        if (start == null) start="";
        String limit=request.getParameter("limit");
        if (limit == null) start="";
		//ȡ������������ֵ
		String all_result[][] = new String[0][0];
		int pageid = 0,pagenum=0,i_rownum = 0; //����ڼ�ҳ����ҳ�����ܼ�¼��
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
	    	  pagenum = pageid+i_myhs;
	    	  if (pagenum>i_rownum) pagenum=i_rownum;
	    	  for(int i=pageid;i<pagenum;i++)
	    	  {
	    		  fy_result.add(allList.get(i));  
	    	  }
	          
	       }
              
		
	    }catch(Exception ex)
	    {
	    	//ex.printStackTrace();
	    	throw new Exception("�ؼ�����"+s_kjname +" ��ѯʱ����SQL="+sql+"\n\n"+ex.toString());
	    }
			
	    JSONObject json_result = new JSONObject();
		json_result.put("total", Integer.valueOf(i_rownum));
		if(fy_result == null){
			JSONArray json_arr = JSONArray.fromObject(allList);
			json_result.put("root", json_arr);
		}else{
			JSONArray json_arr = JSONArray.fromObject(fy_result);
			json_result.put("root", json_arr);
		}
		
		

		
		return json_result;
	}
	
	
	public JSONObject parseExcelJSON(String s_kjname,
			HttpServletRequest request) throws Exception {
        List allList= new ArrayList();
		
        
		
		String filename=request.getParameter("d_SPBM");
		if(filename == null) filename="";
		if(filename.equals(""))
		{
			throw new Exception("�ļ����ƴ���"+filename );
		}
		
		HSSFWorkbook workbook = null;
		try{
			workbook =new HSSFWorkbook(new FileInputStream(new File(filename)));// ���������� 
		}catch(Exception ex1)
		{
			throw new Exception("Excel�ļ���ʽ��������Excel97-2003���������棡"+ex1.toString() );
		}
		 
        HSSFSheet sheet = workbook.getSheetAt(0);// �õ�������  
        
        HSSFRow titleRow = sheet.getRow(0);
   	    Map titleKey = new HashMap();
   	 for(int col=0;col<titleRow.getLastCellNum();col++){
   		 HSSFCell titleCol = titleRow.getCell(col);
   	     String key=titleCol.getRichStringCellValue().toString();
   	     titleKey.put(col, key);
   	    // titleKey.add(key);
   	 }
   	
   	
   	//System.out.println("rows:"+sheet.getLastRowNum()+1);
       for(int j=1;j<sheet.getLastRowNum()+1;j++) {
    	  
        	 HashMap m_row = new HashMap();
        	//����һ���ж���
        	HSSFRow row = sheet.getRow(j);
        	if(row == null){
        		 m_row.put(titleKey.get(0).toString(),"��"+j+"�е�����Ϊ�գ���������");
        		 continue;
        		 //return m_row;
        		//throw new Exception("��"+j+"�е�����Ϊ�գ���������");
        		
        	}
        	
        		 
        	
        	//��һ�����ÿһ���ֶα�������
        	for(int i=0;i<titleKey.size();i++) {
        		
        	    //����һ�������һ���ֶεĶ���Ҳ���ǻ�ȡ����һ����Ԫ���е�ֵ
        		HSSFCell cell=null;
        		try{
        	     cell = row.getCell(i);
        	     if(cell == null){
        	    	 //m_row.put(titleKey.get(0).toString(),"��"+j+"��"+"��"+i+"�е�����Ϊ�գ���������");
        	    	 m_row.put(titleKey.get(0).toString(),"");
        	    	 continue;
        	     }
        	     Object inputValue = null;// ��Ԫ��ֵ  
        	     if(cell.getCellType() == 0) {  
        	         long longVal = Math.round(cell.getNumericCellValue());
        	         double doubleVal = cell.getNumericCellValue();
        	         if(Double.parseDouble(longVal + ".0") == doubleVal)  
        	             inputValue = longVal;  
        	         else  
        	             inputValue = doubleVal;  
        	     } else if (cell.getCellType() == 1)
        	     {
        	    	 inputValue=cell.getRichStringCellValue().toString();
        	     }else{
        	    	 inputValue = cell.getStringCellValue();
        	     }
        	     
                   //System.out.println(titleKey.get(i).toString());
        	     
        	    	 m_row.put(titleKey.get(i).toString(),inputValue);

        	  
        		}catch(IllegalStateException iex){
        			if(cell!=null){
        		          cell.setCellType(Cell.CELL_TYPE_STRING);
        		          m_row.put(sheet.getRow(0).getCell(i).getRichStringCellValue().toString(),cell.getStringCellValue());
        		         
        		     }
        		}
        		
        		catch(Exception ex)
        		{
        			ex.printStackTrace();
        			 m_row.put(titleKey.get(i).toString(),"��"+j+"�У���"+(i+1)+"�����ݸ�ʽ������������");
        			//throw new Exception("��"+j+"�У���"+i+"�����ݸ�ʽ������������");
        			
        		}
        	   
        	   
        	}
        	 if(j>0) //��ͷ��Ҫ��ӵ�arraylist��
    	     {
        		 allList.add(m_row);
    	     }
    	       
        	
        }
        
        
        
        
	
			
	    JSONObject json_result = new JSONObject();
		json_result.put("total", Integer.valueOf(allList.size()));
		
			JSONArray json_arr = JSONArray.fromObject(allList);
			json_result.put("root", json_arr);
		
		
		

		
		return json_result;
	}
	
	
	public JSONObject parseDisplayWXPay(String s_kjname,
			HttpServletRequest request) throws Exception {
        List allList=null;
		List fy_result =null;
		//�������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {
			
			 y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0){
				throw new Exception("�ؼ�����"+s_kjname +" �ؼ�û�ҵ���");
			}
				
		} catch (Exception e) {
			throw new Exception("�ҿؼ�ʱ����"+e.toString() );
		}
		
		String ljh = y_sql[0][1].trim(); //�������Ӻ�
		String cs = y_sql[0][2].trim(); //���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); //ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); //ҵ���õ�SQL2,�������������SQL
		String xsfs = y_sql[0][5].trim(); //���ҳ������ʾʱ��1:ȫ��ʾ 2:ֻ��ʾ���� 3:ֻ��ʾ����
		String sfxsym = y_sql[0][6].trim(); //�Ƿ���ʾҳ��,1:��ʾ 0:����ʾ
		String myhs = y_sql[0][7].trim(); //ÿҳ����
		String defaults = y_sql[0][8].trim(); //��ֵΪNULLʱȱʡֵ
		String msjts = y_sql[0][10].trim(); //û���ҵ�����ʱ��ʾ
		
//		SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request,s_kjname);
		//SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVarEasyui(sql, request, "");

		//String sql = HtmlFunction.parseVar(y_sql[0][3], request, "sql");
        String start=request.getParameter("start");
        if (start == null) start="";
        String limit=request.getParameter("limit");
        if (limit == null) start="";
		//ȡ������������ֵ
		String all_result[][] = new String[0][0];
		int pageid = 0,pagenum=0,i_rownum = 0; //����ڼ�ҳ����ҳ�����ܼ�¼��
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
	    	  pagenum = pageid+i_myhs;
	    	  if (pagenum>i_rownum) pagenum=i_rownum;
	    	  for(int i=pageid;i<pagenum;i++)
	    	  {
	    		  fy_result.add(allList.get(i));  
	    	  }
	          
	       }
              
		
	    }catch(Exception ex)
	    {
	    	throw new Exception("�ؼ�����"+s_kjname +" ��ѯʱ����SQL="+sql+"\n\n"+ex.toString());
	    }
			
	    JSONObject json_result = new JSONObject();
		json_result.put("total", Integer.valueOf(i_rownum));
		if(fy_result == null){
			JSONArray json_arr = JSONArray.fromObject(allList);
			json_result.put("root", json_arr);
		}else{
			JSONArray json_arr = JSONArray.fromObject(fy_result);
			json_result.put("root", json_arr);
		}
		
		

		
		return json_result;
	}



    
    /**
	 * �ϴ��ļ�
	 * 
	 * @param request
	 * @param response
	 * @param config
	 * @throws Exception
	 */
	public String parseUpLoadFile(String html, String kj,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		SQLUpdater updater = DSManager.getSQLUpdater();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_UE, "KJM", kj);
			if (y_sql == null || y_sql.length == 0) {
				throw new Exception("û���ҵ��ؼ�" + kj + "!");
			}

		} catch (Exception e) {
			throw new Exception("��λ" + kj + "SQL����!" + e.toString());
		}
        String cs=  y_sql[0][1];
		String path = y_sql[0][2];
		String ue_sql = y_sql[0][3];
		String filetype = y_sql[0][4];
		String Compress_w = y_sql[0][5];
		String Compress_h = y_sql[0][6];
		
		
		
		

		ServletFileUpload upload = null;
		DiskFileItemFactory factory = new DiskFileItemFactory();
		upload = new ServletFileUpload(factory);
		
		upload.setFileSizeMax(-1);

		// ���·��
		String url = SystemFunction.class.getResource("/").getPath();
		url = url.substring(1, url.length() - 16);
		String filename="";
		
		if(cs == null) cs="";
		
		//����ͼƬǰ�Ĺ���
		if (cs.length() > 0) {
			if (cs.startsWith("delete") || cs.startsWith("update") || cs.startsWith("insert") || cs.startsWith("begin")){
				String sql_cs = HtmlFunction.parseVarAttr(cs, request, "");

				try {
					updater.executeUpdate(sql_cs);
					//System.out.println("sql=" + sql_cs);
				} catch (Exception ex) {
					System.out.println("sql=" + sql_cs);
					ex.printStackTrace();
				}
			}
			
		}
		
		String s_url= "http://" + request.getServerName();
		if(request.getServerPort() == 443){
			s_url = "https://" + request.getServerName() + request.getContextPath();
		}else if(request.getServerPort() == 80){
			s_url=s_url+ request.getContextPath();
		}else 
			s_url=s_url+ ":" + request.getServerPort() + request.getContextPath();
			
				
		//System.out.println("s_url:"+s_url);
		
		List items =upload.parseRequest(request);
		
		
		for (int i = 0; i < items.size(); i++) {
           //System.out.println(i+"--"+System.currentTimeMillis());
			FileItem fi = (FileItem) items.get(i);

			// ����ͼƬ��
			if (!fi.isFormField()) {
				File newFile = null;
				//System.out.print("�ļ����ƣ�"+fi.getName().toString());
				
				String[] arr = fi.getName().split("\\.");
				 String houz = arr[arr.length-1];
				// String houz = fi.getName().split("\\.")[1];
				String s_filecode = java.util.UUID.randomUUID().toString();
				
				s_filecode = s_filecode.replace("-", "");
				String s_filename=s_filecode+"."+houz;
				 filename = path + s_filename;

				if (fi.getName() != null && fi.getSize() != 0) {

					File fp = new File(url + path);
					// ����Ŀ¼
					if (!fp.exists()) {
						fp.mkdirs();// Ŀ¼�����ڵ�����£�����Ŀ¼��
					}
					newFile = new File(url + filename);

					if (!newFile.exists()) {
						newFile.createNewFile();
					}

					fi.write(newFile);
					
					if(kj.equals("UE0028")){
						Map params= new HashMap();
						HttpClientUtil hcu = new HttpClientUtil();
						
						
						params.put("username", "admin");
						params.put("password", "4297f44b13955235245b2497399d7a93");
						String login=hcu.doLoginPost("http://ad-kcc.dderp.cn:10080/login",params);
						
						//File file=new File("C:\\Users\\Administrator\\Desktop\\video\\3ccef36a08ee31fd178f633929dd66ca.mp4");
						
						com.alibaba.fastjson.JSONObject video_id=hcu.doUploadFilePost("http://ad-kcc.dderp.cn:10080/vod/upload", params,newFile );
						
						if(video_id.equals("")){
							throw new Exception("�޷��ϴ�����ý���������");
							
						}
						filename="http://ad-kcc.dderp.cn:10080"+video_id.getString("videoUrl");
						String fm="http://ad-kcc.dderp.cn:10080"+video_id.getString("snapUrl");
						//Video v=new Video();
						//com.alibaba.fastjson.JSONObject video=v.getVideoObject(video_id);
						//����ͼ
						//request.setAttribute("FileShortFullName", "http://ad-kcc.dderp.cn:10080"+video.get("snapUrl"));
						request.setAttribute("FileShortFullName", fm);
						request.setAttribute("video_id", video_id.getString("id"));
						
					}

				} else {
					filename = "";
				}
				request.setAttribute("FileCode", s_filecode);
				request.setAttribute("FileName", s_filename);
				request.setAttribute("ExeName", houz);
				if(!kj.equals("UE0028"))
				  request.setAttribute("FileFullName", s_url+"/"+filename);
				else
					request.setAttribute("FileFullName", filename);
				
			
				if(!houz.equals("mp4")){
				// ȡ����
				BufferedImage bi = null;
				try {
					bi = ImageIO.read(newFile);
					int width = bi.getWidth(); // ����
					int height = bi.getHeight(); // ����
					request.setAttribute("Pix_W", String.valueOf(width));
					request.setAttribute("Pix_H", String.valueOf(height));
				} catch (Exception e) {
					
					e.printStackTrace();
					
				}
				}

				if (filetype.equals("ͼ��") && !(houz.equals("mp4"))) {
					
					

					if (Compress_w.length() > 0) {
						String smallPath = url + path + "small\\";

						File fp = new File(smallPath);

						if (!fp.exists()) {
							fp.mkdirs();
						}
						/*ImageCompress.imageCompress(newFile, smallPath,
								s_filename, 0.5f, 1.0f,
								Integer.parseInt(Compress_w),
								Integer.parseInt(Compress_h));*/
						
						ImageCompress.reduceImg(newFile,smallPath+s_filename,Integer.parseInt(Compress_w),
								Integer.parseInt(Compress_h));
						request.setAttribute("FileShortFullName", s_url+"/"+path +"small/"+ s_filename);
					}

				}else{
					//���÷���,����video��ý����������˴�����Ҫ
					/*String smallPath = url + path + "small\\";
					PicVideoUtils.handler("D:\\ffmpeg\\bin\\ffmpeg.exe", url+filename, smallPath+s_filecode+".png");
					request.setAttribute("FileShortFullName", s_url+"/"+path +"small/"+ s_filecode+".png");*/
				}
				
				
				if (ue_sql.length() > 0) {
					String sql = HtmlFunction.parseVarAttr(ue_sql, request, "");

					try {
						updater.executeUpdate(sql);
						//System.out.println("sql=" + sql);
					} catch (Exception ex) {
						System.out.println("sql=" + sql);
						ex.printStackTrace();
					}
				}
			} else // �����ֶα�
			{
				String name = fi.getFieldName();
				String value = fi.getString();
				request.setAttribute(name, value);
			}
			
			

		}

		

		return filename;

	}
	
	
	public String parseDownloadFile(String html, String kj,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		
		String url_strings=request.getParameter("url_paths");
		if(url_strings == null) url_strings="";
		
		String [] url_paths=url_strings.split(",");
		
		JSONObject json_result = new JSONObject();

		SQLUpdater updater = DSManager.getSQLUpdater();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_UE, "KJM", kj);
			if (y_sql == null || y_sql.length == 0) {
				json_result.put("result", "fasle");
				throw new Exception("û���ҵ��ؼ�" + kj + "!");
			}

		} catch (Exception e) {
			json_result.put("result", "fasle");
			throw new Exception("��λ" + kj + "SQL����!" + e.toString());
		}

		String path = y_sql[0][2];
		String ue_sql = y_sql[0][3];
		String filetype = y_sql[0][4];
		String Compress_w = y_sql[0][5];
		String Compress_h = y_sql[0][6];

	
		// ���·��
		String url = SystemFunction.class.getResource("/").getPath();
		url = url.substring(1, url.length() - 16);
		
		for(int i=0;i<url_paths.length;i++){
			String url_string = url_paths[i];
			String filename="";
			URL url_str = new URL(url_string);
			DataInputStream dataInputStream = new DataInputStream(url_str.openStream());
			File newFile = null;
			String  s_filename = url_string.substring(url_string.lastIndexOf("/") + 1);
			 filename = path + s_filename;
			 
			 File fp = new File(url + path);
				// ����Ŀ¼
				if (!fp.exists()) {
					fp.mkdirs();// Ŀ¼�����ڵ�����£�����Ŀ¼��
				}
				newFile = new File(url + filename);

				if (!newFile.exists()) {
					newFile.createNewFile();
				}
				
				 FileOutputStream fileOutputStream = new FileOutputStream(newFile);
		            ByteArrayOutputStream output = new ByteArrayOutputStream();

		            byte[] buffer = new byte[1024];
		            int length;

		            while ((length = dataInputStream.read(buffer)) > 0) {
		                output.write(buffer, 0, length);
		            }
		            byte[] context=output.toByteArray();
		            fileOutputStream.write(output.toByteArray());
		            dataInputStream.close();
		            fileOutputStream.close();
		            
		            if (filetype.equals("ͼ��")) {
						// ȡ����
						BufferedImage bi = null;
						try {
							bi = ImageIO.read(newFile);
						
							int width = bi.getWidth(); // ����
							int height = bi.getHeight(); // ����
							request.setAttribute("Pix_W", String.valueOf(width));
							request.setAttribute("Pix_H", String.valueOf(height));
						} catch (Exception e) {
							e.printStackTrace();
						}

						if (Compress_w.length() > 0) {
							String smallPath = url + path + "small\\";

							File fp1 = new File(smallPath);

							if (!fp1.exists()) {
								fp1.mkdirs();
							}
							ImageCompress.imageCompress(newFile, smallPath,
									s_filename, 0.5f, 1.0f,
									Integer.parseInt(Compress_w),
									Integer.parseInt(Compress_h));
						}

					}
			 
		}
		
		
		
				
				//System.out.print(fi.getName().toString());
				// String houz = url_string.split("\\.")[1];
				//String s_filename = java.util.UUID.randomUUID().toString()
						//+ "."+houz;
				// ��·���л�ȡ
		       
		          
		      
				//s_filename = s_filename.replace("-", "");
				

				//if (fi.getName() != null && fi.getSize() != 0) {

					
					
					
					

					//fi.write(newFile);

				
			//	request.setAttribute("FileName", s_filename);
				//request.setAttribute("FileFullName", filename);

				
			

		

		if (ue_sql.length() > 0) {
			String sql = HtmlFunction.parseVarAttr(ue_sql, request, "");

			try {
				if (sql.indexOf("call")==-1)
				   updater.executeUpdate(sql);
				else
					updater.executeCall(sql);
			} catch (Exception ex) {
				System.out.println("sql=" + sql);
				ex.printStackTrace();
			}
		}
		
		json_result.put("result", "ok");
		
		

		return json_result.toString();

	}
   
	
	public static synchronized String parseModifyIN(String kjname,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String tmpKj = kjname;
		
		kjname = kjname.substring(0, 6);// ����ؼ������ؼ���Ϊ6λ
		String s_action = request.getParameter("action");// �ж���ɾ�����ǲ���;
		SQLUpdater updater = DSManager.getSQLUpdater();
		SQLQuery query = DSManager.getSQLQuery();

		if (s_action == null || s_action.trim().length() == 0)
			s_action = "insert";
		// ȡҵ������
		String[][] s_modifycs = new String[0][0];
		try {
			s_modifycs = XmlUtil.find(BConstants.PAGE_UP, "KJM", kjname);
			if (s_modifycs.length == 0)
				return "Ajax:��������[" + kjname + "]�����ڣ�����δ�ɹ���";
		} catch (SQLException e) {
			return "Ajax:��������[" + kjname + "]��λSQL����!";
		}
		String s_selectsql = s_modifycs[0][2], s_insertsql = s_modifycs[0][3], s_deletesql = s_modifycs[0][5], s_updatesql = s_modifycs[0][4], s_cs = s_modifycs[0][1], s_url = s_modifycs[0][6];
		
		
		
		
		if (s_action.equals("delete")) {
			s_deletesql = HtmlFunction.parseVar(s_deletesql, request, "");

			try {
				updater.executeUpdate(s_deletesql);
			} catch (SQLException e) {
				return "Ajax:��������[" + kjname + "]ɾ��SQL����!SQL=" + s_deletesql;
			}

		}
		if (s_action.equals("insert")) {

			
			// �ж��Ƿ���Ҫ���,'1'Ϊ��
			if (s_cs.equals("1")) {
				s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "");
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "");
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					return "Ajax:��������[" + kjname + "]���ʱ����!SQL=" + s_selectsql;
				}
				if (checkValue.length != 0)
					return "��¼[" + checkValue[0][0] + "]�Ѵ��ڣ�����δ�ɹ�!";
				try {
					updater.executeUpdate(s_insertsql);
				} catch (SQLException e) {
					System.out.println(s_insertsql);
					return "Ajax:��������[" + kjname + "]����SQL����!SQL="
							+ s_insertsql;
				}
			}
			//// �����⵽��־Ϊ2,���м�¼�͸��¼�¼��������ʾ���޼�¼��ֱ�Ӳ���
			else if (s_cs.equals("2")) {
				s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "");
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "");
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					return "Ajax:��������[" + kjname + "]���ʱ����!SQL=" + s_selectsql;
				}
				if (checkValue.length != 0) {
					s_updatesql = HtmlFunction.parseVar(s_updatesql, request,
							"");
					updater.executeUpdate(s_updatesql);
				} else {
					updater.executeUpdate(s_insertsql);
				}

			}
			else if (s_cs.equals("3")) {
				String[][] checkValue = new String[0][0];
				s_selectsql = HtmlFunction.parseVar(s_selectsql, request, "");
				try {
					checkValue = query.ArrayQuery(s_selectsql);
				} catch (Exception e) {
					return "Ajax:��������[" + kjname + "]���ʱ����!SQL=" + s_selectsql;
				}
				if (checkValue.length == 0) {

					return "�ö������޴�������Ĳ�Ʒ!";
				}

			}
			//����Json������϶�����
			else if(s_cs.equals("4"))
			{
				String jsonData = (String)request.getParameter("d_JsonData");
				
				if(jsonData == null) jsonData = "";
				if(!jsonData.equals(""))
				{
					try{
						//dsn.beginTransaction();
						
						jsonData=java.net.URLDecoder.decode(jsonData,"utf-8");
						s_deletesql = HtmlFunction.parseVar(s_deletesql, request, "");
						updater.executeUpdate(s_deletesql);//ɾ����ϸ����
						List jsonList = JsonUtil.getList4Json(jsonData, Map.class);
						//ѭ��������ϸ����
						for(int i=0;i<jsonList.size();i++){
							Map map=(Map)jsonList.get(i);
							map.put("xh", i);//��Ŵ���
							
							String updatesql=HtmlFunction.parseVar(s_updatesql, request,"",map);
							//System.out.println(updatesql);
							try{
							updater.executeUpdate(updatesql);
							}catch(Exception ex){
								//System.out.println(updatesql);
								return "Ajax:��������[" + kjname + "]����SQL����!SQL="
								+ updatesql;
							}
							
						}
						s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "");
						updater.executeUpdate(s_insertsql);//���붩������
						//dsn.endTransaction();
					}catch(Exception e){
						//dsn.rollback();
						System.out.println(s_insertsql);
						return "Ajax:��������[" + kjname + "]����SQL����!SQL="
						+ s_insertsql;
						
						
					}
				}
				
			}
			//����Json���ݱ�
			else if(s_cs.equals("5"))
			{
                //�����������  
				//request.setCharacterEncoding("utf-8");  


				// ��ȡ�༭���� �����ȡ������json�ַ���
				String deleted = request.getParameter("deleted");
				String inserted = request.getParameter("inserted");
				String updated = request.getParameter("updated");
				
				if (deleted != null) {
					// ��json�ַ���ת���ɶ���
					deleted=java.net.URLDecoder.decode(deleted,"utf-8");
					List listDeleted = JsonUtil.getList4Json(deleted, Map.class);

					for(int i=0;i<listDeleted.size();i++){
						Map map=(Map)listDeleted.get(i);
						//map.put("xh", i);//��Ŵ���
						String deletesql=HtmlFunction.parseVar(s_deletesql, request,"", map);
						try {
							updater.executeUpdate(deletesql);
						} catch (SQLException e) {
							return "Json:��������[" + kjname + "]ɾ��SQL����!SQL="
									+ deletesql;
						}
						
						
					}
					
				}
				if (inserted != null) {
					// ��json�ַ���ת���ɶ���
					inserted=java.net.URLDecoder.decode(inserted,"utf-8");
					List listInserted = JsonUtil.getList4Json(inserted, Map.class);
					for(int i=0;i<listInserted.size();i++){
						Map map=(Map)listInserted.get(i);
						//map.put("xh", i);//��Ŵ���
						String insertsql=HtmlFunction.parseVar(s_insertsql, request,"", map);
						try {
							updater.executeUpdate(insertsql);
						} catch (SQLException e) {
							return "Json:��������[" + kjname + "]����SQL����!SQL="
									+ insertsql;
						}
						
					}
				}
				if (updated != null) {
					// ��json�ַ���ת���ɶ���
					updated=java.net.URLDecoder.decode(updated,"utf-8");
					List listUpdated = JsonUtil.getList4Json(updated, Map.class);
					for(int i=0;i<listUpdated.size();i++){
						Map map=(Map)listUpdated.get(i);
						//map.put("xh", i);//��Ŵ���
						String updatesql=HtmlFunction.parseVar(s_updatesql, request,"", map);
						try {
							updater.executeUpdate(updatesql);
						} catch (SQLException e) {
							return "Json:��������[" + kjname + "]����SQL����!SQL="
									+ updatesql;
						}
						
						
					}
				}
			}
			//ֱ�����������
			else {
				try {
					s_insertsql = HtmlFunction.parseVar(s_insertsql, request, "");
					updater.executeUpdate(s_insertsql);
				} catch (SQLException e) {
					System.out.println(s_insertsql);
					return "Ajax:��������[" + kjname + "]����SQL����!SQL="
							+ s_insertsql;
				}
			}

		}
		if (s_action.equals("update")) {
			s_updatesql = HtmlFunction.parseVar(s_updatesql, request, "");

			try {
				updater.executeUpdate(s_updatesql);
			} catch (SQLException e) {
				return "Ajax:��������[" + kjname + "]����SQL����!SQL=" + s_updatesql;
			}

		}

		if (s_action.equals("call")) {
			s_url = HtmlFunction.parseVar(s_url, request, "");

			try {

				updater.executeCall(s_url);
			} catch (SQLException e) {
				return "Ajax:��������[" + kjname + "]ִ�д�̹��̳���!URL=" + s_url
						+ e.toString();
			}

		}

		return "ok";
	}

	public String parseDisplayEX(String s_html, String kjname,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		String s_t_url = "downLoad?proname=";
		String s_urlstring;
		String s_url = "";// + request.getParameter("proname");
		HttpSession session=request.getSession(true);
		boolean b = request.getMethod().equals("POST");
		String s_queryString = request.getQueryString() == null ? "" : request
				.getQueryString();
		s_queryString = SystemFunction.replace(s_queryString, "%20", " ");
		s_queryString = SystemFunction.converGB(s_queryString);// ����ת��

		Enumeration em_params = request.getParameterNames();
		String s_param, s_value;
		while (em_params.hasMoreElements()) {
			s_param = (String) em_params.nextElement();
			if (!b) {
				s_value = HtmlFunction.getParam(s_queryString, s_param);
			} else {
				s_value = request.getParameter(s_param);
			}

			if (s_value != null && s_value.trim().length() != 0
					&& !s_param.equals("proname")) {
				s_url += "&" + s_param + "=" + s_value;
			}
		}

		s_url += "&inexcel=1";
		s_urlstring = "<input type=button name=exportE id=exportE value=������EXCEL onclick=\"window.location.href='"
				+ s_t_url + kjname + s_url + "'\">"
				+"\r\n <script language=\"javascript\">"
				+ "<!-- \r\n"
				+ "function go() \r\n"
				+ "{  var dj=\""+SystemFunction.null2SpaceString((String)session.getAttribute("LS.QXSH"))+"\";"
               // + "  if (dj.indexOf('A04')== -1){document.getElementById('exportE').disabled = true;}"
				+ "} \n"
				+ "go()"
				+ "//--> \n"
				+ "</script> \n";
		// + "<a href=\"javascript:window.location.href='"
		// + s_t_url+kjname+s_url
		// + "';\">��ת��EXCEL��</a>";
		if (request.getParameter("inexcel") == null
				|| !request.getParameter("inexcel").equals("1")) {
			s_html = SystemFunction.replace(s_html, "$$" + kjname + ",",
					s_urlstring);
			// ����excel������ʾҳ��ʱ��Ԫ����ʱ����ʾ�����⽫ҳ���滻����������style
			s_html = SystemFunction
					.replace(
							s_html,
							" class=\"tablestyle\"",
							" borderColor=\"#ffffff\" borderColorDark=\"#ffffff\" borderColorLight=\"#006600\" ");
			s_html = SystemFunction.replace(s_html, " class=\"tdstyle1\"", "");
		} /*
			 * else { //��Excel����ʾ
			 * response.setContentType("application/vnd.ms-excel;charset=gb2312");
			 * s_html = SystemFunction.replace(s_html, "content=\"text/html;
			 * charset=gb2312\"",
			 * "content=\"application/vnd.ms-excel;charset=gb2312\""); s_html =
			 * SystemFunction.replace(s_html, "$$" + kjname + ",", ""); }
			 */
		return s_html;

	}

	public String parseDisplayAP(String s_html, String s_kjname,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {

		//String s_marker = "<option>$$" + s_kjname + ",</option>";

		// �������ݿ�
		SQLQuery query = DSManager.getSQLQuery();
		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_AP, "KJM", s_kjname);
			if (y_sql == null || y_sql.length == 0)
				return SystemFunction.replace(s_html, "$$" + s_kjname + ",",
						"û���ҵ��ؼ�" + s_kjname + "!");
		} catch (Exception e) {
			return SystemFunction.showError(800, "��λ" + s_kjname + "SQL����!", e
					.toString(), request);
		}

		String sql = HtmlFunction.parseVar(y_sql[0][4], request, "sql");
        String chart_type = y_sql[0][3];
        String chart_title = y_sql[0][6];
        String t0 = y_sql[0][8];
        String t1 = y_sql[0][9];
        String t2 = y_sql[0][10];
        
        String width = y_sql[0][17];
        String height = y_sql[0][18];
		// ȡ������������ֵ
		String result[][] = new String[0][0];
		try {
			result = query.ArrayQuery(sql);
		} catch (Exception e) {
			return SystemFunction.showError(801, s_kjname + "SQL����", e
					.toString()
					+ "\n" + sql, request);
		}
		
		String filename="";
		if(chart_type.equalsIgnoreCase("Pie")){
			filename=ChartUtils.getPie(result, chart_title, request);
		}
		else if(chart_type.equalsIgnoreCase("Bar")){
			String meta[]={t0,t1,t2};
			filename=ChartUtils.getBar(result, chart_title, request,meta,width,height);
		}

		// ����HTML��ֵ



		return SystemFunction.replace(s_html, "$$"+s_kjname+",", filename);
	

	}
	
	/**
	 * �������ݿ�Ψһ��ֵ
	 * 
	 */
	public String parseGenSeq(String s_html, String kjname) throws Exception {
 
		SQLQuery query;
		String seqId[][];
		try {
			query = DSManager.getSQLQuery();
			seqId = query.ArrayQuery("select abs(checksum(newid())) SeqId");
		} catch (SQLException e) {
			throw new Exception("����" + kjname + "����!\n" + e.toString());

		}

		String seqIdValue = seqId[0][0];

		s_html = SystemFunction
				.replace(s_html, "$$" + kjname + ",", seqIdValue);

		return s_html;

	}

	public void downLoadTxt(HttpServletRequest request,
			HttpServletResponse response, ServletConfig config)
			throws Exception {

		String path = XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT)
				+ "\\basicdata.txt";
		SQLQuery query;
		String data[][];
		query = DSManager.getSQLQuery();
		data = query
				.ArrayQuery("select  Product_txm, product_code from product_list where len(product_txm) >0   order by Product_txm");
		int colLen[] = { 30, 20 };
		SystemFunction.data2TxtFile(data, path, colLen, " ");
		// �½�һ��SmartUpload����
		SmartUpload su = new SmartUpload();
		// ��ʼ��

		su.initialize(config, request, response);
		// �趨contentDispositionΪnull�Խ�ֹ������Զ����ļ���
		// ��֤������Ӻ��������ļ��������趨�������ص��ļ���չ��Ϊ
		// docʱ����������Զ���word��������չ��Ϊpdfʱ��
		// ���������acrobat�򿪡�
		su.setContentDisposition(null);
		// �����ļ�

		su.downloadFile(path);

	}

	/**
	 * ��������
	 * 
	 */
	public static void downLoadExcel(String s_kjname,
			HttpServletRequest request, HttpServletResponse response,
			ServletConfig config) throws Exception {

		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", "MJ"
					+ s_kjname.substring(2));
			if (y_sql == null || y_sql.length == 0) {
				throw new Exception("û���ҵ��ؼ�" + s_kjname + "!");
			}

		} catch (Exception e) {
			throw new Exception("��λ" + s_kjname + "SQL����!" + e.toString());
		}
		String cs = y_sql[0][2].trim(); // ���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); // ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); // ҵ���õ�SQL2,�������������SQL
		// SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request, "MJ" + s_kjname.substring(2));
		// SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		sql = HtmlFunction.parseVar(sql, request, "");
		// String sql = HtmlFunction.parseVar(y_sql[0][1], request, "");
		String url = WebControl.class.getResource("/").getPath();
		url = url.substring(1, url.length()-16);
		String path = XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT)
				+ "/download/report" + System.currentTimeMillis() + ".xls";
		path=url+path;
		// String path = new
		// File("/").getAbsolutePath()+"\\report"+System.currentTimeMillis()+".xls";
		SQLQuery query;
		Object data[][];
		query = DSManager.getSQLQuery();
		try{
		data = query.ArrayMetaQuery(sql);
		}catch(Exception ex)
		{
			throw new Exception(ex.toString()+"sql:"+sql);
		}
		// int colLen[] = {30,20};
		// SystemFunction.data2TxtFile(data,path,colLen," ");
		
		SystemFunction.createExcelFile(path, data);
		// �½�һ��SmartUpload����
		SmartUpload su = new SmartUpload();
		// ��ʼ��

		su.initialize(config, request, response);
		// �趨contentDispositionΪnull�Խ�ֹ������Զ����ļ���
		// ��֤������Ӻ��������ļ��������趨�������ص��ļ���չ��Ϊ
		// docʱ����������Զ���word��������չ��Ϊpdfʱ��
		// ���������acrobat�򿪡�
		su.setContentDisposition(null);
		// �����ļ�

		su.downloadFile(path);

	}
	
	/**
	 * ���ؿ�������
	 * 
	 */
	public static String downLoadExcelKQ(String s_kjname,
			HttpServletRequest request, HttpServletResponse response,
			ServletConfig config) throws Exception {

		String y_sql[][] = new String[0][0];
		try {

			y_sql = XmlUtil.find(BConstants.PAGE_MJ, "KJM", "MJ"
					+ s_kjname.substring(2));
			if (y_sql == null || y_sql.length == 0) {
				throw new Exception("û���ҵ��ؼ�" + s_kjname + "!");
			}

		} catch (Exception e) {
			throw new Exception("��λ" + s_kjname + "SQL����!" + e.toString());
		}
		String cs = y_sql[0][2].trim(); // ���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); // ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); // ҵ���õ�SQL2,�������������SQL
		// SQL�������еĲ�ѯ����
		String sql_tj = gettjsql(request, "MJ" + s_kjname.substring(2));
		// SQL���:SQL1+SQL2+����+���ӵ�����CS(�����򣬽���)
		sql = sql + sql2 + sql_tj + cs;
		
		sql = HtmlFunction.parseVar(sql, request, "");
		// String sql = HtmlFunction.parseVar(y_sql[0][1], request, "");
		String url = WebControl.class.getResource("/").getPath();
		url = url.substring(1, url.length()-16);
		String file1="report"+System.currentTimeMillis() + ".xls";
		String path = XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT)
				+ "/download/" + file1;
		
		path=url+path;
		
		String mb_filename =url+ XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT)+ "/download/KQMB.xls";
		// String path = new
		// File("/").getAbsolutePath()+"\\report"+System.currentTimeMillis()+".xls";
		SQLQuery query;
		String data[][];
		query = DSManager.getSQLQuery();
		try{
		data = query.ArrayQuery(sql);
		}catch(Exception ex)
		{
			throw new Exception(ex.toString()+"sql:"+sql);
		}
		// int colLen[] = {30,20};
		// SystemFunction.data2TxtFile(data,path,colLen," ");
		
		//SystemFunction.createExcelFile(path, data);
		
		String ym=request.getParameter("yearmonth");
		if (ym==null) ym="";
		String isdown=request.getParameter("isdown");
		if (isdown==null) isdown="";
		
		SystemFunction.builderPOIExcel(mb_filename,path,data,ym);
		
		if(isdown.equals(""))
		return "http://ad-kcc.dderp.cn/salsa/download/"+file1;
		else{
		// �½�һ��SmartUpload����
		SmartUpload su = new SmartUpload();
		// ��ʼ��

		su.initialize(config, request, response);
		// �趨contentDispositionΪnull�Խ�ֹ������Զ����ļ���
		// ��֤������Ӻ��������ļ��������趨�������ص��ļ���չ��Ϊ
		// docʱ����������Զ���word��������չ��Ϊpdfʱ��
		// ���������acrobat�򿪡�
		su.setContentDisposition(null);
		// �����ļ�

		su.downloadFile(path);
		return "";
		}

	}
	
	/**
	 * ��������
	 * 
	 */
	public static void downLoadFile(String s_kjname,
			HttpServletRequest request, HttpServletResponse response,
			ServletConfig config) throws Exception {

		
		String filename=s_kjname.substring(2);
		String url = WebControl.class.getResource("/").getPath();
		url = url.substring(1, url.length()-16);
		String path = XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT)
				+ "/download/" + filename + ".xls";
		path=url+path;
		
		// �½�һ��SmartUpload����
		SmartUpload su = new SmartUpload();
		// ��ʼ��

		su.initialize(config, request, response);
		// �趨contentDispositionΪnull�Խ�ֹ������Զ����ļ���
		// ��֤������Ӻ��������ļ��������趨�������ص��ļ���չ��Ϊ
		// docʱ����������Զ���word��������չ��Ϊpdfʱ��
		// ���������acrobat�򿪡�
		su.setContentDisposition(null);
		// �����ļ�

		su.downloadFile(path);

	}

	public void upLoad(HttpServletRequest request,
			HttpServletResponse response, ServletConfig config)
			throws Exception {

		String path = XmlUtil.readXml(BConstants.CONFIG_FILE,
				BConstants.SYSTEM_ROOT)
				+ "\\upload";
		SmartUpload su = new SmartUpload();

		su.initialize(config, request, response);
		// su.setMaxFileSize(10000);
		// su.setTotalMaxFileSize(20000) ;
		// su.setAllowedFilesList("txt,doc");
		// su.setDeniedFilesList("xls,bat,exe,jsp,htm,html") ;

		su.upload();

		int count = su.save(path);
		if (count > 0) {
			String targetPage = su.getRequest().getParameter("targetPage");
			com.jspsmart.upload.File dFile = su.getFiles().getFile(0);
			String fileName = dFile.getFileName();
			if (fileName.startsWith("PDD")) {
				this.parsePDDFile(path + "\\" + fileName);
			}
			if (fileName.startsWith("RKD")) {
				this.parseRKDFile(path + "\\" + fileName);
			}
			if (fileName.startsWith("BHD")) {
				this.parseBHDFile(path + "\\" + fileName);
			}

			request.getRequestDispatcher(targetPage).forward(request, response);
		} else
			throw new Exception("�ļ�û���ϴ��ɹ�!");

	}
	
	

	public String parseBHDFile(String path) throws Exception {

		File file = new File(path);
		String filedate;
		if (!file.exists())
			throw new Exception("�����ļ�" + path + "û�ҵ�!");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		SQLUpdater updater = DSManager.getSQLUpdater();
		updater.executeUpdate("delete from prepare_txm_list_tmp");
		while ((filedate = br.readLine()) != null) {
			String bhdh = filedate.substring(0, 20);
			String xh = filedate.substring(21, 41);
			String productTxm = filedate.substring(42, 62);
			String sql = "insert into prepare_txm_list_tmp(BHDH,XH,PRODUCTTXM) values( '"
					+ bhdh + "','" + xh + "','" + productTxm + "')";
			updater.executeUpdate(sql);
		}
		updater.executeCall("{ call sp_pack4txm }");
		br.close();
		return "ok";
	}

	public String parseRKDFile(String path) throws Exception {

		File file = new File(path);
		String filedate;
		if (!file.exists())
			throw new Exception("�����ļ�" + path + "û�ҵ�!");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		SQLUpdater updater = DSManager.getSQLUpdater();
		updater.executeUpdate("delete from enter_txm_list");
		while ((filedate = br.readLine()) != null) {

			String productTxm = filedate.substring(21, 41).trim();
			String enterNum = filedate.substring(42, 52).trim();
			String sql = "";

			sql = "insert into ENTER_TXM_LIST (PRODUCT_TXM,PRODUCT_CODE,SHOW_NUM,PRODUCT_NAME) select PRODUCT_TXM,PRODUCT_CODE,'"
					+ enterNum
					+ "',PRODUCT_NAME  from PRODUCT_LIST  where PRODUCT_TXM = '"
					+ productTxm + "'";
			updater.executeUpdate(sql);
		}
		updater.executeCall("{ call sp_enter4txm }");
		br.close();
		return "ok";
	}

	public String parsePDDFile(String path) throws Exception {

		File file = new File(path);
		String filedate;
		if (!file.exists())
			throw new Exception("�����ļ�" + path + "û�ҵ�!");
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		SQLUpdater updater = DSManager.getSQLUpdater();
		updater.executeUpdate("delete from check_txm_list");
		while ((filedate = br.readLine()) != null) {
			String checkCode = filedate.substring(0, 20).trim();
			String productTxm = filedate.substring(21, 41).trim();
			String enterNum = filedate.substring(42, 52).trim();
			String checkDate = filedate.substring(53, 73).trim();
			String sql = "";

			sql = "insert into CHECK_TXM_LIST (CHECK_CODE,PRODUCT_TXM,PRODUCT_CODE,SHOW_NUM,PRODUCT_NAME,Product_size,product_untl,check_date) select '"
					+ checkCode
					+ "',PRODUCT_TXM,PRODUCT_CODE,'"
					+ enterNum
					+ "',PRODUCT_NAME,product_size,product_untl,'"
					+ checkDate
					+ "'  from PRODUCT_LIST where PRODUCT_TXM = '"
					+ productTxm
					+ "'";
			updater.executeUpdate(sql);
		}
		updater.executeCall("{ call sp_check4txm }");

		br.close();
		return "ok";
	}

	public String parseReport(String html, String kjm,
			HttpServletRequest request) throws Exception {

		// ȡ��ҵ��SQL
		String y_sql[][] = new String[0][0];
		try {
			y_sql = XmlUtil.find(BConstants.PAGE_RP, "KJM", kjm);
		} catch (Exception e) {
			throw new Exception("����" + kjm + "����!" + e.toString());
		}
		if (y_sql == null || y_sql.length == 0) {
			throw new Exception("�ؼ���" + kjm + "û�ҵ�!");
		}

		// ��ȡҵ����������
		String ljh = y_sql[0][1].trim(); // �������Ӻ�
		String cs = y_sql[0][2].trim(); // ���ڼ���order by ,group by ���
		String sql = y_sql[0][3].trim(); // ҵ���õ�SQL
		String sql2 = y_sql[0][4].trim(); // ҵ���õ�SQL2,�������������SQL
		String reportFile = y_sql[0][5].trim();

		sql = sql + sql2 + cs;
		sql = HtmlFunction.parseVar(sql, request, "");
		String reportContent = "";
		try {

			reportContent = SystemFunction.getReportContent(ljh, sql,
					reportFile);
		} catch (Exception e) {
			throw new Exception(kjm + "���ɱ������ݳ���!" + e.toString());
		}
		String newHtml = SystemFunction.replace(html, "$$" + kjm + ",",
				reportContent);
		return newHtml;
	}

	public static synchronized void parseLicense(String s_kjname)
			throws Exception {
		DataExport de = new DataExport();
		String agentCode = s_kjname.substring(3);
		de.exportLicense(agentCode);
	}
	
	public String parseUpLoad(String html, String kj, HttpServletRequest request, HttpServletResponse response) throws Exception
   {
    String path = XmlUtil.readXml("dsconfig.xml", "/datasource-configuration/systemConfig/root") + "upload";
   
    ServletFileUpload upload = null;
    DiskFileItemFactory factory = new DiskFileItemFactory();
    upload = new ServletFileUpload(factory);
    upload.setFileSizeMax(-1L);
    String url = WebControl.class.getResource("/").getPath();
    url = url.substring(1, url.length() - 16);
    String filename = url + path + "\\" + DateHelper.getShortDateTimeTrim() + ".xls";

    List items = upload.parseRequest(request);
    for (int i = 0; i < items.size(); ++i)
    {
      FileItem fi = (FileItem)items.get(i);
      if (fi.isFormField()) {
        continue;
      }
      if ((fi.getName() != null) && (fi.getSize() != 0L))
      {
        java.io.File newFile = new java.io.File(filename);
        fi.write(newFile);
        filename = filename.replaceAll("\\\\", "\\\\\\\\");
      }
      else {
        filename = "";
      }

    }
    return SystemFunction.replace(html, "$$" + kj + ",", filename);
   }

}
