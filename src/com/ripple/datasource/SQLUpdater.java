package com.ripple.datasource;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ripple.datasource.connection.ConnectionPool;
import com.ripple.datasource.dialect.Dialect;
/*
 执行INSERT、UPDATE、DELETE及DDL（数据定义语言）语句
 返回值是一个整数,表示它执行的SQL语句所影响的数据库中的表的行数（更新计数）
*/
public class SQLUpdater
{
	private static Logger logger = LogManager.getLogger( SQLUpdater.class );
	
	//数据源名称
	private String DSName;
	//数据库编码
	//private String DBEncoding;
	//数据源编码
	//private String DSEncoding;
	//数据库类型
	//private Dialect dialect;
	//数据库连接
	private ConnectionPool cp;
	//是否显示结果
	private boolean ShowResult;
	
	public SQLUpdater( String DSName,ConnectionPool cp,String DBEncoding,String DSEncoding,boolean ShowResult,Dialect dialect )  
	{
		this.DSName = DSName;
		this.cp = cp;
		//this.DBEncoding = DBEncoding;
		//this.DSEncoding = DSEncoding;
		//this.dialect = dialect;
		this.ShowResult = ShowResult;
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
			stmt = cp.createStatement();
			result = stmt.executeUpdate( SQL );
		}catch(SQLException sqle)
		{
			logger.info("更新出错: " + sqle.getMessage() );
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
			ps = cp.prepareStatement( SQL );
			for( int i = 0; i < object.length; i ++ )
			{
				if(object[i] == null)
				{
					ps.setObject(i+1, null,Types.VARCHAR);
				}else
				{
					ps.setObject( i+1,object[i] );
				}
				
			}
			result = ps.executeUpdate();
		}catch(SQLException sqle)
		{
			logger.info("更新出错: " + sqle.getMessage() );
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
			
			stmt = cp.prepareCall( SQL );
			if( stmt.execute() ){
				result = 1;
			}
			
			
			
		}catch(SQLException sqle)
		{
			logger.info("更新出错: " + sqle.getMessage() );
			throw sqle;
		}finally
		{
			stmt.close();
			stmt = null;
		}
		return result;
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
