package com.ripple.datasource.config;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.collections.FastArrayList;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.ripple.datasource.DSManager;
import com.ripple.datasource.SQLQuery;

public class Configuration implements Serializable
{

	private static final long	serialVersionUID	= 1L;
	
	private static Logger logger = LogManager.getLogger( Configuration.class );
	/**
	 * 读XML配置文件信息
	 * @param configFile
	 * @return List
	 * @throws ConfigurationException
	 */
	public List readFileConfig( String configFile ) throws ConfigurationException
	{
		List dataSources = new FastArrayList();
		XMLConfiguration config = new XMLConfiguration( configFile );
		//get datasources
		List dss = config.getList("datasources.datasource[@name]");

		for( int i = 0; i < dss.size(); i ++ )
		{
			
			MyDataSource dataSource = new MyDataSource();
			//get datasource name
			dataSource.setName(config.getString("datasources.datasource("+i+")[@name]"));
			//get datasource property
			List propertys = config.getList("datasources.datasource("+i+").property");
			//get datasource property
			for( int k = 0; k < propertys.size(); k ++ )
			{
				String property_name = config.getString("datasources.datasource("+i+").property("+k+")[@name]");
				String value_key = "datasources.datasource("+i+").property("+k+")";
				
				if( property_name.equals( "encoding" ) )
				{
					dataSource.setEncoding( config.getString( value_key,"ISO-8859-1" ) );
				}else if( property_name.equals( "read_only" ) )
				{
					dataSource.readOnly = config.getBoolean( value_key,false );
				}else if( property_name.equals( "show_result" ) )
				{
					dataSource.showResult = config.getBoolean( value_key,false );
				}else if( property_name.equals( "dialect"))
				{
					dataSource.setDialect(config.getString(value_key, "Oracle"));
				}
			}
			//get connection property
			List conn_propertys = config.getList("datasources.datasource("+i+").connection.property");
			for( int k=0; k < conn_propertys.size(); k ++ )
			{
				String property_name = config.getString("datasources.datasource("+i+").connection.property("+k+")[@name]");
				String value_key = "datasources.datasource("+i+").connection.property("+k+")";
				MyConnection myconn = dataSource.getMyConnection();
				MyDatabase mydb = myconn.getDatabase();
				
				if( property_name.equals("conn_pool_size") )
				{
					myconn.setCoonnectionLevelPoolSize( config.getInt(value_key,0) );
				}else if( property_name.equals("stmt_pool_size") )
				{
					myconn.setStatementLevelPoolSize( config.getInt(value_key,0) );
				}else if( property_name.equals("driver") )
				{
					myconn.getDatabase().setDriverClass( config.getString(value_key,"") );
				}else if( property_name.equals("idle_timeout") )
				{
					myconn.setIdleTimeout( config.getInt( value_key ) );
				}else if( property_name.equals("shrink_interval") )
				{
					myconn.setShrinkInterval( config.getInt(value_key) );
				}else if ( property_name.equals("stmt_max_size"))
				{
					myconn.setStatementPoolSize( config.getInt( value_key, 200 ) );					
				}else if( property_name.equals("url") )
				{
					mydb.setConnectionURL( config.getString(value_key,"" ) );
				}else if ( property_name.equals("user") )
				{
					mydb.setUser( config.getString(value_key,"") );
				}else if ( property_name.equals("password") )
				{
					mydb.setPassword( config.getString( value_key, "") );
				}else if ( property_name.equals("encoding") )
				{
					mydb.setEncoding( config.getString( value_key, "GBK" ) );
				}
			}
			dataSources.add( dataSource );
		}
		return dataSources;
	}
	/**
	 * 读取数据库中的数据源配置信息
	 */
	public MyDataSource readDBConfig( String DSName ) throws SQLException
	{
		//获取指定数据源的配置信息
		SQLQuery query = DSManager.getSQLQuery();
		String sql = "SELECT c_ds_mng.* FROM c_ds_mng WHERE ds_name='"+DSName+"'";
		HashMap dsInfo = query.getSingleRowResult( sql );
		MyDataSource dataSource = null;
		if( dsInfo != null )
		{
			dataSource = new MyDataSource();
			dataSource.setName( getStringParam( dsInfo, "DS_NAME", "" ) );
			dataSource.setEncoding( getStringParam( dsInfo, "DS_ENCODING", "ISO-8859-1"));
			dataSource.setReadOnly( false );
			dataSource.setShowResult( false );
			MyConnection myconn = dataSource.getMyConnection();
			myconn.setCoonnectionLevelPoolSize( getIntParam( dsInfo, "CONN_LEVEL_SIZE", 2 ) );
			myconn.setStatementLevelPoolSize( getIntParam( dsInfo, "STMT_LEVEL_SIZE", 2 ) );
			myconn.getDatabase().setDriverClass( getStringParam( dsInfo, "MDB_DRIVER", "" ) );
			myconn.setIdleTimeout( getIntParam( dsInfo, "IDLE_TIMEOUT", 120 ) );
			myconn.setShrinkInterval( getIntParam( dsInfo, "SHRINK_INTERVAL", 100 ) );
			myconn.setStatementPoolSize( getIntParam( dsInfo, "STMT_MAX_SIZE", 200 ) );
			myconn.getDatabase().setConnectionURL( getDB2URL(getStringParam( dsInfo, "MDB_IP_ADDR", "" ),getIntParam( dsInfo, "MDB_PORT", 0 ),getStringParam( dsInfo, "MDB_NAME", "" ) ) );
			MyDatabase mydb = myconn.getDatabase();
			mydb.setUser( getStringParam( dsInfo, "MDB_USER", "" ) );
			mydb.setPassword( getStringParam( dsInfo, "MDB_USER_PWD","" ) );
			mydb.setEncoding( getStringParam( dsInfo, "DB_ENCODING","GBK" ) );
		}
		return dataSource;
	}
	
	private String getDB2URL(String host,int port,String db_name)
	{
		StringBuffer sb = new StringBuffer();
		sb.append( "jdbc:")
		  .append("db2:")
		  .append("//")
		  .append( host )
		  .append(":")
		  .append(port)
		  .append("/")
		  .append(db_name);
		return sb.toString();
	}
	
	private String getStringParam( HashMap info, String valueKey, String defaultValue )
	{
		String paramValue = null;
		Object value = info.get( valueKey );
		if( value == null || "".equals( value ) )
		{
			paramValue = defaultValue;
		}else
		{
			paramValue = value.toString().trim();
		}
		return paramValue;
	}
	
	private int getIntParam( HashMap info, String valueKey, int defaultValue )
	{
		int paramValue = 0;
		Object value = info.get( valueKey );
		if( value == null || "".equals( value ) )
		{
			paramValue = defaultValue;
		}else
		{
			paramValue = ((Integer)value).intValue();
		}
		return paramValue;
	}
}
