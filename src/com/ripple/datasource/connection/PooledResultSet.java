package com.ripple.datasource.connection;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
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
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

public class PooledResultSet implements ResultSet
{
	private static Logger logger = LogManager.getLogger( PooledResultSet.class );
	
    private ResultSet rs;
    private PooledStatement ps;

    public PooledResultSet( PooledStatement ps, ResultSet rs )
    {
        this.rs = rs;
        this.ps = ps;
    }

    private void Active()
    {
        ps.active();
    }

    public boolean next() throws SQLException
    {
        Active();
        return rs.next();
    }

    public void close() throws SQLException
    {
        rs.close();
    }

    public boolean wasNull() throws SQLException
    {
        Active();
        return rs.wasNull();
    }

    public String getString(int string) throws SQLException
    {
        Active();
        return rs.getString(string);
    }

    public boolean getBoolean(int string) throws SQLException
    {
        Active();
        return rs.getBoolean(string);
    }

    public byte getByte(int string) throws SQLException
    {
        Active();
        return rs.getByte(string);
    }

    public short getShort(int string) throws SQLException
    {
        Active();
        return rs.getShort(string);
    }

    public int getInt(int string) throws SQLException
    {
        Active();
        return rs.getInt(string);
    }

    public long getLong(int string) throws SQLException
    {
        Active();
        return rs.getLong(string);
    }

    public float getFloat(int string) throws SQLException
    {
        Active();
        return rs.getFloat(string);
    }

    public double getDouble(int string) throws SQLException
    {
        Active();
        return rs.getDouble(string);
    }

    public BigDecimal getBigDecimal(int columnIndex,int scale) throws SQLException
    {
        Active();
        return rs.getBigDecimal(columnIndex,scale);
    }

    public byte[] getBytes(int string) throws SQLException
    {
        Active();
        return rs.getBytes(string);
    }

    public Date getDate(int string) throws SQLException
    {
        Active();
        return rs.getDate(string);
    }

    public Time getTime(int string) throws SQLException
    {
        Active();
        return rs.getTime(string);
    }

    public Timestamp getTimestamp(int string) throws SQLException
    {
        Active();
        return rs.getTimestamp(string);
    }

    public InputStream getAsciiStream(int string) throws SQLException
    {
        Active();
        return rs.getAsciiStream(string);
    }

    public InputStream getUnicodeStream(int string) throws SQLException
    {
        Active();
        return null;
    }

    public InputStream getBinaryStream(int string) throws SQLException
    {
        Active();
        return rs.getBinaryStream(string);
    }

    public String getString(String columnName) throws SQLException
    {
        Active();
        return rs.getString(columnName);
    }

    public boolean getBoolean(String columnName) throws SQLException
    {
        Active();
        return rs.getBoolean(columnName);
    }

    public byte getByte(String columnName) throws SQLException
    {
        Active();
        return rs.getByte(columnName);
    }

    public short getShort(String columnName) throws SQLException
    {
        Active();
        return rs.getShort(columnName);
    }

    public int getInt(String columnName) throws SQLException
    {
        Active();
        return rs.getInt(columnName);
    }

    public long getLong(String columnName) throws SQLException
    {
        Active();
        return rs.getLong(columnName);
    }

    public float getFloat(String columnName) throws SQLException
    {
        Active();
        return rs.getFloat(columnName);
    }

    public double getDouble( String columnName ) throws SQLException
    {
        Active();
        return rs.getDouble( columnName );
    }

    public BigDecimal getBigDecimal(String columnName, int scale)
        throws SQLException
    {
        Active();
        return rs.getBigDecimal(columnName,scale);
    }

    public byte[] getBytes(String columnName) throws SQLException
    {
        Active();
        return rs.getBytes( columnName );
    }

    public Date getDate(String columnName) throws SQLException
    {
        Active();
        return rs.getDate( columnName );
    }

    public Time getTime(String columnName) throws SQLException
    {
        Active();
        return rs.getTime( columnName );
    }

    public Timestamp getTimestamp(String columnName) throws SQLException
    {
        Active();
        return rs.getTimestamp( columnName );
    }

    public InputStream getAsciiStream(String columnName) throws SQLException
    {
        Active();
        return rs.getAsciiStream( columnName );
    }

    public InputStream getUnicodeStream(String columnName) throws SQLException
    {
        Active();
        return rs.getUnicodeStream(columnName);
    }

    public InputStream getBinaryStream(String columnName) throws SQLException
    {
        Active();
        return rs.getBinaryStream( columnName );
    }

    public SQLWarning getWarnings() throws SQLException
    {
        Active();
        return rs.getWarnings();
    }

    public void clearWarnings() throws SQLException
    {
        Active();
        rs.clearWarnings();
    }

    public String getCursorName() throws SQLException
    {
        Active();
        return rs.getCursorName();
    }

    public ResultSetMetaData getMetaData() throws SQLException
    {
        Active();
        return rs.getMetaData();
    }

    public Object getObject(int columnIndex) throws SQLException
    {
        Active();
        return rs.getObject( columnIndex );
    }

    public Object getObject(String columnName) throws SQLException
    {
        Active();
        return rs.getObject( columnName );
    }

    public int findColumn(String columnName) throws SQLException
    {
        Active();
        return rs.findColumn( columnName );
    }

    public Reader getCharacterStream(int columnIndex) throws SQLException
    {
        Active();
        return rs.getCharacterStream( columnIndex );
    }

    public Reader getCharacterStream(String columnName) throws SQLException
    {
        Active();
        return rs.getCharacterStream( columnName );
    }

    public BigDecimal getBigDecimal(int columnIndex) throws SQLException
    {
        Active();
        return rs.getBigDecimal( columnIndex );
    }

    public BigDecimal getBigDecimal(String columnName) throws SQLException
    {
        Active();
        return rs.getBigDecimal( columnName );
    }

    public boolean isBeforeFirst() throws SQLException
    {
        Active();
        return rs.isBeforeFirst();
    }

    public boolean isAfterLast() throws SQLException
    {
        Active();
        return rs.isAfterLast();
    }

    public boolean isFirst() throws SQLException
    {
        Active();
        return rs.isFirst();
    }

    public boolean isLast() throws SQLException
    {
        Active();
        return rs.isLast();
    }

    public void beforeFirst() throws SQLException
    {
        Active();
        rs.beforeFirst();
    }

    public void afterLast() throws SQLException
    {
        Active();
        rs.afterLast();
    }

    public boolean first() throws SQLException
    {
        Active();
        return rs.first();
    }

    public boolean last() throws SQLException
    {
        Active();
        return rs.last();
    }

    public int getRow() throws SQLException
    {
        Active();
        return rs.getRow();
    }

    public boolean absolute(int columnIndex) throws SQLException
    {
        Active();
        return rs.absolute( columnIndex );
    }

    public boolean relative(int columnIndex) throws SQLException
    {
        Active();
        return rs.relative( columnIndex );
    }

    public boolean previous() throws SQLException
    {
        Active();
        return rs.previous();
    }

    public void setFetchDirection(int columnIndex) throws SQLException
    {
        Active();
        rs.setFetchDirection( columnIndex );
    }

    public int getFetchDirection() throws SQLException
    {
        Active();
        return rs.getFetchDirection();
    }

    public void setFetchSize(int columnIndex) throws SQLException
    {
        Active();
        rs.setFetchSize(columnIndex);
    }

    public int getFetchSize() throws SQLException
    {
        Active();
        return rs.getFetchSize();
    }

    public int getType() throws SQLException
    {
        Active();
        return rs.getType();
    }

    public int getConcurrency() throws SQLException
    {
        Active();
        return rs.getConcurrency();
    }

    public boolean rowUpdated() throws SQLException
    {
        Active();
        return rs.rowUpdated();
    }

    public boolean rowInserted() throws SQLException
    {
        Active();
        return rs.rowInserted();
    }

    public boolean rowDeleted() throws SQLException
    {
        Active();
        return rs.rowDeleted();
    }

    public void updateNull(int columnIndex) throws SQLException
    {
        Active();
        rs.updateNull( columnIndex );
    }

    public void updateBoolean(int columnIndex, boolean x) throws SQLException
    {
        Active();
        rs.updateBoolean(columnIndex, x);
    }

    public void updateByte(int columnIndex, byte x) throws SQLException
    {
        Active();
        rs.updateByte(columnIndex, x);
    }

    public void updateShort(int columnIndex, short x) throws SQLException
    {
        Active();
        rs.updateShort(columnIndex, x);
    }

    public void updateInt(int columnIndex, int x)
        throws SQLException
    {
        Active();
        rs.updateInt(columnIndex, x);
    }

    public void updateLong(int columnIndex, long x) throws SQLException
    {
        Active();
        rs.updateLong(columnIndex, x);
    }

    public void updateFloat(int columnIndex, float x) throws SQLException
    {
        Active();
        rs.updateFloat(columnIndex, x);
    }

    public void updateDouble(int columnIndex, double x) throws SQLException
    {
        Active();
        rs.updateDouble(columnIndex, x);
    }

    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException
    {
        Active();
        rs.updateBigDecimal(columnIndex, x);
    }

    public void updateString(int columnIndex, String x) throws SQLException
    {
        Active();
        rs.updateString(columnIndex, x);
    }

    public void updateBytes(int columnIndex, byte x[]) throws SQLException
    {
        Active();
        rs.updateBytes(columnIndex, x);
    }

    public void updateDate(int columnIndex, Date x) throws SQLException
    {
        Active();
        rs.updateDate(columnIndex, x);
    }

    public void updateTime(int columnIndex, Time x)
        throws SQLException
    {
        Active();
        rs.updateTime(columnIndex, x);
    }

    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException
    {
        Active();
        rs.updateTimestamp(columnIndex, x);
    }

    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException
    {
        Active();
        rs.updateAsciiStream(columnIndex, x, length);
    }

    public void updateBinaryStream(int string, InputStream x, int length) throws SQLException
    {
        Active();
        rs.updateBinaryStream(string, x, length);
    }

    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException
    {
        Active();
        rs.updateCharacterStream(columnIndex, x, length);
    }

    public void updateObject(int columnIndex, Object x, int length) throws SQLException
    {
        Active();
        rs.updateObject(columnIndex, x, length);
    }

    public void updateObject(int columnIndex, Object x)
        throws SQLException
    {
        Active();
        rs.updateObject(columnIndex, x);
    }

    public void updateNull(String columnName) throws SQLException
    {
        Active();
        rs.updateNull( columnName );
    }

    public void updateBoolean(String columnName, boolean x) throws SQLException
    {
        Active();
        rs.updateBoolean(columnName, x);
    }

    public void updateByte(String columnName, byte x) throws SQLException
    {
        Active();
        rs.updateByte(columnName, x);
    }

    public void updateShort(String columnName, short x) throws SQLException
    {
        Active();
        rs.updateShort(columnName, x);
    }

    public void updateInt(String columnName, int x) throws SQLException
    {
        Active();
        rs.updateInt(columnName, x);
    }

    public void updateLong(String columnName, long x) throws SQLException
    {
        Active();
        rs.updateLong(columnName, x);
    }

    public void updateFloat(String columnName, float x) throws SQLException
    {
        Active();
        rs.updateFloat(columnName, x);
    }

    public void updateDouble(String columnName, double x) throws SQLException
    {
        Active();
        rs.updateDouble(columnName, x);
    }

    public void updateBigDecimal(String columnName, BigDecimal x) throws SQLException
    {
        Active();
        rs.updateBigDecimal(columnName, x);
    }

    public void updateString(String columnName, String x) throws SQLException
    {
        Active();
        rs.updateString(columnName, x);
    }

    public void updateBytes(String columnName, byte x[]) throws SQLException
    {
        Active();
        rs.updateBytes(columnName, x);
    }

    public void updateDate(String columnName, Date x) throws SQLException
    {
        Active();
        rs.updateDate(columnName, x);
    }

    public void updateTime(String columnName, Time x) throws SQLException
    {
        Active();
        rs.updateTime(columnName, x);
    }

    public void updateTimestamp(String columnName, Timestamp x) throws SQLException
    {
        Active();
        rs.updateTimestamp(columnName, x);
    }

    public void updateAsciiStream(String columnName, InputStream x, int length) throws SQLException
    {
        Active();
        rs.updateAsciiStream(columnName, x, length);
    }

    public void updateBinaryStream(String columnName, InputStream x, int length) throws SQLException
    {
        Active();
        rs.updateBinaryStream(columnName, x, length);
    }

    public void updateCharacterStream(String columnName, Reader x, int length) throws SQLException
    {
        Active();
        rs.updateCharacterStream(columnName, x, length);
    }

    public void updateObject(String columnName, Object x, int length) throws SQLException
    {
        Active();
        rs.updateObject(columnName, x, length);
    }

    public void updateObject(String columnName, Object x) throws SQLException
    {
        Active();
        rs.updateObject(columnName, x);
    }

    public void insertRow() throws SQLException
    {
        Active();
        rs.insertRow();
    }

    public void updateRow()
        throws SQLException
    {
        Active();
        rs.updateRow();
    }

    public void deleteRow()
        throws SQLException
    {
        Active();
        rs.deleteRow();
    }

    public void refreshRow()
        throws SQLException
    {
        Active();
        rs.refreshRow();
    }

    public void cancelRowUpdates()
        throws SQLException
    {
        Active();
        rs.cancelRowUpdates();
    }

    public void moveToInsertRow()
        throws SQLException
    {
        Active();
        rs.moveToInsertRow();
    }

    public void moveToCurrentRow()
        throws SQLException
    {
        Active();
        rs.moveToCurrentRow();
    }

    public Statement getStatement()
        throws SQLException
    {
        Active();
        return rs.getStatement();
    }

  

    public Ref getRef(int columnIndex) throws SQLException
    {
        Active();
        return rs.getRef(columnIndex);
    }

    public Blob getBlob(int columnIndex) throws SQLException
    {
        Active();
        return rs.getBlob(columnIndex);
    }

    public Clob getClob(int columnIndex) throws SQLException
    {
        Active();
        return rs.getClob(columnIndex);
    }

    public Array getArray(int columnIndex) throws SQLException
    {
        Active();
        return rs.getArray(columnIndex);
    }

    

    public Ref getRef(String columnIndex) throws SQLException
    {
        Active();
        return rs.getRef(columnIndex);
    }

    public Blob getBlob(String columnIndex) throws SQLException
    {
        Active();
        return rs.getBlob(columnIndex);
    }

    public Clob getClob(String columnIndex) throws SQLException
    {
        Active();
        return rs.getClob(columnIndex);
    }

    public Array getArray(String columnIndex) throws SQLException
    {
        Active();
        return rs.getArray(columnIndex);
    }

    public Date getDate(int columnIndex, Calendar cal) throws SQLException
    {
        Active();
        return rs.getDate(columnIndex, cal);
    }

    public Date getDate(String columnIndex, Calendar cal) throws SQLException
    {
        Active();
        return rs.getDate(columnIndex, cal);
    }

    public Time getTime(int columnIndex, Calendar cal) throws SQLException
    {
        Active();
        return rs.getTime(columnIndex, cal);
    }

    public Time getTime(String columnIndex, Calendar cal) throws SQLException
    {
        Active();
        return rs.getTime(columnIndex, cal);
    }

    public Timestamp getTimestamp(int columnIndex, Calendar cal)
        throws SQLException
    {
        Active();
        return rs.getTimestamp(columnIndex, cal);
    }

    public Timestamp getTimestamp(String columnIndex, Calendar cal) throws SQLException
    {
        Active();
        return rs.getTimestamp(columnIndex, cal);
    }

	public URL getURL(int columnIndex) throws SQLException
	{
		Active();
		return rs.getURL(columnIndex);
	}

	public void updateArray(int columnIndex, Array x) throws SQLException
	{
		Active();
		rs.updateArray(columnIndex,x);
	}

	public void updateBlob(int columnIndex, Blob x) throws SQLException
	{
		Active();
		rs.updateBlob(columnIndex,x);
	}

	public void updateClob(int columnIndex, Clob x) throws SQLException
	{
		Active();
		rs.updateClob(columnIndex,x);
	}

	public void updateRef(int columnIndex, Ref x) throws SQLException
	{
		Active();
		rs.updateRef(columnIndex,x);
	}

	public URL getURL(String columnName) throws SQLException
	{
		Active();
		return rs.getURL(columnName);
	}

	public void updateArray(String columnName, Array x) throws SQLException
	{
		Active();
		rs.updateArray(columnName,x);
	}

	public void updateBlob(String columnName, Blob x) throws SQLException
	{
		Active();
		rs.updateBlob(columnName,x);
	}

	public void updateClob(String columnName, Clob x) throws SQLException
	{
		Active();
		rs.updateClob(columnName,x);
	}

	public void updateRef(String columnName, Ref x) throws SQLException
	{
		Active();
		rs.updateRef(columnName,x);
	}

	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public <T> T unwrap(Class<T> iface) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public int getHoldability() throws SQLException {
		// TODO Auto-generated method stub
		return 0;
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

	public boolean isClosed() throws SQLException {
		// TODO Auto-generated method stub
		return false;
	}

	public void updateAsciiStream(int arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(String arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateAsciiStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(int arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(String arg0, InputStream arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBinaryStream(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(int arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(String arg0, InputStream arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(int arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateBlob(String arg0, InputStream arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(int arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(String arg0, Reader arg1)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNCharacterStream(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String arg0, NClob arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String arg0, Reader arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(int arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNClob(String arg0, Reader arg1, long arg2)
			throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNString(int arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateNString(String arg0, String arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateRowId(int arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateRowId(String arg0, RowId arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateSQLXML(int arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public void updateSQLXML(String arg0, SQLXML arg1) throws SQLException {
		// TODO Auto-generated method stub
		
	}

	public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}

	public <T> T getObject(String columnLabel, Class<T> type)
			throws SQLException {
		// TODO Auto-generated method stub
		return null;
	}
}
