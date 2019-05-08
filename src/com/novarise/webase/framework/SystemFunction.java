package com.novarise.webase.framework;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.NumberFormat;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.Region;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;

import com.jacob.com.Dispatch;
import com.novarise.webase.BConstants;
import com.novarise.webase.util.DateHelper;
import com.novarise.webase.xml.DataFormator;
import com.novarise.webase.xml.XmlUtil;


/**
 * Web系统中用到的函数库
 */

public class SystemFunction {

	

	/**
	 * 用于字符串替换的方法: 如:要将"This is a example"中的"a"替换为"an" 则如下调用即可: replace("This is
	 * a example","a","an") 则返回: "This is an axample"
	 */
	public static String replace(String src, String old_str, String new_str) {
		if (old_str.length() == 0 || old_str == null)
			return src;
		if (  new_str == null || new_str.length() == 0 )
			new_str = "";
		String newsrc = "";

		int posStart = 0, pos;
		while ((pos = src.indexOf(old_str, posStart)) != -1) {
			newsrc += src.substring(posStart, pos) + new_str;
			posStart = pos + old_str.length();

		}
		if (posStart < src.length()) {
			newsrc += src.substring(posStart);
		}
		return newsrc;
	}

	/**
	 * 用于中文转换的方法 注意:可能抛出异常, 异常内容：中文转化错误
	 */
	public static String converGB(String content) {
		String s_uncodestr = "";
		try {
			byte[] bytes = content.trim().getBytes("8859_1");
			s_uncodestr = new String(bytes, "GB2312");
		} catch (Exception e) {
			System.out.print(e.toString());
		}
		return s_uncodestr;

	}

	/**
	 * 用于反转换中文的方法 注意:可能抛出异常, 异常代号:102
	 */
	public static String unConverGB(String content) {
		String s_uncodestr = "";
		try {
			byte[] bytes = content.trim().getBytes("GB2312");
			s_uncodestr = new String(bytes, "8859_1");
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return s_uncodestr;
	}

	/**
	 * 用于装入文件,并将文件转换为属性文件，以便后面方便读取这个文件. 注意: 这里的文件路径是类的路径，即:与classpath一致 如:
	 * loadFile("config.properties");
	 */
	public  static Properties loadPropertiesFile(String filename) throws Exception {
		Properties prs = new Properties();//实例化一个属性文件
		ClassLoader cl = SystemFunction.class.getClassLoader();
		InputStream is = cl.getResourceAsStream(filename);//生成一个文件
		if (is == null)
			throw new Exception("属性文件" + filename + "没有找到!");
		prs.load(is);//将文件加入属性文件中，以便提取
		return prs;
	}

	/**
	 * 得到本系统的配置文件 "config.properties"
	 * 
	 * @return @throws
	 *         java.lang.Exception
	 */
	/*public  Properties getPropertiesFile() throws Exception {
		return loadPropertiesFile(PROPERTIESFILENAME);
	}*/

	/**
	 * 用于读取文件内容的方法,将文件的内容转换为字符串并返回 注意:可能抛出异常；文件没找到 如:
	 * readFile("c:\","main.htm");
	 */
	public static String readFile(String filename) throws Exception {
        StringBuffer sb = new StringBuffer();
        //相对路径
        String url = SystemFunction.class.getResource("/").getPath();
		url = url.substring(1, url.length()-16);
		File file = new File( url+filename);
        //绝对路径
        //File file = new File( filename);
        
        if (!file.exists())
            throw new Exception("载入文件" +  filename + "没找到!");
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
        String filedate;
        /*ClassLoader load = SystemFunction.class.getClassLoader();
        InputStreamReader ir = new InputStreamReader(load.getResourceAsStream(filename));
        BufferedReader br = new BufferedReader(ir);*/
        while ((filedate = br.readLine()) != null) {
            sb.append(filedate);
            sb.append("\n");
        }
        br.close();
        return sb.toString();
	}

	/**
	 * 用于获得错误信息的方法 参数: err_id 错误ID号 err_msg 错误提示信息 err_details 错误具体信息
	 * 有关错误ID号,以及取值见:dsconfig.xml文件
	 */
	public  static String showError(int err_id, String err_msg, String err_details,HttpServletRequest request) {

		String errPageContent = "";
		try {
			String root = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT);
			String errpage = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ERRORPAGE);
			errPageContent = readFile(root+errpage);
			errPageContent = replace(errPageContent, "$$err_id$$", String.valueOf(err_id));
			errPageContent = replace(errPageContent, "$$err_msg$$", err_msg);
			errPageContent = replace(errPageContent, "$$err_msg_detail$$",err_details);
			String title   = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE);
			errPageContent = replace(errPageContent, "$$msg_version$$",title);
			errPageContent = HtmlFunction.parseVar(errPageContent, request, "");
		} catch (Exception e) {
			errPageContent = "系统执行时出错，如有任何问题，请联系系统管理员！<br>" + e.toString();
			e.printStackTrace();
		}
		return errPageContent;

	}

	/**
	 * 用于获得错误信息的方法 参数: err_id 错误ID号 err_msg 错误提示信息 err_details 错误具体信息
	 * 有关错误ID号,以及取值见:err_config.properties文件
	 */
	public  static String showLoginError(int err_id, String err_msg, String err_details) {

		String errPageContent = "";
		try {
			String root = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT);
			String errpage = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ERRORLOGINPAGE);
			String title   = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE);
			errPageContent = readFile(root+errpage);
			errPageContent = replace(errPageContent, "$$err_id$$", String.valueOf(err_id));
			errPageContent = replace(errPageContent, "$$err_msg$$", err_msg);
			errPageContent = replace(errPageContent, "$$err_msg_detail$$",err_details);
			errPageContent = replace(errPageContent, "$$msg_version$$",title);
		} catch (Exception e) {
			errPageContent = "系统执行时出错，如有任何问题，请联系系统管理员！<br>" + e.toString();
			
		}

		return errPageContent;

	}
	
	
	/**
	 * 用于获得错误信息的方法 参数: err_id 错误ID号 err_msg 错误提示信息 err_details 错误具体信息
	 * 有关错误ID号,以及取值见:err_config.properties文件
	 */
	public  static String showWeiXinPay(String err_id, String err_msg, String err_details) {

		String errPageContent = "";
		try {
			String root = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT);
			String errpage = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_WEIXINPAYPAGE);
			String title   = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE);
			errPageContent = readFile(root+errpage);
			errPageContent = replace(errPageContent, "$$err_id$$", String.valueOf(err_id));
			errPageContent = replace(errPageContent, "$$err_msg$$", err_msg);
			errPageContent = replace(errPageContent, "$$err_msg_detail$$",err_details);
			errPageContent = replace(errPageContent, "$$msg_version$$",title);
		} catch (Exception e) {
			errPageContent = "系统执行时出错，如有任何问题，请联系系统管理员！<br>" + e.toString();
			
		}

		return errPageContent;

	}
	
	/**
	 * 用于获得错误信息的方法 参数: err_id 错误ID号 err_msg 错误提示信息 err_details 错误具体信息
	 * 有关错误ID号,以及取值见:err_config.properties文件
	 */
	public  static String showSessionError(int err_id, String err_msg, String err_details) {

		String errPageContent = "";
		try {
			String root = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT);
			
			String errpage = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ERRORSESSIONPAGE);
			errPageContent = readFile(root+errpage);
			errPageContent = replace(errPageContent, "$$err_id$$", String
					.valueOf(err_id));
			errPageContent = replace(errPageContent, "$$err_msg$$", err_msg);
			errPageContent = replace(errPageContent, "$$err_msg_detail$$",
					err_details);
			String title   = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_TITLE);
			errPageContent = replace(errPageContent, "$$msg_version$$",
					title);
		} catch (Exception e) {
			errPageContent = "系统执行时出错，如有任何问题，请联系系统管理员！<br>" + e.toString();
			
		}

		return errPageContent;

	}

	public static String getYear() {
		Date currDate = new Date();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(currDate);
		return parseNormalString(calendar.get(calendar.YEAR));
	}

	public static String getMonth() {
		Date currDate = new Date();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(currDate);

		return parseNormalString(calendar.get(Calendar.MONTH) + 1);
	}

	public static String getDay() {
		Date currDate = new Date();
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(currDate);
		return parseNormalString(calendar.get(Calendar.DATE));

	}
	
	public static String getDate(Date date)
	{
		return parseNormalString((date.getYear() + 1900))+"-"+parseNormalString(date.getMonth()+1)+"-"+parseNormalString(date.getDate());
	}

	/**
	 * 用于系统出现错误时,将错误写入Log文件里面 参数: err_details 错误信息 log文件格式 系统时间:||err_details
	 * 注意:log文件名见属性文件config.properties
	 */
	public  void writeErrorLog(String err_details) throws Exception {
		// BufferedOutputStream outfile=null;
		try {
			String path = getClass().getResource("SysFun.class").toString();
			if (path.indexOf("/") != -1)
				path = path.substring(6, path.lastIndexOf("/") + 1);
			FileOutputStream file = new FileOutputStream(path + "error.log");
			BufferedOutputStream outfile = new BufferedOutputStream(file);
			outfile.write(err_details.getBytes());

			outfile.close();
		} catch (Exception e) {
			throw new Exception("日志文件:" + "error.log" + "没找到!" + e.toString());
		} finally {
			// outfile.close() ;
		}
	}

	/**
	 * 转化为系统标准字符串 参数:str 要转化的字符串 返回:转化后的字符串 转化标准:将一位数的数字字符串，前加'0'构成两位
	 * 如:getNormalString("1");->"01"
	 */
	public static String parseNormalString(int num) {
		String numstr = String.valueOf(num);
		if (numstr.length() == 1) {
			numstr = "0" + numstr;
		}
		return numstr;
	}
	
	
	public static String null2str(String str) {
		str = null2SpaceString(str);
		if(str.equals("&nbsp;"))
		{
		    return "";
		}
		return str;
	}
	
	public static String null2SpaceString(Object obj) {
		if(obj == null || obj.equals("null") || obj.equals("NULL")){
			return "";
		}
		return (String)obj;
		
	}
	
	public static String toOriginString(String str) {
		/*
		* 把一个包含实体转义的字符串从实体转义转回原来的格式
		*/
		StringBuffer buf = new StringBuffer();
		Matcher m = Pattern.compile("&#\\d+;").matcher(str);
		int start = 0;
		String findstr = null;
		while (m.find()) {
		findstr = m.group(); 
		buf.append(str.substring(start, m.start())+(char)Integer.parseInt(findstr.substring(2, findstr.length() - 1)));
		start = m.end();
		}
		buf.append(str.substring(start));
		return buf.toString();
		}
    
    /**
     * 数据生成excelFile
     * 参数：文件路径,数据
     * 返回：是否成功
     * 
     */
    
    public static  String createExcelFile(String fileName,Object[][] data) throws Exception{
        try{
            WritableWorkbook wwb = Workbook.createWorkbook(new File(fileName));
            WritableSheet     ws  = wwb.createSheet("Sheet1",0);
           
            
            NumberFormat nf = new NumberFormat("0.00");
            WritableCellFormat wcfN = new WritableCellFormat(nf);
            
            NumberFormat nf1 = new NumberFormat("0");
            WritableCellFormat wcfN1 = new WritableCellFormat(nf1);
            for(int i=0;i<data.length;i++){
                for(int j=0;j<data[i].length;j++){
                	ws.setColumnView(j, 20);
                	Object obj = data[i][j];
                     
                	if (obj instanceof Integer){
                		int value = ((Integer)obj).intValue();
                		jxl.write.Number labelNF = new  jxl.write.Number(j, i, value,wcfN1);
                		
                    	ws.addCell(labelNF);
                	}
                	if(obj instanceof String){
                		String value=(String)obj;
                		Label label = new Label(j,i,value);
                        ws.addCell(label); 
                	}
                	if(obj instanceof BigDecimal){
                		double value=((BigDecimal)obj).doubleValue();
                		jxl.write.Number labelNF = new  jxl.write.Number(j, i, value,wcfN);
                    	ws.addCell(labelNF);
                	}
                	
                		
                	
       
                    
                }
                
            }
            wwb.write();
            wwb.close();
            return "ok";
        }catch(Exception e){
           throw new Exception("创建Excel文件时出错! " + e.toString()); 
        }
        
           
    }
    
    
    
    /**
     * 数据生成txtFile
     * 参数：数据，文件路径,列分隔符
     * 返回：是否成功
     * 
     */
    public static boolean createTxtFile( Object[][] data,String path,String martet) throws Exception{

        boolean flag = true;
        
        PrintStream out1 ;
        StringBuffer sb = new  StringBuffer();
       
        try {
            out1 =new PrintStream(new FileOutputStream(path));
            for(int i=0;i<data.length;i++){
               String row="";
               for(int j=0;j<data[i].length-1;j++){
            	   row +=data[i][j]+martet;
                   
                }
               sb.append(row.substring(0,row.length()-1));
               sb.append("\r\n");
                
            }
            out1.print(sb.toString());
        } catch (Exception e) {
            flag = false;
            throw new Exception("创建txt文件时出错!"+e.toString());
           
            
        }
        
        
        return flag;

    }
    
    /**
     * 数据生成txtFile
     * 参数：数据，文件路径,列分隔符
     * 返回：是否成功
     * 
     */
    public static void data2StringBuffer( Object[][] data,String martet, StringBuffer sb,String tableName) throws Exception{

       
        sb.append(tableName);
        sb.append("\r\n");
        try {
            
            for(int i=0;i<data.length;i++){
               String row="";
               for(int j=0;j<data[i].length;j++){
            	   row +=data[i][j]+martet;
                   
                }
               sb.append(row.substring(0,row.length()-1));
               sb.append("\r\n");
                
            }
           
        } catch (Exception e) {
            throw new Exception("转换为StringBuffer时出错!"+e.toString());
           
            
        }
        
        

    }
    
    /**
     * 数据生成txtFile
     * 参数：数据，文件路径,字段长度，不足填空的字符
     * 返回：是否成功
     * 
     */
    public static boolean data2TxtFile( String[][] data,String path,int colLen[],String fillstring) throws Exception{

        boolean flag = true;
        
        PrintStream out1 ;
        StringBuffer sb = new  StringBuffer();
       
        try {
            out1 =new PrintStream(new FileOutputStream(path));
            for(int i=0;i<data.length;i++){
               for(int j=0;j<data[i].length-1;j++){
                   sb.append(DataFormator.stringAddEOF(data[i][j], colLen[j], fillstring));
                }
               sb.append("\r\n");
                
            }
            out1.print(sb.toString());
        } catch (Exception e) {
            flag = false;
            e.printStackTrace();
            throw new Exception("创建txt文件时出错!"+e.toString());
           
            
        }
        
        
        return flag;

    }
   
	public static  String getReportContent(String dataCon,String sql,String reportFile){
         String reportContent="";
         Dispatch dispatch = new Dispatch("printx.adocon");
         reportContent = dispatch.call(dispatch, "fprint", dataCon, sql, reportFile).toString();
         return reportContent;
    }
	
	public static void builderPOIExcel(String mb_filename,String path,String data[][],String yearmonth) throws Exception{
		//excel模板路径    
		
		
		File fi=new File(mb_filename);    
		POIFSFileSystem fs=null;
		fs = new POIFSFileSystem(new FileInputStream(fi));
	
		
		//读取excel模板    
		HSSFWorkbook wb=null;
		wb = new HSSFWorkbook(fs);
		
		//读取了模板内所有sheet内容    
		HSSFSheet sheet = wb.getSheetAt(0);
		HSSFRow rows = sheet.getRow(1); 
		HSSFCell cells = rows.getCell(1); 
		
		cells.setCellValue(yearmonth);
		
		
		
		sheet.autoSizeColumn(1); 
		CellStyle style = wb.createCellStyle();
		
		
		style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		style.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
		style.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
		style.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
		style.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框 
		style.setWrapText(true);
		
		
		HSSFRow row2 = sheet.createRow(2);
		HSSFCell cell0 = row2.createCell(0);
		cell0.setCellStyle(style);
		cell0.setCellValue("姓名");
		HSSFCell cell1 = row2.createCell(1);
		cell1.setCellStyle(style);
		cell1.setCellValue("手机号");
		HSSFCell cell2 = row2.createCell(2);
		cell2.setCellStyle(style);
		cell2.setCellValue("岗位");
		
		HSSFRow row3 = sheet.createRow(3);
		HSSFCell cell30 = row3.createCell(0);
		cell30.setCellStyle(style);
		cell30.setCellValue("");
		cell30 = row3.createCell(1);
		cell30.setCellStyle(style);
		cell30.setCellValue("");
		cell30 = row3.createCell(2);
		cell30.setCellStyle(style);
		cell30.setCellValue("");
		
		for(int i=0;i<31;i++)
		{
			//row.createCell(i+3).setCellStyle(style);
			HSSFCell cell = row2.createCell(i+3);
			cell.setCellStyle(style);
			cell.setCellValue(i+1);
			
			HSSFCell cell3 = row3.createCell(i+3);
			cell3.setCellStyle(style);
			
			cell3.setCellValue(DateHelper.getDayofweek(yearmonth+"-"+(i+1)));
		}
		
		sheet.addMergedRegion(new Region(2, (short)0,3,(short)0));    
		sheet.addMergedRegion(new Region(2, (short)1,3,(short)1));    
		sheet.addMergedRegion(new Region(2, (short)2,3,(short)2)); 
		
		
		
        CellStyle stylered = wb.createCellStyle();
		
        stylered.setFillForegroundColor(IndexedColors.RED.getIndex());
        stylered.setFillPattern(CellStyle.SOLID_FOREGROUND);
        stylered.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        stylered.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框    
        stylered.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框    
        stylered.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框    
        stylered.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框 
        stylered.setWrapText(true);
		
		
		for (int i=0;i<data.length;i++)
		{
			HSSFRow rowx = sheet.createRow(i+4);
			for(int j=0;j<data[i].length-1;j++)
			{
				HSSFCell celly = rowx.createCell(j);
				
				celly.setCellValue(data[i][j]);
				if(data[i][j].contains("迟到") || data[i][j].contains("早退"))
				{
					//System.out.println(data[i][j]);
					celly.setCellStyle(stylered);
				}else{
					celly.setCellStyle(style);
				}
			}
		}
		
		
	   // HSSFRow rows = sheet.getRow(2);         
	
		//在相应的单元格进行赋值    
		//HSSFCell cell = rows.getCell(3); 
		//row.setHeight((short) 825);//目的是想把行高设置成25px
		/*CellStyle style = wb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.RED.getIndex());
		style.setFillPattern(CellStyle.SOLID_FOREGROUND);
		style.setWrapText(true);
		//style.setFillForegroundColor(IndexedColors.RED.getIndex());// 设置背景色   
		cell.setCellValue("08:00 16:30");*/
		//System.out.println(cell.getStringCellValue());
		//cell.setCellStyle(style);
		
		//String filename ="考勤数据"+String.valueOf(System.currentTimeMillis()).toString();  
		FileOutputStream out = new FileOutputStream(path);    
		wb.write(out);    
		out.close();  
		out.flush();  
		System.out.println("成功！");
		//cell.setCellValue(XX);  
	}


	public String s_exception;
	//public static final String PROPERTIESFILENAME = "config.properties";
   
    public static void main(String agrs[]){
        //Dispatch test = new Dispatch("printx.adocon");
        //String con="Provider=SQLOLEDB.1;Password=salsa;Persist Security Info=True;User ID=sa;Initial Catalog=drp;Data Source=127.0.0.1\\salsa";
        //String sql="select a.product_code,a.product_name,a.product_size,a.product_color,a.product_untl,a.product_price,a.fact_num-isnull(a.del_num,0) rel_num,a.fact_amount-isnull(a.del_amount,0) rel_amount,b.Pricetype_name,a.package_demo,a.order_code,agent_name,dbo.getdatestr1(out_date) out_date  from order_product_list a,price_type b,order_list c  where a.price_type=b.pricetype_id and a.order_code=c.order_code and a.order_code='10020080827007' and a.fact_num-isnull(a.del_num,0) !=0 order by a.id";
        //System.out.println(Dispatch.call(test, "fprint", con, sql, "C:\\book6\\printx\\diaoyong\\report\\salsa_fhd.frf"));
       // System.out.println(SystemFunction.getReportContent(con, sql, "C:\\book6\\printx\\diaoyong\\report\\salsa_fhd.frf"));
    	SystemFunction sa=new SystemFunction();
    	try {
			//sa.builderPOIExcel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	

}
