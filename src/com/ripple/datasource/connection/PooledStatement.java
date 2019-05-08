package com.ripple.datasource.connection;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;


public class PooledStatement implements Statement, PreparedStatement, CallableStatement
{
	//private static Logger logger = LogManager.getLogger( PooledStatement.class );
	
    private String poolName;
    private Statement statement;
    private PooledConnection pooledConnection;
    private long createdTime;
    private long lastActionTime;
    private long timeOut;
    private String referedObjectName;
    private String referedObjectClass;
    private int statementPosition;

    public PooledStatement( int statementPosition, String poolName,PooledConnection pooledConnection,
    						Statement statement, 
    						long timeOut )
    {
        this.createdTime = System.currentTimeMillis();
        this.lastActionTime = System.currentTimeMillis();
        this.timeOut = 60L;
        this.referedObjectName = "";
        this.referedObjectClass = "";
        this.pooledConnection = pooledConnection;
        this.poolName = poolName;
        this.statement = statement;
        this.timeOut = timeOut;
        this.statementPosition = statementPosition;
        active();
    }

    public String toString()
    {
        return new StringBuffer("[")
        	//.append(pooledConnection.getPosition())
        	.append("-")
    		.append(statementPosition).append("] closed=")
    		.append(isClosed()).append(", createdTime=")
    		.append((new Date(createdTime)).toString())
    		.append(", lastActionTime=")
    		.append((new Date(lastActionTime)).toString()).toString();
    		//.append(", referedObjectName=")
    		//.append(referedObjectName)
    		//.append(", referedObjectClass=")
    		//.append(referedObjectClass).toString();
    }

    public void close() throws SQLException
    {
        if(statement != null)
        {
            try
            {
                statement.close();
            }catch(SQLException e)
            {
                throw e;
            }finally
            {
            	statement = null;
            }
        }
    }

    public boolean isClosed()
    {
        return statement == null;
    }

    public boolean isTimeout()
    {
        return System.currentTimeMillis() - lastActionTime > timeOut * (long)1000;
    }

    public void active()
    {
        pooledConnection.active();
        lastActionTime = System.currentTimeMillis();
        referedObjectName = "";
        referedObjectClass = "";
    }

    public ResultSet executeQuery(String sql) throws SQLException
    {
        active();
        PooledStatement tempPooledStatement = new PooledStatement
        (this.statementPosition,this.poolName,this.pooledConnection,this.statement,this.timeOut);
        return new PooledResultSet(tempPooledStatement, statement.executeQuery(sql));
        //return statement.executeQuery(sql);
    }

    public int executeUpdate(String sql) throws SQLException
    {
        active();
        return statement.executeUpdate(sql);
    }

    public int getMaxFieldSize() throws SQLException
    {
        active();
        return statement.getMaxFieldSize();
    }

    public void setMaxFieldSize(int max) throws SQLException
    {
        active();
        statement.setMaxFieldSize( max );
    }

    public int getMaxRows() throws SQLException
    {
        active();
        return statement.getMaxRows();
    }

    public void setMaxRows(int row) throws SQLException
    {
        active();
        statement.setMaxRows(row);
    }

    public void setEscapeProcessing(boolean enable) throws SQLException
    {
        active();
        statement.setEscapeProcessing(enable);
    }

    public int getQueryTimeout() throws SQLException
    {
        active();
        return statement.getQueryTimeout();
    }

    public void setQueryTimeout(int seconds) throws SQLException
    {
        active();
        statement.setQueryTimeout(seconds);
    }

    public void cancel() throws SQLException
    {
        active();
        statement.cancel();
    }

    public SQLWarning getWarnings() throws SQLException
    {
        active();
        return statement.getWarnings();
    }

    public void clearWarnings() throws SQLException
    {
        active();
        statement.clearWarnings();
    }

    public void setCursorName(String name) throws SQLException
    {
        active();
        statement.setCursorName(name);
    }

    public boolean execute(String sql) throws SQLException
    {
        active();
        return statement.execute(sql);
    }

    public ResultSet getResultSet() throws SQLException
    {
        active();
        return new PooledResultSet((PooledStatement) statement, statement.getResultSet());
    }

    public int getUpdateCount() throws SQLException
    {
        active();
        return statement.getUpdateCount();
    }

    public boolean getMoreResults() throws SQLException
    {
        active();
        return statement.getMoreResults();
    }

    public void setFetchDirection(int direction) throws SQLException
    {
        active();
        statement.setFetchDirection( direction );
    }

    public int getFetchDirection() throws SQLException
    {
        active();
        return statement.getFetchDirection();
    }

    public void setFetchSize(int rows) throws SQLException
    {
        active();
        statement.setFetchSize(rows);
    }

    public int getFetchSize() throws SQLException
    {
        active();
        return statement.getFetchSize();
    }

    public int getResultSetConcurrency() throws SQLException
    {
        active();
        return statement.getResultSetConcurrency();
    }

    public int getResultSetType() throws SQLException
    {
        active();
        return statement.getResultSetType();
    }

    public void addBatch(String sql) throws SQLException
    {
        active();
        statement.addBatch(sql);
    }

    public void clearBatch() throws SQLException
    {
        active();
        statement.clearBatch();
    }

    public int[] executeBatch() throws SQLException
    {
        active();
        return statement.executeBatch();
    }

    public Connection getConnection() throws SQLException
    {
        throw new SQLException("the method is NOT supported.");
    }

    public void setNull(int parameterIndex, int sqlType) throws SQLException
    {
        active();
        ((CallableStatement)statement).setNull(parameterIndex, sqlType);
    }

    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException
    {
        active();
        ((CallableStatement) statement).setNull(parameterIndex, sqlType, typeName);
    }

    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setTimestamp(parameterIndex, x);
    }

    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setTimestamp(parameterIndex, x, cal);
    }

    public void setArray(int parameterIndex, Array x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setArray(parameterIndex, x);
    }

    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setAsciiStream(parameterIndex, x, length);
    }

    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setBigDecimal(parameterIndex, x);
    }

    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setBinaryStream(parameterIndex, x, length);
    }

    public void setBlob(int parameterIndex, Blob x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setBlob(parameterIndex, x);
    }

    public void setBoolean(int parameterIndex, boolean x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setBoolean(parameterIndex, x);
    }

    public void setByte(int parameterIndex, byte x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setByte(parameterIndex, x);
    }

    public void setBytes(int parameterIndex, byte x[]) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setBytes(parameterIndex, x);
    }

    public void setCharacterStream(int parameterIndex, Reader x, int length) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setCharacterStream(parameterIndex, x, length);
    }

    public void setClob(int parameterIndex, Clob x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setClob(parameterIndex, x);
    }

    public void setDate(int parameterIndex, java.sql.Date x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setDate(parameterIndex, x);
    }

    public void setDate(int parameterIndex, java.sql.Date x, Calendar cal) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setDate(parameterIndex, x, cal);
    }

    public void setDouble(int parameterIndex, double x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setDouble(parameterIndex, x);
    }

    public void setFloat(int parameterIndex, float x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setFloat(parameterIndex, x);
    }

    public void setInt(int parameterIndex, int x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setInt(parameterIndex, x);
    }

    public void setLong(int parameterIndex, long x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setLong(parameterIndex, x);
    }

    public void setObject(int parameterIndex, Object x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setObject(parameterIndex, x);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType)
        throws SQLException
    {
        active();
        ((PreparedStatement)statement).setObject(parameterIndex, x, targetSqlType);
    }

    public void setObject(int parameterIndex, Object x, int targetSqlType, int scale) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setObject(parameterIndex, x, targetSqlType, scale);
    }

    public void setRef(int parameterIndex, Ref x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setRef(parameterIndex, x);
    }

    public void setShort(int parameterIndex, short x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setShort(parameterIndex, x);
    }

    public void setString(int parameterIndex, String x) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setString(parameterIndex, x);
    }

    public void setTime(int parameterIndex, Time time) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setTime(parameterIndex, time);
    }

    public void setTime(int parameterIndex, Time time, Calendar cal) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setTime(parameterIndex, time, cal);
    }

    public void setUnicodeStream(int parameterIndex, InputStream is, int scale) throws SQLException
    {
        active();
        ((PreparedStatement)statement).setUnicodeStream(parameterIndex,is,scale);
    }

    public ResultSetMetaData getMetaData() throws SQLException
    {
        active();
        return ((PreparedStatement)statement).getMetaData();
    }

    public void addBatch() throws SQLException
    {
        active();
        ((PreparedStatement)statement).addBatch();
    }

    public boolean execute() throws SQLException
    {
        active();
        return ((PreparedStatement)statement).execute();
    }

    public void clearParameters() throws SQLException
    {
        active();
        ((PreparedStatement)statement).clearParameters();
    }

    public int executeUpdate() throws SQLException
    {
        active();
        return ((PreparedStatement)statement).executeUpdate();
    }

    public ResultSet executeQuery() throws SQLException
    {
        active();
        return ((PreparedStatement)statement).executeQuery();
    }

    public Array getArray(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getArray(parameterIndex);
    }

    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getBigDecimal(parameterIndex);
    }

    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getBigDecimal(parameterIndex,scale);
    }

    public Blob getBlob(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getBlob(parameterIndex);
    }

    public boolean getBoolean(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getBoolean(parameterIndex);
    }

    public byte getByte(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getByte(parameterIndex);
    }

    public byte[] getBytes(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getBytes(parameterIndex);
    }

    public Clob getClob(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getClob(parameterIndex);
    }

    public java.sql.Date getDate(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getDate(parameterIndex);
    }

    public java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getDate(parameterIndex, cal);
    }

    public double getDouble(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getDouble(parameterIndex);
    }

    public float getFloat(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getFloat(parameterIndex);
    }

    public int getInt(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getInt(parameterIndex);
    }

    public long getLong(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getLong(parameterIndex);
    }

    public Object getObject(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getObject(parameterIndex);
    }

   

    public Ref getRef(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getRef(parameterIndex);
    }

    public short getShort(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getShort(parameterIndex);
    }

    public String getString(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getString(parameterIndex);
    }

    public Time getTime(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getTime(parameterIndex);
    }

    public Time getTime(int parameterIndex, Calendar cal) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getTime(parameterIndex, cal);
    }

    public Timestamp getTimestamp(int parameterIndex) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getTimestamp(parameterIndex);
    }

    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException
    {
        active();
        return ((CallableStatement)statement).getTimestamp(parameterIndex, cal);
    }

    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException
    {
        active();
        ((CallableStatement)statement).registerOutParameter(parameterIndex, sqlType);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException
    {
        active();
        ((CallableStatement)statement).registerOutParameter(parameterIndex, sqlType, scale);
    }

    public void registerOutParameter(int parameterIndex, int sqlType, String scale) throws SQLException
    {
        active();
        ((CallableStatement)statement).registerOutParameter(parameterIndex, sqlType, scale);
    }

    public boolean wasNull() throws SQLException
    {
        active();
        return ((CallableStatement)statement).wasNull();
    }

	public int getResultSetHoldability() throws SQLException
	{
        active();
        return ((CallableStatement)statement).getResultSetHoldability();
	}

	public boolean getMoreResults(int current) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getMoreResults(current);
	}

	public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException
	{
        active();
        return statement.executeUpdate(sql,autoGeneratedKeys);
	}

	public boolean execute(String sql, int autoGeneratedKeys) throws SQLException
	{
        active();
        return ((CallableStatement)statement).execute(sql,autoGeneratedKeys);
	}

	public int executeUpdate(String sql, int[] columnIndexes) throws SQLException
	{
        active();
        return ((CallableStatement)statement).executeUpdate(sql,columnIndexes);
	}

	public boolean execute(String sql, int[] columnIndexes) throws SQLException
	{
        active();
        return ((CallableStatement)statement).execute(sql,columnIndexes);
	}

	public ResultSet getGeneratedKeys() throws SQLException
	{
        active();
        return ((CallableStatement)statement).getGeneratedKeys();
	}

	public int executeUpdate(String sql, String[] columnNames) throws SQLException
	{
        active();
        return ((CallableStatement)statement).executeUpdate(sql,columnNames);
	}

	public boolean execute(String sql, String[] columnNames) throws SQLException
	{
        active();
        return ((CallableStatement)statement).execute(sql,columnNames);
	}

	public void setURL(int parameterIndex, URL x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setURL(parameterIndex,x);
	}

	public ParameterMetaData getParameterMetaData() throws SQLException
	{
        active();
        return ((CallableStatement)statement).getParameterMetaData();
	}

	public byte getByte(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getByte(parameterName);
	}

	public double getDouble(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getDouble(parameterName);
	}

	public float getFloat(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getFloat(parameterName);
	}

	public int getInt(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getInt(parameterName);
	}

	public long getLong(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getLong(parameterName);
	}

	public short getShort(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getShort(parameterName);
	}

	public boolean getBoolean(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getBoolean(parameterName);
	}

	public byte[] getBytes(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getBytes(parameterName);
	}

	public void setByte(String parameterName, byte x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setByte(parameterName,x);
	}

	public void setDouble(String parameterName, double x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setDouble(parameterName,x);
	}

	public void setFloat(String parameterName, float x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setFloat(parameterName,x);
	}

	public void registerOutParameter(String parameterName, int sqlType) throws SQLException
	{
        active();
        ((CallableStatement)statement).registerOutParameter(parameterName,sqlType);
	}

	public void setInt(String parameterName, int x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setInt(parameterName,x);
	}

	public void setNull(String parameterName, int sqlType) throws SQLException
	{
        active();
        ((CallableStatement)statement).setNull(parameterName,sqlType);
	}

	public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException
	{
        active();
        ((CallableStatement)statement).registerOutParameter(parameterName,sqlType,scale);
		
	}

	public void setLong(String parameterName, long x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setLong(parameterName,x);
    }

	public void setShort(String parameterName, short x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setShort(parameterName,x);
	}

	public void setBoolean(String parameterName, boolean x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setBoolean(parameterName,x);
	}

	public void setBytes(String parameterName, byte[] x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setBytes(parameterName,x);
	}

	public URL getURL(int parameterIndex) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getURL(parameterIndex);
	}

	public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException
	{
        active();
        ((CallableStatement)statement).setAsciiStream(parameterName,x,length);
	}

	public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException
	{
        active();
        ((CallableStatement)statement).setBinaryStream(parameterName,x,length);
	}

	public void setCharacterStream(String parameterName, Reader reader, int length) throws SQLException
	{
        active();
        ((CallableStatement)statement).setCharacterStream(parameterName,reader,length);	
	}

	public Object getObject(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getObject(parameterName);	
	}

	public void setObject(String parameterName, Object x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setObject(parameterName,x);	
	}

	public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException
	{
        active();
        ((CallableStatement)statement).setObject(parameterName,x,targetSqlType);		
	}

	public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException
	{
        active();
        ((CallableStatement)statement).setObject(parameterName,x,targetSqlType,scale);	
	}

	public String getString(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getString(parameterName);
	}

	public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException
	{
        active();
        ((CallableStatement)statement).registerOutParameter(parameterName,sqlType,typeName);
	}

	public void setNull(String parameterName, int sqlType, String typeName) throws SQLException
	{
        active();
        ((CallableStatement)statement).setNull(parameterName,sqlType,typeName);		
	}

	public void setString(String parameterName, String x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setString(parameterName,x);
	}

	public BigDecimal getBigDecimal(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getBigDecimal(parameterName);
	}

	public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setBigDecimal(parameterName,x);
	}

	public URL getURL(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getURL(parameterName);
	}

	public void setURL(String parameterName, URL val) throws SQLException
	{
        active();
        ((CallableStatement)statement).setURL(parameterName,val);
	}

	public Array getArray(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getArray(parameterName);
	}

	public Blob getBlob(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getBlob(parameterName);
	}

	public Clob getClob(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getClob(parameterName);
	}

	public java.sql.Date getDate(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getDate(parameterName);
	}

	public void setDate(String parameterName, java.sql.Date x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setDate(parameterName,x);
	}

	public Ref getRef(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getRef(parameterName);
	}

	public Time getTime(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getTime(parameterName);
	}

	public void setTime(String parameterName, Time x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setTime(parameterName,x);
	}

	public Timestamp getTimestamp(String parameterName) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getTimestamp(parameterName);
	}

	public void setTimestamp(String parameterName, Timestamp x) throws SQLException
	{
        active();
        ((CallableStatement)statement).setTimestamp(parameterName,x);
	}

	

	public java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getDate(parameterName,cal);
	}

	public Time getTime(String parameterName, Calendar cal) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getTime(parameterName,cal);
	}

	public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException
	{
        active();
        return ((CallableStatement)statement).getTimestamp(parameterName,cal);
	}

	public void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException
	{
        active();
        ((CallableStatement)statement).setDate(parameterName,x,cal);
	}

	public void setTime(String parameterName, Time x, Calendar cal) throws SQLException
	{
        active();
        ((CallableStatement)statement).setTime(parameterName,x,cal);
	}

	public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException
	{
        active();
        ((CallableStatement)statement).setTimestamp(parameterName,x,cal);
		
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getCharacterStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getCharacterStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Reader getNCharacterStream(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob getNClob(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public NClob getNClob(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNString(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getNString(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(int arg0, Map<String, Class<?>> arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public Object getObject(String arg0, Map<String, Class<?>> arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowId getRowId(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public RowId getRowId(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(int arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public SQLXML getSQLXML(String arg0) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setAsciiStream(String arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setAsciiStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBinaryStream(String arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBinaryStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(String arg0, Blob arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(String arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(String arg0, Clob arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(String arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNString(String arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setRowId(String arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setSQLXML(String arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setAsciiStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setAsciiStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBinaryStream(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBinaryStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setBlob(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNCharacterStream(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(int arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNClob(int arg0, Reader arg1, long arg2) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setNString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setRowId(int arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void setSQLXML(int arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean isPoolable() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void setPoolable(boolean arg0) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public <T> T getObject(int parameterIndex, Class<T> type)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getObject(String parameterName, Class<T> type)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public void closeOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public boolean isCloseOnCompletion() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}
}
