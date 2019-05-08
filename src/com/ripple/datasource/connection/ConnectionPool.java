package com.ripple.datasource.connection;

import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.ripple.Constants;
import com.ripple.datasource.config.MyConnection;
import com.ripple.datasource.config.MyDatabase;
import com.ripple.datasource.exception.PoolUnmatchedLevelException;

/*
 * 
 */
public class ConnectionPool extends PooledConnection implements Runnable
{
	private static Logger logger = Logger.getLogger( ConnectionPool.class );

    private boolean isBeenUsing;

    private String poolName;

    private PooledConnection pooledConnection[];

    private int curPos;

    private int connPoolSize;

    private int stmtPoolSize;

    private int shrinkInterval;

    private long createdTime;

    private long lastShrinkTime;

    private long idleTimeout;

    private String connStatus;

    private int poolLevel; 

    private String driverClass;

    private String dbUrl;

    private String dbUser;

    private String dbPass;

    public ConnectionPool( String DSName, int poolLevel, MyConnection co ) throws Exception
    {
    	this.isBeenUsing = false;
    	this.curPos = 0;
        this.idleTimeout = co.getIdleTimeout();
        this.stmtPoolSize = co.getStatementPoolSize();
        this.shrinkInterval = co.getShrinkInterval();
        this.createdTime = System.currentTimeMillis();
        this.lastShrinkTime = System.currentTimeMillis();
        this.connStatus = "no active";
        this.poolName = DSName;
        this.poolLevel = poolLevel;
        MyDatabase mydb = co.getDatabase();
        this.driverClass = mydb.getDriverClass();
        this.dbUrl = mydb.getConnectionURL();
        this.dbUser = mydb.getUser();
        this.dbPass = mydb.getPassword();
        
        if( poolLevel == Constants.ConnectionLevel)
        {
        	this.connPoolSize = co.getCoonnectionLevelPoolSize();
        }else if( poolLevel == Constants.StatementLevel )
        {
        	this.connPoolSize = co.getStatementLevelPoolSize();
        }
        if( poolName == null )
        {
        	throw new Exception("连接池的名称没有配置！");
        }
        if( driverClass == null )
        {
        	throw new Exception("数据源驱动程序没有配置！");
        }
        if( dbUrl == null )
        {
        	throw new Exception("数据源连接字符串没有配置！");
        }
        if( dbUser == null )
        {
        	throw new Exception("数据源登录用户没有配置！");
        }
        if( dbPass == null )
        {
        	throw new Exception("数据源登录密码没有配置！");
        }
        pooledConnection = new PooledConnection[connPoolSize];
        for( int i = 0; i < pooledConnection.length; i ++ )
        {
            pooledConnection[i] = null;
        }
        //注册数据库驱动
        Class.forName( driverClass );
    }

    /*
     * 获得当前活动的连接数量
     */
    private int getActiveConnectionNumber()
    {
        int number = 0;
        for(int i = 0; i < connPoolSize; i++ )
        {
            if( pooledConnection[i] != null	&& !pooledConnection[i].isTimeout()	&& !pooledConnection[i].isClosed())
            {
            	number ++;
            }
        }
        return number;
    }

    public void run()
    {
        if( isBeenUsing )
        {
            return;
        }
        isBeenUsing = true;
        if( poolLevel == Constants.ConnectionLevel)
        {
            poolLevelShrink();
        }else
        {
            stmtLevelShrink();
        }
    }

    public int poolLevelShrink()
    {
        long lastShrinkTime = System.currentTimeMillis();
        for( ; ; )
        {
            Thread.currentThread();
			try
			{
				Thread.sleep(1000L);
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
            if( System.currentTimeMillis() - lastShrinkTime >= (long)( shrinkInterval * 1000 ))
            {
                shrink();
                lastShrinkTime = System.currentTimeMillis();
            }
//            try
//            {
//                Connection conn = getConnection();
//                conn.close();
//            }catch(Exception e)
//            {
//            	logger.error( e.getMessage() );
//            }
        }
    }

    public int stmtLevelShrink()
    {
        for(;;)
        {
            try
            {
                Thread.currentThread();
                Thread.sleep( shrinkInterval * 1000 );
                shrink();
            }catch(InterruptedException e)
            {
                logger.error( e.getMessage() );
            }
        }
    }

    protected synchronized int shrink()
    {
    	//logger.info("连接池["+poolName+"],连接类型["+poolLevel+"] : 开始执行检查程序......");
        int activeCount = 0;
        long createdTime = 0L;
        int currentPosition = 0;

        lastShrinkTime = System.currentTimeMillis();
        for( int i = 0; i < connPoolSize; i ++ )
        {
            curPos = curPos % connPoolSize;
            if( pooledConnection[curPos] != null )
            {
                if( pooledConnection[curPos].isTimeout() || pooledConnection[curPos].isClosed() )
                {
                    try
                    {
                        pooledConnection[curPos].forceClose();
                    }catch(SQLException sqle)
                    {
                    	logger.info("关闭数据源 [" + poolName + "-" + curPos + "]出错: " + sqle.getMessage() );
                    }
                    pooledConnection[curPos] = null;
                } else
                {
                    pooledConnection[curPos].shrink();
                    if( pooledConnection[curPos].getCreatedTime() > createdTime )
                    {
                        createdTime = pooledConnection[curPos].getCreatedTime();
                        currentPosition = curPos;
                    }
                    activeCount++;
                }
            }
            curPos++;
        }
        curPos = currentPosition;
        //logger.info( new StringBuffer("连接池[").append(poolName).append("] : 连接池检查完毕, 此连接池中有 ").append(activeCount).append(" 个活动连接."));
        return activeCount;
    }

    private PooledConnection createPooledConnection() throws SQLException
    {
        connStatus = "";
        try
        {
        	return new PooledConnection(curPos, poolLevel, DriverManager.getConnection(dbUrl, dbUser, dbPass), poolName, stmtPoolSize, idleTimeout);
        }catch(SQLException e)
        {
            connStatus = e.getMessage();
            throw e;
        }
    }

    private void isSupportLevel(int level) throws PoolUnmatchedLevelException
    {
        if( level != poolLevel )
        {
            throw new PoolUnmatchedLevelException("current methoed is NOT supported in level:".concat(poolLevel()));
        }
    }

    public synchronized PooledConnection getConnection() throws SQLException
    {
        int curNum = -1;
        //isSupportLevel( Constants.ConnectionLevel );
        int count = 0;
        do
        {
	        for( int i = 0; i < connPoolSize; i ++ )
	        {
	            curPos = curPos % connPoolSize;
	            if( pooledConnection[curPos] == null )
	            {
	                if( curNum < 0 )
	                {
	                    curNum = curPos;
	                }
	            } else if( pooledConnection[curPos].isTimeout() || pooledConnection[curPos].isClosed() )
	            {
	                try
	                {
	                	pooledConnection[curPos].forceClose();
	                }catch(SQLException sqle)
	                {
	                	logger.info("关闭数据源 [" + poolName + "-" + curPos + "]出错: " + sqle.getMessage() );
	                }
	                pooledConnection[curPos] = null;
	                if( curNum < 0 )
	                {
	                    curNum = curPos;
	                }
	            }else if( ! pooledConnection[curPos].isBeenUsing() )
	            {
	            	pooledConnection[curPos].use();
	                return pooledConnection[curPos];
	            }
	            curPos++;
	        }
			try
			{
				Thread.currentThread();
				Thread.sleep( idleTimeout );
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			count ++;
        }while( curNum < 0 && count < 10 );
        
        if( curNum < 0 )
        {
        	throw new SQLException("数据源[" + poolName + "]连接已满!");
        }
        curPos = curNum;
        PooledConnection pc = null;
        try
		{
			pc = createPooledConnection();
			pc.use();
		} catch (SQLException sqle)
		{
			pc = null;
			throw new SQLException("新建数据源[ " + poolName + "-" + curPos + "]连接时出错:" + sqle.getMessage());
		}
		pooledConnection[curPos] = pc;
        return pc;
    }

    public synchronized Statement createStatement() throws SQLException
    {
        //isSupportLevel( Constants.StatementLevel );
        Statement currentStatement = null;
        int currentConnectionNumber = -1;
        int count = 0;
        while( currentConnectionNumber < 0 && count < 10 )
        {
		    for( int i = 0; i < connPoolSize; i ++ )
		    {
		        curPos = curPos % connPoolSize;
		        if( pooledConnection[curPos] == null )
		        {
		            if( currentConnectionNumber < 0 )
		            {
		                currentConnectionNumber = curPos;
		            }
		        }else if( pooledConnection[curPos].isTimeout() || pooledConnection[curPos].isClosed() )
		        {
		        	try
		            {
		                pooledConnection[curPos].forceClose();
		            }catch(SQLException sqle) 
		            {
		            	logger.info("关闭数据源 [" + poolName + "-" + curPos + "]出错: " + sqle.getMessage() );
		            }
		            pooledConnection[curPos] = null;
		            if(currentConnectionNumber < 0)
		            {
		                currentConnectionNumber = curPos;
		            }
		        }else if( pooledConnection[curPos].searchFreeCell() >= 0 )
		        {
		            try
		            {
		                currentStatement = pooledConnection[curPos].createStatement();
		            }catch(SQLException sqle)
		            {
		            	currentStatement = null;
		            	logger.info("新建数据源 [" + poolName + "-" + curPos + "]会话出错: " + sqle.getMessage() );
		            }
		            if(currentStatement != null)
		            {
		                return currentStatement;
		            }
		        }
		        curPos ++;
		    }
		    Thread.currentThread();
			try
			{
				Thread.sleep( idleTimeout );
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			count ++;
        }

        if( currentConnectionNumber < 0 )
        {
        	throw new SQLException("数据源[" + poolName + "]连接已满!");
        }
	    curPos = currentConnectionNumber;
	    try
	    {
	    	pooledConnection[curPos] = createPooledConnection();
	    	currentStatement = pooledConnection[curPos].createStatement();
	    }catch(SQLException sqle)
	    {
	    	currentStatement = null;
	    	throw new SQLException("新建数据源[ " + poolName + "-" + curPos + "]连接时出错:" + sqle.getMessage());
	    }
	    return currentStatement;
    }

    public synchronized Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException
    {
        //isSupportLevel( Constants.StatementLevel );
        Statement currentStatement = null;
        int currentConnectionNumber = -1;
        int count = 0;
        while( currentConnectionNumber < 0 && count < 10 )
        {
            for(int i = 0; i < connPoolSize; i ++ )
            {
                curPos = curPos % connPoolSize;
                if( pooledConnection[curPos] == null )
                {
                    if(currentConnectionNumber < 0)
                    {
                        currentConnectionNumber = curPos;
                    }
                } else if(pooledConnection[curPos].isTimeout() || pooledConnection[curPos].isClosed())
                {
                    try
                    {
                        pooledConnection[curPos].forceClose();
                    }catch(SQLException sqle)
                    { 
                    	logger.info("关闭数据源连接[" + poolName + "-" + curPos + "]出错: " + sqle.getMessage() );
                    }
                    pooledConnection[curPos] = null;
                    if(currentConnectionNumber < 0)
                    {
                        currentConnectionNumber = curPos;
                    }
                } else if(pooledConnection[curPos].searchFreeCell() >= 0 )
                {
                    try
                    {
                        currentStatement = pooledConnection[curPos].createStatement(resultSetType, resultSetConcurrency);
                    }catch(SQLException sqle)
                    {
                        currentStatement = null;
                        logger.info("新建数据源 [" + poolName + "-" + curPos + "]会话出错: " + sqle.getMessage() );
                    }
                    if(currentStatement != null)
                    {
                        return currentStatement;
                    }
                }
                curPos++;
            }
		    Thread.currentThread();
			try
			{
				Thread.sleep( idleTimeout );
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			count ++;
        }

        if( currentConnectionNumber < 0 )
        {
        	throw new SQLException("数据源[" + poolName + "]连接已满!");
        }
        curPos = currentConnectionNumber;
        try
        {
        	pooledConnection[curPos] = createPooledConnection();
        	currentStatement = pooledConnection[curPos].createStatement(resultSetType, resultSetConcurrency);
	    }catch(SQLException sqle)
	    {
	    	currentStatement = null;
	    	throw new SQLException("新建数据源[ " + poolName + "-" + curPos + "]连接时出错:" + sqle.getMessage());
	    }
        return currentStatement;
    }
    
    public synchronized PreparedStatement prepareStatement(String sql) throws SQLException
    {
        //isSupportLevel( Constants.StatementLevel );
        Statement currentStatement = null;
        int currentConnectionNumber = -1;
        int count = 0;
        while( currentConnectionNumber < 0 && count < 10 )
        {
            for(int i = 0; i < connPoolSize;i ++)
            {
                curPos = curPos % connPoolSize;
                if(pooledConnection[curPos] == null)
                {
                    if(currentConnectionNumber < 0)
                        currentConnectionNumber = curPos;
                } else if(pooledConnection[curPos].isTimeout() || pooledConnection[curPos].isClosed())
                {
                    try
                    {
                        pooledConnection[curPos].forceClose();
                    }catch(SQLException sqle)
                    { 
                    	logger.info("关闭数据源连接[" + poolName + "-" + curPos + "]出错: " + sqle.getMessage() );
                    }
                    pooledConnection[curPos] = null;
                    if(currentConnectionNumber < 0 )
                    {
                        currentConnectionNumber = curPos;
                    }
                } else if(pooledConnection[curPos].searchFreeCell() >= 0 )
                {
                    try
                    {
                        currentStatement = pooledConnection[curPos].prepareStatement(sql);
                    }catch(SQLException sqle)
                    {
                        currentStatement = null;
                        logger.info("新建数据源 [" + poolName + "-" + curPos + "]会话出错: " + sqle.getMessage() );
                    }
                    if(currentStatement != null)
                    {
                        return (PreparedStatement)currentStatement;
                    }
                }
                curPos++;
            }
		    Thread.currentThread();
			try
			{
				Thread.sleep( idleTimeout );
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			count ++;
        }

        if( currentConnectionNumber < 0 )
        {
        	throw new SQLException("数据源[" + poolName + "]连接已满!");
        }
        curPos = currentConnectionNumber;
        try
        {
        	pooledConnection[curPos] = createPooledConnection();
        	currentStatement = pooledConnection[curPos].prepareStatement(sql);
	    }catch(SQLException sqle)
	    {
	    	currentStatement = null;
	    	throw new SQLException("新建数据源[ " + poolName + "-" + curPos + "]连接时出错:" + sqle.getMessage());
	    }   
        return (PreparedStatement)currentStatement;
    }

    public synchronized CallableStatement prepareCall(String sql) throws SQLException
    {
        isSupportLevel( Constants.StatementLevel );
        Statement currentStatement = null;
        int currentConnectionNumber = -1;
        int count = 0;
        while( currentConnectionNumber < 0 && count < 10 )
        {
            for(int i = 0; i < connPoolSize; i ++)
            {
                curPos = curPos % connPoolSize;
                if(pooledConnection[curPos] == null)
                {
                    if(currentConnectionNumber < 0)
                    {
                        currentConnectionNumber = curPos;
                    }
                } else if(pooledConnection[curPos].isTimeout() || pooledConnection[curPos].isClosed())
                {
                    try
                    {
                        pooledConnection[curPos].forceClose();
                    } catch(SQLException sqle)
                    {
                    	logger.info("关闭数据源连接[" + poolName + "-" + curPos + "]出错: " + sqle.getMessage() );
                    }
                    pooledConnection[curPos] = null;
                    if(currentConnectionNumber < 0)
                    {
                        currentConnectionNumber = curPos;
                    }
                } else if(pooledConnection[curPos].searchFreeCell() >= 0)
                {
                    try
                    {
                        currentStatement = pooledConnection[curPos].prepareCall(sql);
                    }catch(SQLException sqle)
                    {
                        currentStatement = null;
                        logger.info("新建数据源 [" + poolName + "-" + curPos + "]会话出错: " + sqle.getMessage() );
                    }
                    if(currentStatement != null)
                    {
                        CallableStatement callablestatement1 = (CallableStatement)currentStatement;
                        return callablestatement1;
                    }
                }
                curPos++;
            }
		    Thread.currentThread();
			try
			{
				Thread.sleep( idleTimeout );
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			count ++;
        }

        if( currentConnectionNumber < 0 )
        {
        	throw new SQLException("数据源[" + poolName + "]连接已满!");
        }
       
        curPos = currentConnectionNumber;
        try
        {
        	pooledConnection[curPos] = createPooledConnection();
        	currentStatement = pooledConnection[curPos].prepareCall(sql);
	    }catch(SQLException sqle)
	    {
	    	currentStatement = null;
	    	throw new SQLException("新建数据源[ " + poolName + "-" + curPos + "]连接时出错:" + sqle.getMessage());
	    }
        return (CallableStatement)currentStatement;
    }

    public synchronized PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        //isSupportLevel( Constants.StatementLevel );
        Statement currentStatement = null;
        int currentConnectionNumber = -1;
        int count = 0;
        while( currentConnectionNumber < 0 && count < 10 )
        {
            for(int i = 0; i < connPoolSize; i ++ )
            {
                curPos = curPos % connPoolSize;
                if(pooledConnection[curPos] == null)
                {
                    if(currentConnectionNumber < 0)
                    {
                        currentConnectionNumber = curPos;
                    }
                } else if(pooledConnection[curPos].isTimeout() || pooledConnection[curPos].isClosed())
                {
                    try
                    {
                        pooledConnection[curPos].forceClose();
                    }catch(SQLException sqle)
                    {
                    	logger.info("关闭数据源连接[" + poolName + "-" + curPos + "]出错: " + sqle.getMessage() );
                    }
                    pooledConnection[curPos] = null;
                    if(currentConnectionNumber < 0)
                    {
                        currentConnectionNumber = curPos;
                    }
                } else if( pooledConnection[curPos].searchFreeCell() >= 0 )
                {
                    try
                    {
                        currentStatement = pooledConnection[curPos].prepareStatement(sql, resultSetType, resultSetConcurrency);
                    }
                    catch( SQLException sqle )
                    {
                        currentStatement = null;
                        logger.info("新建数据源 [" + poolName + "-" + curPos + "]会话出错: " + sqle.getMessage() );
                    }
                    if( currentStatement != null )
                    {
                        return (PreparedStatement)currentStatement;
                    }
                }
                curPos ++;
            }
		    Thread.currentThread();
			try
			{
				Thread.sleep( idleTimeout );
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			count ++;
        }
        
        if(currentConnectionNumber < 0)
        {
        	throw new SQLException("数据源[" + poolName + "]连接已满!");
        }
        curPos = currentConnectionNumber;
        try
        {
        	pooledConnection[curPos] = createPooledConnection();
        	currentStatement = pooledConnection[curPos].prepareStatement(sql, resultSetType, resultSetConcurrency);
	    }catch(SQLException sqle)
	    {
	    	currentStatement = null;
	    	throw new SQLException("新建数据源[ " + poolName + "-" + curPos + "]连接时出错:" + sqle.getMessage());
	    }
        return (PreparedStatement)currentStatement;
    }

    public synchronized CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        //isSupportLevel( Constants.StatementLevel );
        Statement currentStatement = null;
        int currentConnectionNumber = -1;
        int count = 0;
        while( currentConnectionNumber < 0 && count < 10 )
        {
            for( int i = 0; i < connPoolSize; i ++ )
            {
                curPos = curPos % connPoolSize;
                if( pooledConnection[curPos] == null )
                {
                    if(currentConnectionNumber < 0)
                    {
                        currentConnectionNumber = curPos;
                    }
                } else if( pooledConnection[curPos].isTimeout() || pooledConnection[curPos].isClosed() )
                {
                    try
                    {
                        pooledConnection[curPos].forceClose();
                    }catch(SQLException sqle )
                    {
                    	logger.info("关闭数据源连接[" + poolName + "-" + curPos + "]出错: " + sqle.getMessage() );
                    }
                    pooledConnection[curPos] = null;
                    if( currentConnectionNumber < 0 )
                    {
                        currentConnectionNumber = curPos;
                    }
                } else if( pooledConnection[curPos].searchFreeCell() >= 0 )
                {
                    try
                    {
                        currentStatement = pooledConnection[curPos].prepareCall(sql, resultSetType, resultSetConcurrency);
                    }catch(SQLException sqle)
                    {
                        currentStatement = null;
                        logger.info("新建数据源 [" + poolName + "-" + curPos + "]会话出错: " + sqle.getMessage() );
                    }
                    if( currentStatement != null )
                    {
                        return (CallableStatement)currentStatement;
                    }
                }
                curPos++;
            }
		    Thread.currentThread();
			try
			{
				Thread.sleep( idleTimeout );
			} catch (InterruptedException e)
			{
				e.printStackTrace();
			}
			count ++;
        }
        if( currentConnectionNumber < 0 )
        {
        	throw new SQLException("数据源[" + poolName + "]连接已满!");
        }
        curPos = currentConnectionNumber;
        try
        {
        	pooledConnection[curPos] = createPooledConnection();
        	currentStatement = pooledConnection[curPos].prepareCall(sql, resultSetType, resultSetConcurrency);
	    }catch(SQLException sqle)
	    {
	    	currentStatement = null;
	    	throw new SQLException("新建数据源[ " + poolName + "-" + curPos + "]连接时出错:" + sqle.getMessage());
	    }
        return (CallableStatement)currentStatement;
    }
    
    public boolean isAutoShrink()
    {
        return shrinkInterval > 0;
    }

    public String getPoolName()
    {
        return poolName;
    }

    public String getDriverName()
    {
        return driverClass;
    }

    public String getDbUrl()
    {
        return dbUrl;
    }

    public String poolLevel()
    {
        if( poolLevel == Constants.ConnectionLevel )
        {
            return "ConnectionLevel";
        }else
        {
            return "StatementLevel";
        }
    }

}
