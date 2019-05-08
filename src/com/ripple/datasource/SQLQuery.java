package com.ripple.datasource;

import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.ripple.datasource.connection.ConnectionPool;
import com.ripple.datasource.dialect.Dialect;
import com.ripple.datasource.util.DataTool;
import com.ripple.datasource.util.TimeTools;
import com.ripple.module.MyDataRow;
import com.ripple.module.MyDataTable;

/*
 * 执行数据库SQL查询操作
 */
public class SQLQuery
{
	private static Logger logger = LogManager.getLogger( SQLQuery.class );

	//数据源名称
	private String DSName;
	//数据库连接会话
	private ConnectionPool cp;
	//数据库编码
	private String DBEncoding;
	//数据源编码
	private String DSEncoding;
	//是否显示结果集内容
	private boolean ShowResult;
	//数据库方言
	private Dialect dialect;
	
	/*
	 * 构造函数
	 */
	public SQLQuery( String DSName,ConnectionPool cp,String DBEncoding,String DSEncoding,boolean ShowResult,Dialect dialect ) throws SQLException 
	{
		this.DSName = DSName;
		this.cp = cp;
		this.DBEncoding = DBEncoding;
		this.DSEncoding = DSEncoding;
		this.ShowResult = ShowResult;
		this.dialect = dialect;
	}
	
	public List ListQuery( String SQL ) throws SQLException
	{
		//计时开始
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		//获得查询结果集
		Statement stmt = null;
		ResultSet rs = null;
		List rsData = null;
		try
		{
			stmt = cp.createStatement();
			while( stmt == null )
			{
				stmt = cp.createStatement();
			}
			rs = stmt.executeQuery( SQL );
			
			if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
			{
				rsData = DataTool.rsToList( rs, DSEncoding, DBEncoding );
			}else
			{
				rsData = DataTool.rsToList( rs );
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			if(rs !=null){
				rs.close();
				rs = null;
			}
			if(stmt != null){
				stmt.close();
				stmt = null;
			}
			
		}
		//显示结果集
		if( ShowResult )
		{
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.size() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
	}
	
	public String[][] ArrayQuery( String SQL ) throws SQLException
	{
		//计时开始
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		//获得查询结果集
		Statement stmt = null;
		ResultSet rs = null;
		String [][] rsData = null;
		try
		{
			stmt = cp.createStatement();
			while( stmt == null )
			{
				stmt = cp.createStatement();
			}
			rs = stmt.executeQuery( SQL );
			
			rsData = DataTool.rsToArray( rs);
			
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			sqle.printStackTrace();
			throw new SQLException(sqle.getMessage());
		}finally
		{
			if(rs != null){
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			}
		}
		//显示结果集
		if( ShowResult )
		{
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.length + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
		
	}
    
    public Object[][] ArrayMetaQuery( String SQL ) throws SQLException
    {
        //计时开始
        long beginTime = 0;
        if( ShowResult )
        {
            beginTime = System.currentTimeMillis();
        }
        //获得查询结果集
        Statement stmt = null;
        ResultSet rs = null;
        Object [][] rsData = null;
        try
        {
            stmt = cp.createStatement();
            while( stmt == null )
            {
                stmt = cp.createStatement();
            }
            rs = stmt.executeQuery( SQL );
            
            rsData = DataTool.rsToMetaArray( rs);
            
        }catch(SQLException sqle)
        {
            logger.info( "查询出错: " + sqle.getMessage() );
            throw new SQLException(sqle.getMessage());
        }finally
        {
            if(rs != null){
            rs.close();
            rs = null;
            stmt.close();
            stmt = null;
            }
        }
        //显示结果集
        if( ShowResult )
        {
            long endTime = System.currentTimeMillis();
            logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.length + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
            //DataTool.showListContent( rsData );
        }
        return rsData;
        
    }
	
	public Document xmlQuery( String SQL ) throws SQLException
	{
		//计时开始
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		//获得查询结果集
		Statement stmt = null;
		ResultSet rs = null;
		Document rsData = null;
		try
		{
			stmt = cp.createStatement();
			while( stmt == null )
			{
				stmt = cp.createStatement();
			}
			rs = stmt.executeQuery( SQL );
			
			rsData = DataTool.rsToXml( rs);
			
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw new SQLException(sqle.getMessage());
		}finally
		{
			if(rs != null){
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			}
		}
		//显示结果集
		if( ShowResult )
		{
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:xml [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
		
	}
    
	
	public Document xmlTableQuery( String SQL ) throws SQLException
	{
		//计时开始
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		//获得查询结果集
		Statement stmt = null;
		ResultSet rs = null;
		Document rsData = null;
		try
		{
			stmt = cp.createStatement();
			while( stmt == null )
			{
				stmt = cp.createStatement();
			}
			rs = stmt.executeQuery( SQL );
			
			rsData = DataTool.rsToTableXml(rs);
			
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw new SQLException(sqle.getMessage());
		}finally
		{
			if(rs != null){
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
			}
		}
		//显示结果集
		if( ShowResult )
		{
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:xml [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
		
	}
	/*
	 * 方法同上，addKH判断返回字符型是否加''
	 */
	public List ListQuery( String SQL, boolean addKH) throws SQLException
	{
		//计时开始
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		//获得查询结果集
		Statement stmt = null;
		ResultSet rs = null;
		List rsData = null;
		try
		{
			stmt = cp.createStatement();
			while( stmt == null )
			{
				stmt = cp.createStatement();
			}
			rs = stmt.executeQuery( SQL );
			
			if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
			{
				rsData = DataTool.rsToList( rs, DSEncoding, DBEncoding, addKH);
			}else
			{
				rsData = DataTool.rsToList( rs, addKH);
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}
		//显示结果集
		if( ShowResult )
		{
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.size() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
	}
	public List ListPageQuery( String SQL, int pageNumber, int pageSize ) throws SQLException
	{
		//计时开始
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		int startIndex = ( pageNumber - 1 ) * pageSize;
		SQL = dialect.getPageString( SQL, startIndex, pageSize );
		//获得查询结果集
		Statement stmt = null;
		ResultSet rs = null;
		List rsData = null;
		boolean pageLimitSupport = dialect.supportPageLimit();
		try
		{
			if( pageLimitSupport )
			{
				stmt = cp.createStatement();
			}else
			{
				stmt = cp.createStatement(rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY );
			}
			rs = stmt.executeQuery( SQL );
			if( pageLimitSupport )
			{
				if( startIndex > 0  )
				{
					rs.absolute( startIndex );
				}
				if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
				{
					rsData = DataTool.rsToList( rs, pageSize, DSEncoding, DBEncoding );
				}else
				{
					rsData = DataTool.rsToList( rs, pageSize );
				}
			}else
			{
				if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
				{
					rsData = DataTool.rsToList( rs,pageSize, DSEncoding, DBEncoding );
				}else
				{
					rsData = DataTool.rsToList( rs,pageSize );
				}
			}
		}catch(SQLException sqle)
		{
			logger.info( "分页查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//结束计时
			long endTime = System.currentTimeMillis();
			System.out.println( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.size() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
	}
	
	public String[][] ArrayPageQuery( String SQL, int pageNumber, int pageSize ) throws SQLException
	{
//		计时开始
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		int startIndex = ( pageNumber - 1 ) * pageSize;
		SQL = dialect.getPageString( SQL, startIndex, pageSize );
		//获得查询结果集
		Statement stmt = null;
		ResultSet rs = null;
		String [][] rsData = null;
		boolean pageLimitSupport = dialect.supportPageLimit();
		try
		{
			if( pageLimitSupport )
			{
				stmt = cp.createStatement();
			}else
			{
				stmt = cp.createStatement(rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY );
			}
			rs = stmt.executeQuery( SQL );
			if( pageLimitSupport )
			{
				if( startIndex > 0  )
				{
					rs.absolute( startIndex );
				}
				rsData = DataTool.rsToArray( rs, pageNumber,pageSize );
				
			}else
			{
				rsData = DataTool.rsToArray( rs ,pageNumber,pageSize);
				
			}
		}catch(SQLException sqle)
		{
			logger.info( "分页查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//结束计时
			long endTime = System.currentTimeMillis();
			System.out.println( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.length + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
	}
	
	public LinkedList LinkedListQuery( String SQL ) throws SQLException
	{
		//计时开始
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		//获得查询结果集
		Statement stmt = null;
		ResultSet rs = null;
		LinkedList rsData = null;
		try
		{
			stmt = cp.createStatement();
			rs = stmt.executeQuery( SQL );
			
			if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
			{
				rsData = DataTool.rsToLinkedList( rs, DSEncoding, DBEncoding );
			}else
			{
				rsData = DataTool.rsToLinkedList( rs );
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//结束计时
			long endTime = System.currentTimeMillis();
			System.out.println( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.size() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
	}
	
	public MyDataTable DataTableQuery( String SQL ) throws SQLException
	{
		//计时开始
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		//获得查询结果集
		Statement stmt = null;
		ResultSet rs = null;
		MyDataTable mdt = null;
		try
		{
			stmt = cp.createStatement();
			rs = stmt.executeQuery( SQL );
			mdt = new MyDataTable( rs, DSEncoding, DBEncoding );
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//结束计时
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + mdt.getRows() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
		}
		return mdt;
	}
	
	public MyDataTable DataTablePageQuery( String SQL, int pageNumber, int pageSize ) throws SQLException
	{//页号从1开始
		//计时开始
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		int startIndex = ( pageNumber - 1 ) * pageSize;
		SQL = dialect.getPageString( SQL,startIndex,pageSize );
		//获得查询结果集
		Statement stmt = null;
		ResultSet rs = null;
		MyDataTable mdt = null;
		boolean pageLimitSupport = dialect.supportPageLimit();
		try
		{
			if( !pageLimitSupport )
			{
				stmt = cp.createStatement( rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY );
			}else
			{
				stmt = cp.createStatement();
			}
			rs = stmt.executeQuery( SQL );
			if( !pageLimitSupport )
			{
				if( startIndex > 0 )
				{
					rs.absolute( startIndex );
				}
				mdt = new MyDataTable( rs, pageSize, DSEncoding, DBEncoding );
			}else
			{
				mdt = new MyDataTable( rs, DSEncoding, DBEncoding );
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//结束计时
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + mdt.getRows() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
		}
		return mdt;
	}
	
	public int getIntResult( String SQL ) throws SQLException
	{
		int number = 0;
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = cp.createStatement();
			rs = stmt.executeQuery( SQL );
			if( rs.next() )
			{
				number = rs.getInt(1);
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//结束计时
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//logger.info( "数字结果:" + number );
		}
		return number;
	}
	
	public String getStringResult( String SQL ) throws SQLException
	{
		String str = null;
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		Statement stmt = null;
		ResultSet rs = null;

		try
		{
			stmt = cp.createStatement();
			rs = stmt.executeQuery( SQL );
			if( rs.next() )
			{
				str = rs.getString(1);
				if( str == null )
				{
					str = "";
				}else
				{
					if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
					{
						try
						{
							str = new String( str.getBytes( DSEncoding ), DBEncoding ).trim();
						} catch (UnsupportedEncodingException e)
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//结束计时
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//logger.info( "数字结果:" + str );
		}
		return str;
	}
	
	public HashMap getSingleRowResult( String SQL ) throws SQLException
	{
		//开始计时
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		Statement stmt = null;
		ResultSet rs = null;
		HashMap singleRow = null;
		try
		{
			stmt = cp.createStatement();
			rs = stmt.executeQuery( SQL );
			singleRow = DataTool.rsToMap( rs, DSEncoding, DBEncoding );
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//时间统计
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showMapContent( singleRow );
		}
		return singleRow;
	}
	
	public HashMap getSingleRowPrepareResult( String SQL, Object[] object ) throws SQLException
	{
		//开始计时
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		HashMap singleRow = null;
		try
		{
			ps = cp.prepareStatement( SQL );
			for( int i = 0; i < object.length; i ++ )
			{
				ps.setObject( i+1,object[i] );
			}
			rs = ps.executeQuery();
			singleRow = DataTool.rsToMap( rs );
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//时间统计
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showMapContent( singleRow );
		}
		return singleRow;
	}
	
	public MyDataRow getDataRowResult( String SQL ) throws SQLException
	{
		//开始计时
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		Statement stmt = null;
		ResultSet rs = null;
		MyDataRow row = null;
		try
		{
			stmt = cp.createStatement();
			rs = stmt.executeQuery( SQL );
			row = new MyDataRow( rs );
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			stmt.close();
			stmt = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//时间统计
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
		}
		return row;
	}

	public List ListPrepareQuery( String SQL, Object [] object ) throws SQLException
	{
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		List rsData = null;
		try
		{
			ps = cp.prepareStatement( SQL );
			
			for( int i = 0; i < object.length; i ++ )
			{
				ps.setObject( i+1, object[i] );
			}
			rs = ps.executeQuery();
			
			if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
			{
				rsData = DataTool.rsToList( rs, DSEncoding, DBEncoding );
			}else
			{
				rsData = DataTool.rsToList( rs );
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//时间统计
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.size() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
	}
	
	public LinkedList LinkedListPrepareQuery( String SQL, Object [] object ) throws SQLException
	{
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		LinkedList rsData = null;
		try
		{
			ps = cp.prepareStatement( SQL );
			for( int i = 0; i < object.length; i ++ )
			{
				ps.setObject( i+1, object[i] );
			}
			rs = ps.executeQuery();
			
			if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
			{
				rsData = DataTool.rsToLinkedList( rs, DSEncoding, DBEncoding );
			}else
			{
				rsData = DataTool.rsToLinkedList( rs );
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//时间统计
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.size() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
	}
	
	public LinkedList LinkedListPreparePageQuery( String SQL, Object [] object, int pageNumber, int pageSize ) throws SQLException
	{
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		int startIndex = ( pageNumber - 1 ) * pageSize;
		PreparedStatement ps = null;
		ResultSet rs = null;
		LinkedList rsData = null;
		boolean pageLimitSupport = dialect.supportPageLimit();
		try
		{
			if( pageLimitSupport )
			{
				ps = cp.prepareStatement( SQL );
			}else
			{
				ps = cp.prepareStatement( SQL, rs.TYPE_SCROLL_INSENSITIVE,rs.CONCUR_READ_ONLY );
			}
			
			for( int i = 0; i < object.length; i ++ )
			{
				ps.setObject( i+1, object[i] );
			}
			rs = ps.executeQuery();
			if( pageLimitSupport )
			{
				if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
				{
					rsData = DataTool.rsToLinkedList( rs, DSEncoding, DBEncoding );
				}else
				{
					rsData = DataTool.rsToLinkedList( rs );
				}
			}else
			{
				if( startIndex > 0 )
				{
					rs.absolute( startIndex );
				}
				if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
				{
					rsData = DataTool.rsToLinkedList( rs, pageSize, DSEncoding, DBEncoding );
				}else
				{
					rsData = DataTool.rsToLinkedList( rs, pageSize );
				}
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//时间统计
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.size() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
	}
	
	public List ListPreparePageQuery( String SQL, Object [] object, int pageNumber, int pageSize ) throws SQLException
	{
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		int startIndex = ( pageNumber - 1 ) * pageSize;
		SQL = dialect.getPageString( SQL, startIndex, pageSize );
		
		PreparedStatement ps = null;
		ResultSet rs = null;
		List rsData = null;
		boolean pageLimitSupport = dialect.supportPageLimit();
		
		try
		{
			if( pageLimitSupport )
			{
				ps = cp.prepareStatement( SQL );
			}else
			{
				ps = cp.prepareStatement( SQL, rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY );
			}
			
			for( int i = 0; i < object.length; i ++ )
			{
				ps.setObject( i+1, object[i] );
			}
			rs = ps.executeQuery();
			if( pageLimitSupport )
			{
				if( startIndex > 0 )
				{
					rs.absolute( startIndex );
				}
				if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
				{
					rsData = DataTool.rsToList( rs, pageSize, DSEncoding, DBEncoding );
				}else
				{
					rsData = DataTool.rsToList( rs, pageSize );
				}
			}else
			{
				if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
				{
					rsData = DataTool.rsToList( rs, DSEncoding, DBEncoding );
				}else
				{
					rsData = DataTool.rsToList( rs );
				}
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//时间统计
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.size() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//DataTool.showListContent( rsData );
		}
		return rsData;
	}
	
	public MyDataTable DataTablePrepareQuery( String SQL, Object [] object ) throws SQLException
	{
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		MyDataTable mdt = null;
		try
		{
			ps = cp.prepareStatement( SQL );
			for( int i = 0; i < object.length; i ++ )
			{
				ps.setObject( i+1, object[i] );
			}
			rs = ps.executeQuery();
			mdt = new MyDataTable( rs, DSEncoding, DBEncoding ); 
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//时间统计
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + mdt.getRows() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
		}
		return mdt;
	}
	
	public MyDataTable DataTablePreparePageQuery( String SQL, Object [] object, int pageNumber, int pageSize ) throws SQLException
	{
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		MyDataTable mdt = null;
		boolean pageLimitSupport = dialect.supportPageLimit();
		try
		{
			if( pageLimitSupport )
			{
				ps = cp.prepareStatement( SQL );
			}else
			{
				ps = cp.prepareStatement( SQL, rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY );
			}
			for( int i = 0; i < object.length; i ++ )
			{
				ps.setObject( i+1, object[i] );
			}
			rs = ps.executeQuery();
			if( pageLimitSupport )
			{
				mdt = new MyDataTable( rs, DSEncoding, DBEncoding ); 
			}else
			{
				mdt = new MyDataTable( rs, pageSize, DSEncoding, DBEncoding );
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//时间统计
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + mdt.getRows() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
		}
		return mdt;
	}
	
	public int getPrepareIntResult( String SQL, Object[] object ) throws SQLException
	{
		int number = 0;
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = cp.prepareStatement( SQL );
			for( int i = 0; i < object.length; i ++ )
			{
				ps.setObject( i+1,object[i] );
			}
			rs = ps.executeQuery();
		if( rs.next() )
		{
			number = rs.getInt(1);
		}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//结束计时
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//logger.info( "数字结果:" + number );
		}
		return number;
	}
	
	public String getPrepareStringResult( String SQL, Object[] object ) throws SQLException
	{
		String str = null;
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		PreparedStatement ps = null;
		ResultSet rs = null;
		
		try
		{
			ps = cp.prepareStatement( SQL );
			for( int i = 0; i < object.length; i ++ )
			{
				ps.setObject( i+1,object[i] );
			}
			rs = ps.executeQuery();
			if( rs.next() )
			{
				str = rs.getString(1);
				if( str == null )
				{
					str = "";
				}else
				{
					if( !DSEncoding.equalsIgnoreCase( DBEncoding ) )
					{
						try
						{
							str = new String( str.getBytes( DSEncoding ), DBEncoding ).trim();
						} catch (UnsupportedEncodingException e)
						{
							e.printStackTrace();
						}
					}
				}
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//结束计时
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
			//logger.info( "数字结果:" + str );
		}
		return str;
	}
	
	public MyDataRow getPrepareDataRowResult( String SQL, Object[] object ) throws SQLException
	{
		//开始计时
		long beginTime = 0;
		if( ShowResult )
		{
			beginTime = System.currentTimeMillis();
		}
		PreparedStatement ps =  null;
		ResultSet rs = null;
		MyDataRow mdr = null;
		try
		{
			ps = cp.prepareStatement( SQL );
			for( int i = 0; i < object.length; i ++ )
			{
				ps.setObject( i+1,object[i] );
			}
			rs = ps.executeQuery();
			mdr = new MyDataRow( rs );
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
			ps.close();
			ps = null;
		}
		//显示结果集
		if( ShowResult )
		{
			//时间统计
			long endTime = System.currentTimeMillis();
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
		}
		return mdr;
	}
	
	public String getDBEncoding()
	{
		return DBEncoding;
	}

	public String getDSEncoding()
	{
		return DSEncoding;
	}

	public String getDSName()
	{
		return DSName;
	}

	public boolean isShowResult()
	{
		return ShowResult;
	}
}
