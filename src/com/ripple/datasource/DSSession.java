package com.ripple.datasource;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Types;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.dom4j.Document;

import com.ripple.Constants;
import com.ripple.datasource.config.MyDataSource;
import com.ripple.datasource.dialect.DB2Dialect;
import com.ripple.datasource.dialect.Dialect;
import com.ripple.datasource.dialect.InformixDialect;
import com.ripple.datasource.dialect.MSSQLDialect;
import com.ripple.datasource.dialect.MySQLDialect;
import com.ripple.datasource.dialect.OracleDialect;
import com.ripple.datasource.util.DataTool;
import com.ripple.datasource.util.TimeTools;
import com.ripple.module.MyDataRow;
import com.ripple.module.MyDataTable;

public class DSSession
{
	private static Logger logger = LogManager.getLogger( DSSession.class );
	
	//数据源名称
	private String DSName;
	//数据库编码
	private String DBEncoding;
	//数据源编码
	private String DSEncoding;
	//是否显示结果集内容
	private boolean ShowResult;
	//数据库类型
	private Dialect dialect;
	//数据源连接
    private Connection connection;
    //设置的断点
    private Savepoint savePoint;
    //是否支持回滚
    private boolean supportsRollBack;
    //是否支持断点
    private boolean supportsSavePoints;
    
    public DSSession( MyDataSource mds ) throws SQLException
    {
		this.DSName = mds.getName();
		this.connection = mds.getConnectionPool().getConnection();
		this.DBEncoding = mds.getMyConnection().getDatabase().getEncoding();
		this.DSEncoding = mds.getEncoding();
		this.ShowResult = mds.showResult;
		this.dialect = getDBDialect( mds.getDialect() );
		this.supportsRollBack = dialect.supportsCommitAndRollback();
		this.supportsSavePoints = dialect.supportsSavePoints();
    }
	
	public DSSession( String DSName,Connection conn,String DBEncoding,String DSEncoding,boolean ShowResult,Dialect dialet ) throws SQLException
	{
		this.DSName = DSName;
		this.connection = conn;
		this.DBEncoding = DBEncoding;
		this.DSEncoding = DSEncoding;
		this.ShowResult = ShowResult;
		this.dialect = dialet;
		this.supportsRollBack = dialect.supportsCommitAndRollback();
		this.supportsSavePoints = dialect.supportsSavePoints();
	}
	
	private Dialect getDBDialect( String dialectType ) throws SQLException
	{
		if( dialectType == null )
		{
			throw new SQLException("不正确的数据源类型！"); 
		}
		Dialect dialect = null;
		String type = dialectType.toLowerCase();
		if( type .equals( Constants.DATABASE_ORACLE ) )
		{
			dialect = new OracleDialect();
		}else if( type.equals( Constants.DATABASE_MYSQL ))
		{
			dialect = new MySQLDialect();
		}else if( type.equals( Constants.DATABASE_DB2 ))
		{
			dialect = new DB2Dialect();
		}else if( type.equals( Constants.DATABASE_INFORMIX ))
		{
			dialect = new InformixDialect();
		}else if (type.equals(Constants.DATABASE_SQLSERVER))
		{
			dialect = new MSSQLDialect();
		}
		return dialect;
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
			stmt = connection.createStatement();
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
			stmt = connection.createStatement();
			rs = stmt.executeQuery( SQL );
			rsData = DataTool.rsToXml( rs );
			
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
			logger.info( "[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:xml" + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
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
				stmt = connection.createStatement();
			}else
			{
				stmt = connection.createStatement(rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY );
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
					rsData = DataTool.rsToList( rs, DSEncoding, DBEncoding );
				}else
				{
					rsData = DataTool.rsToList( rs );
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
			stmt = connection.createStatement();
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
			logger.info("[数据源]:" + DSName + " [SQL语句]: " + SQL + "[查询记录数]:" + rsData.size() + " [查询耗时]: " + TimeTools.getPeriodOfTime(beginTime,endTime) );
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
			stmt = connection.createStatement();
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
				stmt = connection.createStatement( rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY );
			}else
			{
				stmt = connection.createStatement();
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
			stmt = connection.createStatement();
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
			stmt = connection.createStatement();
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
			stmt = connection.createStatement();
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
			ps = connection.prepareStatement( SQL );
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
			stmt = connection.createStatement();
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
			ps = connection.prepareStatement( SQL );
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
			ps = connection.prepareStatement( SQL );
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
				ps = connection.prepareStatement( SQL );
			}else
			{
				ps = connection.prepareStatement( SQL, rs.TYPE_SCROLL_INSENSITIVE,rs.CONCUR_READ_ONLY );
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
			ps = connection.prepareStatement( SQL );
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
				ps = connection.prepareStatement( SQL );
			}else
			{
				ps = connection.prepareStatement( SQL, rs.TYPE_SCROLL_INSENSITIVE, rs.CONCUR_READ_ONLY );
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
			ps = connection.prepareStatement( SQL );
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
			ps = connection.prepareStatement( SQL );
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
			ps = connection.prepareStatement( SQL );
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
	
	public int executeUpdate( String SQL ) throws SQLException
	{
		int result = 0;
		if( ShowResult )
		{
			logger.info( "[SQL语句]: " + SQL );
		}
		Statement stmt = null;
		try
		{
			stmt = connection.createStatement();
			result = stmt.executeUpdate( SQL );
		}catch(SQLException sqle)
		{
			logger.info( "更新出错: " + sqle.getMessage() );
			throw sqle;
		}finally
        {
			stmt.close();
			stmt = null;			
		}
		return result;
	}
	
	public int executePrepareUpdate( String SQL, Object[] object ) throws SQLException
	{
		int result = 0;
		if( ShowResult )
		{
			logger.info( "[SQL语句]: " + SQL );
		}
		PreparedStatement ps = null;
		try
		{
			ps = connection.prepareStatement( SQL );
			for( int i = 0; i < object.length; i ++ )
			{
				if (object[i] == null)
				{
					ps.setObject(i+1,null,Types.VARCHAR);
				}else
				{
				ps.setObject( i+1,object[i] );
				}
			}
			result = ps.executeUpdate();
		}catch(SQLException sqle)
		{
			logger.info( "更新出错: " + sqle.getMessage() );
            throw sqle;
		}finally
        {
			ps.close();
			ps = null;			
		}
		return result;
	}
	
	public int executeBatchPrepareUpdate( String SQL, LinkedList prepareParams ) throws SQLException
	{
		int result = 0;
		if( ShowResult )
		{
			logger.info( "[SQL语句]: " + SQL );
		}
		PreparedStatement ps = null;
		try
		{
			ps = connection.prepareStatement( SQL );
			for( int j=0; j < prepareParams.size(); j ++ )
			{
				LinkedHashMap lhm = (LinkedHashMap) prepareParams.get( j );
				//lhm.remove( "ROWNUMBER_" );
				Iterator it = lhm.keySet().iterator();
				for( int i=0; it.hasNext(); i ++ )
				{
					Object obj = it.next();
					ps.setObject( i+1, lhm.get( obj ) );
				}
				ps.addBatch();
			}
			result = ps.executeUpdate();
		}catch( SQLException sqle )
		{
			logger.info( "更新出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			ps.close();
			ps = null;
		}
		return result;
	}
	
	public int executeCall( String SQL ) throws SQLException
	{
		int result = 0;
		if( ShowResult )
		{
			logger.info( "[SQL语句]: " + SQL );
		}
		CallableStatement stmt = null;
		try
		{
			stmt = connection.prepareCall( SQL );
			if( stmt.execute() ){
				result = 1;
			}
		}catch(SQLException sqle)
		{
			logger.info( "更新出错: " + sqle.getMessage() );
			throw sqle;
		}finally
        {
			stmt.close();
			stmt = null;			
		}
		return result;
	}
	
	public LinkedList getTableColumns( String schemaName, String tableName ) throws SQLException
	{
		LinkedList columns = new LinkedList();
		ResultSet rs = null;
		try
		{
			DatabaseMetaData dmd = connection.getMetaData();
			rs = dmd.getColumns(null, schemaName, tableName.toUpperCase(), null);
			while ( rs.next() )
			{
				columns.add( rs.getString("COLUMN_NAME") );
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
		}
		return columns;
	}
	
	public LinkedList getTableALLColumns( String schemaName, String tableName ) throws SQLException
	{
		LinkedList columns = new LinkedList();
		ResultSet rs = null;
		try
		{
			DatabaseMetaData dmd = connection.getMetaData();
			rs = dmd.getColumns(null, schemaName, tableName.toUpperCase(), null);
			while ( rs.next() )
			{
				LinkedHashMap lhm = new LinkedHashMap();
				lhm.put( "COLUMN_NAME",rs.getString("COLUMN_NAME") );
				lhm.put( "DATA_TYPE",rs.getString("DATA_TYPE") );
				lhm.put( "TYPE_NAME",rs.getString("TYPE_NAME") );
				columns.add( lhm );
			}
		}catch(SQLException sqle)
		{
			logger.info( "查询出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			rs.close();
			rs = null;
		}
		return columns;
	}
	
	public LinkedList getTablePrimaryKey(String catalog,String schema,String tablename) throws SQLException
	{
		LinkedList key = new LinkedList();
		ResultSet rs = null;
		try
		{
			DatabaseMetaData dmd = connection.getMetaData();
			rs = dmd.getPrimaryKeys(catalog,schema,tablename);
			if( rs != null )
			{
				while( rs.next() )
				{
					LinkedHashMap lhm = new LinkedHashMap();
					lhm.put("TABLE_CAT",rs.getObject(1));
					lhm.put("TABLE_SCHEM",rs.getObject(2));
					lhm.put("TABLE_NAME",rs.getObject(3));
					lhm.put("COLUMN_NAME",rs.getObject(4));
					lhm.put("KEY_SEQ",rs.getObject(5));
					lhm.put("PK_NAME",rs.getObject(6));
					key.add( lhm );
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
		}
		return key;
	}
	  
	public void beginTransaction() throws SQLException
	{
		if( supportsSavePoints )
		{
			savePoint = connection.setSavepoint();
		}else
		{
			connection.setAutoCommit( false );
		}
	}
	
	//在事务结束时提交事务并关闭连接
	public void endTransaction() throws SQLException
	{
		connection.commit();
	}
	
	//事务回滚
    public void rollback()
    {
        try 
        {
        	if ( supportsSavePoints )
        	{
        	   connection.rollback( savePoint );
        	}else if( supportsRollBack )
        	{
               connection.rollback();
        	}
        }catch (SQLException e) 
        {
            logger.error( "事务回滚出错: " + e.getMessage() );
        }
    }
    
    //关闭连接
	public void close() throws SQLException
	{
		if( connection != null )
		{
			connection.close();
		}
	}
    
    public void lockTable( String tableName ) throws SQLException
    {
        String sql = dialect.getLockTableString( tableName );
        if( sql != null )
        {
        	executeUpdate( sql );
        }
    }
    
    public void unlockTable() throws SQLException
    {
        String sql = dialect.getUnLockTableString();
        if( sql != null )
        {
            executeUpdate( sql );
        }
    }
    
    public boolean getConnnectionStatus() throws SQLException
    {
        return connection.getAutoCommit();
    }
}
