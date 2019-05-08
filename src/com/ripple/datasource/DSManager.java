package com.ripple.datasource;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Hashtable;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ripple.Constants;
import com.ripple.datasource.config.Configuration;
import com.ripple.datasource.config.MyDataSource;
import com.ripple.datasource.connection.ConnectionPool;
import com.ripple.datasource.dialect.DB2Dialect;
import com.ripple.datasource.dialect.Dialect;
import com.ripple.datasource.dialect.InformixDialect;
import com.ripple.datasource.dialect.MSSQLDialect;
import com.ripple.datasource.dialect.MySQLDialect;
import com.ripple.datasource.dialect.OracleDialect;

public class DSManager
{
	private static Logger logger = LogManager.getLogger( DSManager.class );
	
	//private static volatile Hashtable DSPools = new Hashtable();
	private static Hashtable DSPools = new Hashtable();
	
	private static String default_ds =  "local";
	
	/*
	 * 初始化数据源并帮定到数据源列表中
	 */
	public static void initDataSource( MyDataSource ds ) throws SQLException
	{
		boolean flag = false;
		if( ds == null )
		{
			throw new SQLException("数据源为空!");
		}
		//加载连接缓冲级的缓冲池
        ConnectionPool connectionPool = null;
        //加载会话缓冲级的缓冲池
        ConnectionPool statementPool = null;

		if( ds.getMyConnection().getCoonnectionLevelPoolSize() > 0 )
		{
			try
			{
				connectionPool = new ConnectionPool( ds.getName(),Constants.ConnectionLevel,ds.getMyConnection() );
				ds.setConnectionPool( connectionPool );
				if( connectionPool.isAutoShrink() )
		        {
		            Thread connPoolThread = new Thread( connectionPool );
		            connPoolThread.setDaemon(true);
		            connPoolThread.start();
		        }
			} catch (Exception e)
			{
				logger.info( "创建数据源[" + ds.getName() + "]连接池出错：" + e.getMessage() );
			}
		}

		if( ds.getMyConnection().getStatementLevelPoolSize() > 0 )
		{
			try
			{
				statementPool = new ConnectionPool( ds.getName(),Constants.StatementLevel,ds.getMyConnection() );
				ds.setStatementPool( statementPool );
		        if( statementPool.isAutoShrink() )
		        {
		            Thread stmtPoolThread = new Thread( statementPool );
		            stmtPoolThread.setDaemon(true);
		            stmtPoolThread.start();
		        }
			} catch (Exception e)
			{
				logger.info( "创建数据源[" + ds.getName() + "]会话池出错：" + e.getMessage() );
			}
		}
		
        if( connectionPool != null && statementPool != null )
        {
        	try 
        	{
				bindDataSource( ds );
				logger.info( "加载数据源[" + ds.getName() + "]成功！" );
			} catch (SQLException e) 
			{
				logger.info( "加载数据源[" + ds.getName() + "]失败！" );
				e.printStackTrace();
			}
        }else
        {
        	logger.info( "加载数据源[" + ds.getName() + "]失败！" );
        }
	}
	
	/*
	 * 在数据源池中添加一个数据源
	 */
	public static void bindDataSource( MyDataSource ds ) throws SQLException
	{
		if( ds == null )
		{
			throw new SQLException("数据源为空!");
		}
		String DSName = ds.getName();
		if( lookupDataSource( DSName ) == null )
		{
			DSPools.put( DSName, ds );
		}else
		{
			throw new SQLException("数据源[" + ds.getName() + "]已经存在!");
		}
	}
	/*
	 * 释放数据源 synchronized
	 */
	public static void unBindDataSource( String DSName ) throws SQLException
	{
		MyDataSource ds = lookupDataSource( DSName );
		if( ds != null )
		{
			try
			{
				//关闭所有的连接
				ds.getConnectionPool().close();
				ds.getStatementPool().close();
			} catch ( SQLException e )
			{
				logger.info( "释放数据源出错 : " + e.getMessage() );
			}
			if( DSPools.remove( DSName ) == null )
			{
				ds = null;
			}
		}else
		{
			throw new SQLException("数据源[" + DSName + "]没有找到!");
		}
	}
	
	/*
	 * 根据名称获得数据源
	 */
    public static MyDataSource lookupDataSource( String DSName )
    {
        return (MyDataSource)DSPools.get( DSName );
    }   
    /*
     * 获得数据库连接
     */
    public static Connection getConnection() throws SQLException
    {
    	if( default_ds == null )
    	{
    		throw new SQLException("没有指定默认数据源！");
    	}
		MyDataSource dso = DSManager.lookupDataSource( default_ds );
		if( dso == null )
		{
			throw new SQLException("没有找到默认数据源配置！");
		}
		return dso.getConnectionPool().getConnection();
    }
    
    public static Connection getConnection( String DSName ) throws SQLException
    {
		MyDataSource dso = DSManager.lookupDataSource( DSName );
		if( dso == null )
		{
			Configuration cfg = new Configuration();
			dso = cfg.readDBConfig( DSName );
			if( dso != null )
			{
				initDataSource( dso );
			}else
			{
				throw new SQLException("没有找到默认数据源配置！");
			}
		}
		return dso.getConnectionPool().getConnection();
    }
    
    //-------------------------产生SQL操作对象--------------------------------
    /*
     * 获得默认的会话对象
     */
    public static DSSession getDSSession() throws SQLException
    {
    	if( default_ds == null )
    	{
    		throw new SQLException("没有指定默认数据源！");
    	}

    	MyDataSource dso = DSManager.lookupDataSource( default_ds );
    	Connection conn = dso.getConnectionPool().getConnection();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		boolean showResult = dso.showResult;
		String dialectStr = dso.getDialect();
		Dialect dialect = getDBDialect( dialectStr );
		
		return new DSSession( default_ds,conn,DBEncoding,DSEncoding,showResult,dialect );
    }
    /*
     * 获得指定数据源的会话对象
     */
    public static DSSession getDSSession( String DSName ) throws SQLException
    {
    	if( DSName == null )
    	{
    		throw new SQLException("没有指定数据源！");
    	}
		MyDataSource dso = DSManager.lookupDataSource( DSName );
		if( dso == null )
		{
			Configuration cfg = new Configuration();
			dso = cfg.readDBConfig( DSName );
			if( dso != null )
			{
				initDataSource( dso );
			}else
			{
				throw new SQLException("没有找到默认数据源配置！");
			}
		}
		
		Connection conn = dso.getConnectionPool().getConnection();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		boolean showResult = dso.showResult;
		String dialectStr = dso.getDialect();
		Dialect dialect = getDBDialect( dialectStr );	
		
		return new DSSession( DSName,conn,DBEncoding,DSEncoding,showResult,dialect );
    }
    /*
     * 获得默认更新对象
     */
    public static SQLUpdater getSQLUpdater() throws SQLException
    {
    	if( default_ds == null )
    	{
    		throw new SQLException("没有指定默认数据源！");
    	}
		MyDataSource dso = DSManager.lookupDataSource( default_ds );
		if( dso == null )
		{
			throw new SQLException("没有找到默认数据源配置！");
		}
		ConnectionPool cp = dso.getStatementPool();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		Dialect dialect = getDBDialect( dso.getDialect() );
		
		return new SQLUpdater( default_ds,cp,DBEncoding,DSEncoding,dso.showResult,dialect );
    }
    /*
     * 获得指定数据源的更新对象
     */
    public static SQLUpdater getSQLUpdater( String DSName ) throws SQLException
    {
    	if( DSName == null )
    	{
    		throw new SQLException("没有指定数据源！");
    	}
		MyDataSource dso = DSManager.lookupDataSource( DSName );
		//判断数据源是否已经加载，如果没有加载则开始初始化此数据源
		if( dso == null )
		{
			Configuration cfg = new Configuration();
			dso = cfg.readDBConfig( DSName );
			initDataSource( dso );
		}
		
		ConnectionPool cp = dso.getStatementPool();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		Dialect dialect = getDBDialect( dso.getDialect() );

		return new SQLUpdater( DSName,cp,DBEncoding,DSEncoding,dso.showResult,dialect );
    }
    /*
     * 获得默认的查找对象
     */
    public static SQLQuery getSQLQuery() throws SQLException
    {
    	if( default_ds == null )
    	{
    		throw new SQLException("没有指定默认数据源！");
    	}
		MyDataSource dso = DSManager.lookupDataSource( default_ds );
		if( dso == null )
		{
			throw new SQLException("没有找到默认数据源配置！");
		}
		ConnectionPool cp = dso.getStatementPool();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		Dialect dialect = getDBDialect( dso.getDialect() );
		
		return new SQLQuery( default_ds,cp,DBEncoding,DSEncoding,dso.showResult,dialect );
    }
    /*
     * 获得指定数据源的查询对象
     */
    public static SQLQuery getSQLQuery( String DSName ) throws SQLException
    {
    	if( DSName == null )
    	{
    		throw new SQLException("没有指定数据源！");
    	}
		MyDataSource dso = DSManager.lookupDataSource( DSName );
		if( dso == null )
		{
			Configuration cfg = new Configuration();
			dso = cfg.readDBConfig( DSName );
			initDataSource( dso );
		}
		ConnectionPool cp = dso.getStatementPool();
		String DBEncoding = dso.getMyConnection().getDatabase().getEncoding();
		String DSEncoding = dso.getEncoding();
		Dialect dialect = getDBDialect( dso.getDialect() );

		return new SQLQuery( DSName,cp,DBEncoding,DSEncoding,dso.showResult,dialect );
    }
	
	public static String getDefaultDS()
	{
		return default_ds;
	}
	
	public static void setDefaultDS( String ds )
	{
		default_ds = ds;
	}
	
	private static Dialect getDBDialect( String dialectType ) throws SQLException
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
		}else if ( type.equals(Constants.DATABASE_SQLSERVER))
		{
			dialect = new MSSQLDialect();
		}
		return dialect;
	}
}
