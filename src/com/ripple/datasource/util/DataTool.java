package com.ripple.datasource.util;

import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import com.novarise.webase.framework.SystemFunction;





public class DataTool
{	
	private static Logger logger = LogManager.getLogger( DataTool.class );
	/**
	 * 将结果集转换List
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static List rsToList( ResultSet rs ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        List rs_data = new ArrayList();
        while( rs.next() )
        {
        	HashMap row = new HashMap( columnCount,1 );
            for( int i = 1; i <= columnCount; i ++ )
            {

            	/*if( rsmd.getColumnType(i) == Types.CHAR 
            	 || rsmd.getColumnType(i) == Types.VARCHAR 
            	 || rsmd.getColumnType(i) == Types.LONGVARCHAR )
            	{
            		String temp = rs.getString(i);
            		
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
            	}else if(rsmd.getColumnType(i) == Types.DECIMAL
               				|| rsmd.getColumnType(i) == Types.NUMERIC){
            		BigDecimal temp;
               		if(rs.getObject(i) == null){
               			temp=new BigDecimal(0);
               		}else{
               			temp=rs.getBigDecimal(i);
               		}
               		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
               	}
            	else
            	{*/
            		//row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(rs.getString(i)));
            	/*}*/
                if (rsmd.getColumnName(i).toLowerCase().equals("leaf")){//针对json数据
                	if (Integer.parseInt(rs.getString(i))>0){
                		row.put(rsmd.getColumnName(i).toLowerCase(),false);
                	}else{
                		row.put(rsmd.getColumnName(i).toLowerCase(),true);
                	}
                }
                else{
                	row.put(rsmd.getColumnName(i).toLowerCase(),rs.getObject(i));
                }
            	
            }
            rs_data.add(row);
        }
        return rs_data;
	}
	/**
	 * 将结果集转换为Array
	 * @param rs
	 * @param pageSize
	 * @return
	 * @throws SQLException
	 */
	public static String[][] rsToArray( ResultSet rs ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        List rs_data = new ArrayList();
        int rowid =1;
        while( rs.next() )
        {
        	String[] sRow = new String[columnCount+1];
        	for( int i = 0; i < columnCount; i ++ )
            {
                sRow[i] =SystemFunction.null2SpaceString(rs.getString(i+1));
                
            }
            sRow[columnCount] = String.valueOf(rowid);
            rs_data.add(sRow);
            rowid++;
            
        }
        
        String [][] returnData = new String[rs_data.size()][];
        Object [] oData = rs_data.toArray();
        for(int i =0;i <rs_data.size(); i ++){
        	returnData[i] = (String[])oData[i];
        }
        return returnData;
	}
    
    /**
     * 将结果集转换为Array
     * @param rs
     * @param pageSize
     * @return
     * @throws SQLException
     */
    public static Object[][] rsToMetaArray( ResultSet rs ) throws SQLException
    {
        ResultSetMetaData rsmd = rs.getMetaData();
        int columnCount = rsmd.getColumnCount();
        
        List rs_data = new ArrayList();
        String metaRow[] = new String[columnCount];
        for(int j=0;j < columnCount; j ++ ){
            metaRow[j] = rsmd.getColumnName(j+1);
            
        }
        rs_data.add(metaRow);
        while( rs.next() )
        {
            Object[] sRow = new Object[columnCount];
            for( int i = 0; i < columnCount; i ++ )
            {
              sRow[i] =rs.getObject(i+1);
            }
            rs_data.add(sRow);
            
        }
        
        Object [][] returnData = new Object[rs_data.size()][];
        rs_data.toArray(returnData);
        return returnData;
    }
	/**
	 * 将结果集转换为List
	 * @param rs
	 * @param addKH
	 * @return
	 * @throws SQLException
	 */
	public static List rsToList( ResultSet rs, boolean addKH) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        List rs_data = new ArrayList();
        while( rs.next() )
        {
        	HashMap row = new HashMap( columnCount,1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
                row.put(rsmd.getColumnName(i).toLowerCase(),rs.getObject(i));
            }
            rs_data.add(row);
        }
        return rs_data;
	}
	
	/**
	 * 将结果集转换为Array
	 * @param rs
	 * @param pageSize
	 * @return
	 * @throws SQLException
	 */
	public static String[][] rsToArray( ResultSet rs, int pageid,int pageSize ) throws SQLException
	{
       //定义与分页相关的变量
		int rowbegin = (pageid - 1) * pageSize + 1;
		int rowend = pageid * pageSize;
		int rows = 1;
		//根据传来的pageid,pagerownum取出符合条件的记录放到矢量v_data中
		int cols = rs.getMetaData().getColumnCount();
		List v_data = new ArrayList();
		while (rs.next() && rows <= rowend) {
			if (rows >= rowbegin) {
				String row[] = new String[cols];
				for (int col = 0; col < row.length; col++) {
					row[col] = rs.getString(col + 1);
					/*if (row[col] == null || row[col].length() == 0) {
						row[col] = s_default;
					}*/
				}
				v_data.add(row);
			}
			rows++;
		}

		//从矢量v_data中取出记录放到二维数组中
		String [][] returnData = new String[v_data.size()][];
        Object [] oData = v_data.toArray();
        for(int i =0;i <v_data.size(); i ++){
        	returnData[i] = (String[])oData[i];
        }
		return returnData;
		
	}
	
	/**
	 * 将结果集转换为List
	 * @param rs
	 * @param pageSize
	 * @return
	 * @throws SQLException
	 */
	public static List rsToList( ResultSet rs, int pageSize ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        List rs_data = new ArrayList( pageSize );
        int rowNumber = 0;
        while( rs.next() && rowNumber < pageSize )
        {
        	HashMap row = new HashMap( columnCount,1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
                row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(rs.getObject(i)));
            }
            rs_data.add(row);
            rowNumber ++;
        }
        return rs_data;
	}
	/**
	 * 将结果集转换结LinkedList
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static LinkedList rsToLinkedList( ResultSet rs ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        LinkedList rs_data = new LinkedList();
        while( rs.next() )
        {
        	LinkedHashMap row = new LinkedHashMap( columnCount,1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
            	
            	if( rsmd.getColumnType(i) == Types.CHAR 
                   	 || rsmd.getColumnType(i) == Types.VARCHAR 
                   	 || rsmd.getColumnType(i) == Types.LONGVARCHAR )
               	{
            		//row.put(rsmd.getColumnName(i),"'"+rs.getString(i)+"'");modify by yhh
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(rs.getString(i)));
               	}else if(rsmd.getColumnType(i) == Types.DECIMAL
               				|| rsmd.getColumnType(i) == Types.NUMERIC){
               		Integer temp;
               		if(rs.getObject(i) == null){
               			temp=new Integer(0);
               		}else{
               			temp=new Integer(rs.getBigDecimal(i).toString());
               		}
               		
               		
               		 
               		
               		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
               	}
                else{
                	row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(rs.getObject(i)));
                }
            }
            rs_data.add(row);
        }
        return rs_data;
	}
	/**
	 * 将结果集转换为LinkedList
	 * @param rs
	 * @param pageSize
	 * @return
	 * @throws SQLException
	 */
	public static LinkedList rsToLinkedList( ResultSet rs, int pageSize ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        LinkedList rs_data = new LinkedList();
        int rowNumber = 0;
        while( rs.next() && rowNumber < pageSize )
        {
        	LinkedHashMap row = new LinkedHashMap( columnCount,1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
                row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(rs.getObject(i)));
            }
            rs_data.add( row );
            rowNumber ++;
        }
        return rs_data;
	}
	/**
	 * 将结果集转换为HashMap
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	public static HashMap rsToMap( ResultSet rs ) throws SQLException 
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		HashMap row = null;
        if( rs.next() )
        {
        	row = new HashMap( columnCount, 1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
                row.put( rsmd.getColumnName(i).toLowerCase(), formatNull(rs.getObject(i)) );
            }
        }
        return row;
	}
	/**
	 * 将结果集转换成HashMap
	 * @param rs
	 * @param sourceEncoding
	 * @param targetEncoding
	 * @return
	 * @throws SQLException
	 */
	public static HashMap rsToMap( ResultSet rs, String sourceEncoding,String targetEncoding ) throws SQLException 
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		HashMap row = null; 
        if( rs.next() )
        {
        	row = new HashMap( columnCount, 1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
            	if( rsmd.getColumnType(i) == Types.CHAR 
                   	 || rsmd.getColumnType(i) == Types.VARCHAR 
                   	 || rsmd.getColumnType(i) == Types.LONGVARCHAR )
               	{
               		String temp = rs.getString(i);
               		if( temp != null )
               		{
               			try
						{
							temp = new String( temp.getBytes( targetEncoding ), sourceEncoding ).trim();
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
               		}else
               		{
               			temp = "";
               		}
               		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
               	}else if(rsmd.getColumnType(i) == Types.DECIMAL
               				|| rsmd.getColumnType(i) == Types.NUMERIC){
               		Integer temp;
               		if(rs.getObject(i) == null){
               			temp=new Integer(0);
               		}else{
               			temp=new Integer(rs.getBigDecimal(i).toString());
               		}
               		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
               	}
            	else
               	{
               		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(rs.getObject(i)));
               	}
            }
        }
        return row;
	}
	/**
	 * 将结果集转换为Xml doc
	 * @param rs
	 * @param sourceEncoding
	 * @param targetEncoding
	 * @return
	 * @throws SQLException
	 */
	public static Document rsToXml( ResultSet rs ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols=rsmd.getColumnCount();
		Document xmldoc=DocumentHelper.createDocument();
        Element root = xmldoc.addElement("data");
        Element e,e1;
        while( rs.next() )
        {
        	e=root.addElement("row");
            
			for (int i=0;i<cols;i++)
			{
				e1=e.addElement(rs.getMetaData().getColumnLabel(i+1));
				String value=rs.getString(i+1);
				if(value == null || value.length()==0){
					value=" ";
				}
				e1.setText(value);
				
				
			}
			
        }
        
        return xmldoc; 
	}
    
    
	
	/**
	 * 将结果集转换为Xml String
	 * @param rs
	 * @param sourceEncoding
	 * @param targetEncoding
	 * @return
	 * @throws SQLException
	 */
	public static Document rsToTableXml( ResultSet rs ) throws SQLException
	{
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int cols=rsmd.getColumnCount();
		Document xmldoc=DocumentHelper.createDocument();
        Element root = xmldoc.addElement("rows");
        Element e,e1;
        while( rs.next() )
        {
        	e=root.addElement("row");
            e.addAttribute("id", rs.getString(1));
			for (int i=0;i<cols;i++)
			{
				e1=e.addElement("cell");
				String value=rs.getString(i+1);
				if(value == null || value.length()==0){
					value=" ";
				}
				e1.setText(value);
				
				
			}
			
        }
        
        return xmldoc; 
	}
	
	
	/**
	 * 将结果集转换为List
	 * @param rs
	 * @param sourceEncoding
	 * @param targetEncoding
	 * @return
	 * @throws SQLException
	 */
	public static List rsToList( ResultSet rs,String sourceEncoding,String targetEncoding ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        List rs_data = new ArrayList();
        while( rs.next() )
        {
            HashMap row = new HashMap( columnCount,1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
            	if( rsmd.getColumnType(i) == Types.CHAR 
            	 || rsmd.getColumnType(i) == Types.VARCHAR 
            	 || rsmd.getColumnType(i) == Types.LONGVARCHAR )
            	{
            		String temp = rs.getString(i);
            		if( temp != null )
            		{
            			try
						{
							temp = new String( temp.getBytes( targetEncoding ), sourceEncoding ).trim();
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
            		}else
            		{
            			temp = "";
            		}
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
            	}else if(rsmd.getColumnType(i) == Types.DECIMAL
               				|| rsmd.getColumnType(i) == Types.NUMERIC){
            		Integer temp;
               		if(rs.getObject(i) == null){
               			temp=new Integer(0);
               		}else{
               			temp=new Integer(rs.getBigDecimal(i).toString());
               		}
               		row.put(rsmd.getColumnName(i).toLowerCase(),temp.toString());
               	}
            	else
            	{
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(rs.getObject(i)));
            	}
            }
            rs_data.add(row);
        }
        return rs_data; 
	}
	
	/**
	 * 将结果集转换为List
	 * @param rs
	 * @param sourceEncoding
	 * @param targetEncoding
	 * @return
	 * @throws SQLException
	 */
	public static List rsToJsonList( ResultSet rs,String sourceEncoding,String targetEncoding ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        List rs_data = new ArrayList();
        while( rs.next() )
        {
            HashMap row = new HashMap( columnCount,1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
            	if( rsmd.getColumnType(i) == Types.CHAR 
            	 || rsmd.getColumnType(i) == Types.VARCHAR 
            	 || rsmd.getColumnType(i) == Types.LONGVARCHAR )
            	{
            		String temp = rs.getString(i);
            		if( temp != null )
            		{
            			try
						{
							temp = new String( temp.getBytes( targetEncoding ), sourceEncoding ).trim();
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
            		}else
            		{
            			temp = "";
            		}
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
            	}else if(rsmd.getColumnType(i) == Types.DECIMAL
               				|| rsmd.getColumnType(i) == Types.NUMERIC){
            		Integer temp;
               		if(rs.getObject(i) == null){
               			temp=new Integer(0);
               		}else{
               			temp=new Integer(rs.getBigDecimal(i).toString());
               		}
               		row.put(rsmd.getColumnName(i).toLowerCase(),temp.toString());
               	}
            	else
            	{
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(rs.getObject(i)));
            	}
            }
            rs_data.add(row);
        }
        return rs_data; 
	}
	/**
	 * 将结果集转换成List
	 * @param rs
	 * @param sourceEncoding
	 * @param targetEncoding
	 * @param addKH
	 * @return
	 * @throws SQLException
	 */
	public static List rsToList( ResultSet rs,String sourceEncoding,String targetEncoding,boolean addKH ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        List rs_data = new ArrayList();
        while( rs.next() )
        {
            HashMap row = new HashMap( columnCount,1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
            	if( rsmd.getColumnType(i) == Types.CHAR 
            	 || rsmd.getColumnType(i) == Types.VARCHAR 
            	 || rsmd.getColumnType(i) == Types.LONGVARCHAR )
            	{
            		String temp = rs.getString(i);
            		if( temp != null )
            		{
            			try
						{
							temp = new String( temp.getBytes( targetEncoding ), sourceEncoding ).trim();
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
            		}else
            		{
            			temp = "";
            		}
            		if(addKH)
            			row.put(rsmd.getColumnName(i).toLowerCase(),"'"+temp+"'");
            		else
            		    row.put(rsmd.getColumnName(i).toLowerCase(),temp);
            	}else if(rsmd.getColumnType(i) == Types.DECIMAL
               				|| rsmd.getColumnType(i) == Types.NUMERIC){
            		Integer temp;
               		if(rs.getObject(i) == null){
               			temp=new Integer(0);
               		}else{
               			temp=new Integer(rs.getBigDecimal(i).toString());
               		}
               		row.put(rsmd.getColumnName(i).toLowerCase(),temp);
               	}
            	else
            	{
            		row.put(rsmd.getColumnName(i).toLowerCase(),rs.getObject(i));
            	}
            }
            rs_data.add(row);
        }
        return rs_data; 
	}	
	/**
	 * 将结果集转换为List
	 * @param rs
	 * @param pageSize
	 * @param sourceEncoding
	 * @param targetEncoding
	 * @return
	 * @throws SQLException
	 */
	public static List rsToList( ResultSet rs, int pageSize, String sourceEncoding,String targetEncoding ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        List rs_data = new ArrayList( pageSize );
        int rowNumber = 0; 
        while( rs.next() && rowNumber < pageSize )
        {
            HashMap row = new HashMap( columnCount,1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
            	if( rsmd.getColumnType(i) == Types.CHAR 
            	 || rsmd.getColumnType(i) == Types.VARCHAR 
            	 || rsmd.getColumnType(i) == Types.LONGVARCHAR )
            	{
            		String temp = rs.getString(i);
            		if( temp != null )
            		{
            			try
						{
							temp = new String( temp.getBytes( targetEncoding ), sourceEncoding ).trim();
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
            		}else
            		{
            			temp = "";
            		}
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
            	}else if(rsmd.getColumnType(i) == Types.DECIMAL
               				|| rsmd.getColumnType(i) == Types.NUMERIC){
            		Integer temp;
               		if(rs.getObject(i) == null){
               			temp=new Integer(0);
               		}else{
               			temp=new Integer(rs.getBigDecimal(i).toString());
               		}
               		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
               	}
            	else
            	{
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(rs.getObject(i)));
            	}
            }
            rs_data.add(row);
            rowNumber ++; 
        }
        return rs_data; 
	}
	/**
	 * 将结果集转换LinkedList
	 * @param rs
	 * @param sourceEncoding
	 * @param targetEncoding
	 * @return
	 * @throws SQLException
	 */
	public static LinkedList rsToLinkedList( ResultSet rs,String sourceEncoding,String targetEncoding ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        LinkedList rs_data = new LinkedList();
        while( rs.next() )
        {
            LinkedHashMap row = new LinkedHashMap( columnCount,1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
            	if( rsmd.getColumnType(i) == Types.CHAR 
            	 || rsmd.getColumnType(i) == Types.VARCHAR 
            	 || rsmd.getColumnType(i) == Types.LONGVARCHAR )
            	{
            		String temp = rs.getString(i);
            		if( temp != null )
            		{
            			try
						{
							temp = new String( temp.getBytes( targetEncoding ), sourceEncoding ).trim();
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
            		}else
            		{
            			temp = "";
            		}
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
            	}else if(rsmd.getColumnType(i) == Types.DECIMAL
               				|| rsmd.getColumnType(i) == Types.NUMERIC){
            		Integer temp;
               		if(rs.getObject(i) == null){
               			temp=new Integer(0);
               		}else{
               			temp=new Integer(rs.getBigDecimal(i).toString());
               		}
               		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
               	}
            	else
            	{
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(rs.getObject(i)));
            	}
            }
            rs_data.add(row);
        }
        return rs_data; 
	}
	/**
	 * 将结果集转换为LinkedList类对象
	 * @param rs
	 * @param pageSize
	 * @param sourceEncoding
	 * @param targetEncoding
	 * @return
	 * @throws SQLException
	 */
	public static LinkedList rsToLinkedList( ResultSet rs, int pageSize, String sourceEncoding,String targetEncoding ) throws SQLException
	{
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
        LinkedList rs_data = new LinkedList();
        int rowNumber = 0;
        while( rs.next() && rowNumber < pageSize )
        {
            LinkedHashMap row = new LinkedHashMap( columnCount,1 );
            for( int i = 1; i <= columnCount; i ++ )
            {
            	if( rsmd.getColumnType(i) == Types.CHAR 
            	 || rsmd.getColumnType(i) == Types.VARCHAR 
            	 || rsmd.getColumnType(i) == Types.LONGVARCHAR )
            	{
            		String temp = rs.getString(i);
            		if( temp != null )
            		{
            			try
						{
							temp = new String( temp.getBytes( targetEncoding ), sourceEncoding ).trim();
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
            		}else
            		{
            			temp = "";
            		}
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
            	}else if(rsmd.getColumnType(i) == Types.DECIMAL
               				|| rsmd.getColumnType(i) == Types.NUMERIC){
            		Integer temp;
               		if(rs.getObject(i) == null){
               			temp=new Integer(0);
               		}else{
               			temp=new Integer(rs.getBigDecimal(i).toString());
               		}
               		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(temp));
               	}
            	else
            	{
            		row.put(rsmd.getColumnName(i).toLowerCase(),formatNull(rs.getObject(i)));
            	}
            }
            rs_data.add(row);
            rowNumber ++;
        }
        return rs_data; 
	}
	
	public static void showListContent( List listData )
	{
		if( listData.size() > 0 )
		{
	    	for( int i=0; i<listData.size(); i ++ )
	    	{
    			HashMap row = (HashMap)listData.get(i);
    			Set keys = row.keySet();
    			String valueStr = new String();
    			for(Iterator e = keys.iterator(); e.hasNext(); )
    			{
    				String name = (String) e.next();
    				String value = null;
    				Object field = row.get(name);
    				if( field != null )
    				{
    					value = field.toString();
    				}else
    				{
    					value = "";
    				}
    				valueStr += name+":"+value+" ";
    			}
		    	System.out.println( valueStr );
	    	}
		}
	}
	
	public static void showMapContent( HashMap singleRow )
	{
		Set keys = singleRow.keySet();
		String valueStr = new String();
		for(Iterator e = keys.iterator(); e.hasNext(); )
		{
			String name = (String) e.next();
			String value = null;
			Object field = singleRow.get(name);
			if( field != null )
			{
				value = field.toString();
			}else
			{
				value = "";
			}
			valueStr += name+":"+value+" ";
		}
    	System.out.println( valueStr );
	}
	
	public  static Object formatNull(Object object){
		if(object == null || object.equals("null") || object.equals("NULL")){
			return " ";
		}
		return object;
	}
}
