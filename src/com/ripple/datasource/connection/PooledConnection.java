package com.ripple.datasource.connection;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.log4j.Logger;

import com.ripple.Constants;
import com.ripple.datasource.exception.ConnectionNotInUsingException;

public class PooledConnection implements Connection
{
	private static Logger logger = Logger.getLogger( PooledConnection.class );
	  
	private boolean isBeenUsing;
	private String poolName;
	private int poolLevel;
	
    private Connection conn;
    private PooledStatement pooledStatement[];
    private long idleTimeout;
    private int statementPoolSize;
    private int curPos;
    
    private long createdTime;
    private long lastActionTime;
    
    private int connectionPosition;

    protected PooledConnection()
    {
        lastActionTime = System.currentTimeMillis();
    }

    protected PooledConnection(int connectionPosition, int poolLevel, Connection conn, String poolName, int statementPoolSize, long idleTimeout) throws SQLException
    {
        this.conn = null;
        this.curPos = 0;
        this.lastActionTime = System.currentTimeMillis();
        this.isBeenUsing = false;
        this.poolLevel = poolLevel;
        this.conn = conn;
        this.poolName = poolName;
        this.idleTimeout = idleTimeout;
        this.statementPoolSize = statementPoolSize;
        this.connectionPosition = connectionPosition;
        pooledStatement = new PooledStatement[statementPoolSize];
        for(int i = 0; i < pooledStatement.length; i++)
        {
            pooledStatement[i] = null;
        }
        createdTime = System.currentTimeMillis();
    }

    protected int getPosition()
    {
        return connectionPosition;
    }

    protected boolean isTimeout()
    {
        return System.currentTimeMillis() - lastActionTime > idleTimeout * (long)1000;
    }

    protected void active()
    {
        lastActionTime = System.currentTimeMillis();
    }

    protected long getCreatedTime()
    {
        return createdTime;
    }

    private int getActiveStatementNumber()
    {
        int activeStmtNumber = 0;
        for( int i = 0; i < statementPoolSize; i++ )
        {
            if(pooledStatement[i] != null && !pooledStatement[i].isTimeout() && !pooledStatement[i].isClosed())
            {
            	activeStmtNumber ++;
            }
        }
        return activeStmtNumber;
    }

    protected synchronized int shrink()
    {
        //logger.info( new StringBuffer("conn[").append(poolName).append("-").append(connectionPosition).append("] : start shrink process."));
        int activeStmtCount = 0;

        int endPos = -1;
        for( int i = 0; i < statementPoolSize; i ++ )
        {
            curPos = curPos % statementPoolSize;
            if(pooledStatement[curPos] == null)
            {
                if( endPos < 0 )
                {
                    endPos = curPos;
                }
            } else
            {
                if( pooledStatement[curPos].isTimeout() || pooledStatement[curPos].isClosed() )
                {
                	try
                	{
                		pooledStatement[curPos].close();
                	}catch(SQLException sqle)
                	{
                		logger.info("关闭数据源 [" + poolName + "-" + curPos + "]会话出错: " + sqle.getMessage() );
                	}
                    pooledStatement[curPos] = null;
                    if( endPos < 0 )
                    {
                        endPos = curPos;
                    }
                } else
                {
                    activeStmtCount++;
                }
            }
            curPos ++;
        }

        if( endPos >= 0 )
        {
             curPos = endPos;
        }
        return activeStmtCount;
    }

    protected int searchFreeCell()
    {
        for( int i = 0; i < statementPoolSize; i ++ )
        {
            curPos = curPos % statementPoolSize;
            if(pooledStatement[curPos] == null)
            {
                return curPos;
            }else if(pooledStatement[curPos].isClosed() )
            {
                pooledStatement[curPos] = null;
                return curPos;
            }else if(pooledStatement[curPos].isTimeout() )
            {
                if( ! pooledStatement[curPos].isClosed() )
                {
                    try
                    {
                        pooledStatement[curPos].close();
                    }catch(SQLException sqle)
                    { 
                    	logger.info( "close stmt error: " + sqle.getMessage() );
                    }
                }
                pooledStatement[curPos] = null;
                return curPos;
            }
            curPos++;
        }
        return -1;
    }

    private void closeStatement()
    {
        for( int i = 0; i < statementPoolSize; i ++ )
        {
            if(pooledStatement[i] == null)
            {
                continue;
            }
            try
            {
                pooledStatement[i].close();
            }catch(SQLException sqle)
            {
            	sqle.printStackTrace();
            }
            pooledStatement[i] = null;
        }
    }

    private synchronized PooledStatement CreatePooledStatement(Statement statement) throws SQLException
    {
        PooledStatement poolStmt = null;
        int currentStatementNumber = 0;
        try
        {
            currentStatementNumber = searchFreeCell();
            if( currentStatementNumber < 0 )
            {
                statement.close();
                return null;
            }
            PooledConnection tempPooledConnection = new PooledConnection
            (this.connectionPosition,this.poolLevel,this.conn,this.poolName,this.statementPoolSize,this.idleTimeout);
            poolStmt = new PooledStatement(currentStatementNumber, poolName, tempPooledConnection, statement, idleTimeout);
        }catch(SQLException sqle)
        {
            logger.error(new StringBuffer("conn[").append(poolName).append("-").append(connectionPosition).append("] : has error in create statement:").append(sqle.toString()));
            statement.close();
            throw sqle;
        }
        pooledStatement[currentStatementNumber] = poolStmt;
        return poolStmt;
    }

    protected void forceClose() throws SQLException
    {
        if( ! isClosed() )
        {
        	if( poolLevel == Constants.StatementLevel )
        	{
        		closeStatement();
        	}
            conn.close();
            conn = null;
            isBeenUsing = false;
        }
    }

    protected void use()
    {
        isBeenUsing = true;
    }

    protected boolean isBeenUsing()
    {
        if(poolLevel == Constants.ConnectionLevel)
        {
            return isBeenUsing;
        }else
        {
            return true;
        }
    }

    public Statement createStatement() throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return CreatePooledStatement(conn.createStatement());
        }
    }

    public PreparedStatement prepareStatement(String sql) throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return CreatePooledStatement(conn.prepareStatement( sql ));
        }
    }

    public CallableStatement prepareCall(String sql) throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            CallableStatement callableStmt = CreatePooledStatement(conn.prepareCall(sql));
            return callableStmt;
        }
    }

    public Statement createStatement( int resultSetType, int resultSetConcurrency ) throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return CreatePooledStatement(conn.createStatement(resultSetType, resultSetConcurrency));
        }
    }

    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return CreatePooledStatement(conn.prepareStatement(sql, resultSetType, resultSetConcurrency));
        }
    }

    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            PreparedStatement prepareStmt = 
            	CreatePooledStatement( conn.prepareCall(sql, resultSetType, resultSetConcurrency));
            return (CallableStatement)prepareStmt;
        }
    }

    public String nativeSQL( String sql ) throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.nativeSQL( sql );
        }
    }

    public void setAutoCommit( boolean autoCommit ) throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            conn.setAutoCommit( autoCommit );
        }
    }

    public boolean getAutoCommit() throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.getAutoCommit();
        }
    }

    public void commit() throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            conn.commit();
        }
    }

    public void rollback() throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            conn.rollback();
        }
    }

    public void close() throws SQLException
    {
        if( isClosed() )
        {
            return;
        }
        if( poolLevel == Constants.ConnectionLevel )
        {
            //commit();
            //closeStatement();
            isBeenUsing = false;
        } else
        {
            closeStatement();
            conn.close();
            conn = null;
            isBeenUsing = false;
        }
    }

    public boolean isClosed()
    {
        return conn == null;
    }

    public DatabaseMetaData getMetaData() throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.getMetaData();
        }
    }

    public void setReadOnly(boolean readOnly) throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            conn.setReadOnly( readOnly );
        }
    }

    public boolean isReadOnly() throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.isReadOnly();
        }
    }

    public void setCatalog( String catalog ) throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            conn.setCatalog( catalog );
        }
    }

    public String getCatalog() throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.getCatalog();
        }
    }

    public void setTransactionIsolation(int level) throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            conn.setTransactionIsolation( level );
        }
    }

    public int getTransactionIsolation() throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.getTransactionIsolation();
        }
    }

    public SQLWarning getWarnings() throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.getWarnings();
        }
    }

    public void clearWarnings() throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            conn.clearWarnings();
        }
    }

    public Map getTypeMap() throws SQLException
    {
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.getTypeMap();
        }
    }

    

	public int getHoldability() throws SQLException
	{
        if( !isBeenUsing() )
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.getHoldability();
        }
	}

	public void setHoldability(int holdability) throws SQLException
	{
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            conn.setHoldability( holdability );
        }
	}

	public Savepoint setSavepoint() throws SQLException
	{
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.setSavepoint();
        }
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException
	{
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            conn.releaseSavepoint( savepoint );
        }
	}

	public void rollback(Savepoint savepoint) throws SQLException
	{
        if( !isBeenUsing() )
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        } else
        {
            conn.rollback( savepoint );
        }
	}

	public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
	{
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.createStatement(resultSetType,resultSetConcurrency,resultSetHoldability);
        }
	}

	public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
	{
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.prepareCall(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
        }
	}

	public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException
	{
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.prepareStatement( sql, autoGeneratedKeys );
        }
	}

	public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException
	{
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.prepareStatement(sql,resultSetType,resultSetConcurrency,resultSetHoldability);
        }
	}

	public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException
	{
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.prepareStatement(sql,columnIndexes);
        }
	}

	public Savepoint setSavepoint(String name) throws SQLException
	{
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.setSavepoint( name );
        }
	}

	public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException
	{
        if(!isBeenUsing())
        {
            throw new ConnectionNotInUsingException("current connection can NOT be used for reference object.");
        }else
        {
            return conn.prepareStatement( sql, columnNames );
        }
	}

	public boolean isWrapperFor(Class<?> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> T unwrap(Class<T> arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Array createArrayOf(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Blob createBlob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Clob createClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob createNClob() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML createSQLXML() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Struct createStruct(String arg0, Object[] arg1) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Properties getClientInfo() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getClientInfo(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean isValid(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setClientInfo(Properties arg0) throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}

	public void setClientInfo(String arg0, String arg1)
			throws SQLClientInfoException {
		// TODO Auto-generated method stub
		
	}

	public void setTypeMap(Map<String, Class<?>> arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setSchema(String schema) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public String getSchema() throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void abort(Executor executor) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNetworkTimeout(Executor executor, int milliseconds)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public int getNetworkTimeout() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
	}
}
