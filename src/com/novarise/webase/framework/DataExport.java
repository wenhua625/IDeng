package com.novarise.webase.framework;

import java.io.FileOutputStream;
import java.io.PrintStream;

import com.novarise.webase.BConstants;
import com.novarise.webase.xml.XmlUtil;
import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;

public class DataExport {
    public void exportData() throws Exception{
    	
    	try{
    	String path = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT)+"\\posBaseData.pos";
    	
    	SQLQuery query;
        Object data[][];
        query = DSManager.getSQLQuery();
        
        
        
        StringBuffer content= new StringBuffer();
        String sql = "select Product_Code,Product_Name,Product_Txm,Product_Size,Product_Color,Product_Untl,0 Product_Num,Series_Code, Product_Price Cost_Price, Type_Sales  Sale_Price,Price_Type  from product_list";
        data = query.ArrayMetaQuery(sql);
        SystemFunction.data2StringBuffer(data, "|",content,"[Pos_Stock]");
        
        sql="select Series_Code,Series_Name,Sts from series_list";
        data = query.ArrayMetaQuery(sql);
        SystemFunction.data2StringBuffer(data, "|",content,"[Series_List]");
        
        PrintStream out1 ;
        out1 =new PrintStream(new FileOutputStream(path));
        out1.print(content.toString());
    	}catch(Exception e){
    		 throw new Exception("导出txt文件时出错!"+e.toString());
    	}
    	
    	DataImport imp = new DataImport();
    	imp.importData(XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT)+"\\socketServer\\","pos");
        
    }
    
    public void exportLicense(String agentCode) throws Exception{
    	String path = XmlUtil.readXml(BConstants.CONFIG_FILE, BConstants.SYSTEM_ROOT)+"\\pos_license\\"+agentCode+".license";
    	try{
    	SQLQuery query;
        Object data[][];
        query = DSManager.getSQLQuery();
        
        StringBuffer content= new StringBuffer();
        String sql="select Agent_Code,Agent_Name,Sjbm,'shop' Agent_Type,Agent_Address,Agent_Tel,Agent_Fax,Contact_Man from agent_list where agent_code='"+agentCode+"'";
        data = query.ArrayMetaQuery(sql);
        SystemFunction.data2StringBuffer(data, "|",content,"[Agent_List]");
        
        sql="select '"+agentCode+"' YHH,'shop' YHZ,'"+(String)data[1][1]+"' Yhxm,'"+agentCode+"' Dept,'25ed1bcb423b0b7200f485fc5ff71c8e' MM,'0' Sts union  select 'admin' Dept,'admin' YHZ,'系统管理员' Yhxm,'"+agentCode+"' Dept,'25ed1bcb423b0b7200f485fc5ff71c8e' MM,'0' Sts";
        data = query.ArrayMetaQuery(sql);
        SystemFunction.data2StringBuffer(data, "|",content,"[TJ_SYS_YH]");
        PrintStream out1 ;
        out1 =new PrintStream(new FileOutputStream(path));
        out1.print(content.toString());
    	}catch(Exception e){
    		throw new Exception("生成POS License出错!"+e.toString());
    	}
        
        
    }
}
