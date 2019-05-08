package com.ripple.datasource.config;

import com.ripple.datasource.connection.ConnectionPool;

public class MyDataSource
{
	private String name;

	private String encoding;

	public boolean readOnly;

	public boolean showResult;
	
	private String dialect;

	private MyConnection myConnection;

	private ConnectionPool connectionPool;

	private ConnectionPool statementPool;
	
	public MyDataSource()
	{
		myConnection = new MyConnection();
	}
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}

	public MyConnection getMyConnection()
	{
		return myConnection;
	}

	public void setMyConnection(MyConnection myConnection)
	{
		this.myConnection = myConnection;
	}

	public boolean isReadOnly()
	{
		return readOnly;
	}

	public void setReadOnly(boolean readOnly)
	{
		this.readOnly = readOnly;
	}

	public boolean isShowResult()
	{
		return showResult;
	}

	public void setShowResult(boolean showResult)
	{
		this.showResult = showResult;
	}

	public String getEncoding()
	{
		return encoding;
	}
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}

	public ConnectionPool getConnectionPool()
	{
		return connectionPool;
	}

	public void setConnectionPool(ConnectionPool connectionPool)
	{
		this.connectionPool = connectionPool;
	}

	public ConnectionPool getStatementPool()
	{
		return statementPool;
	}

	public void setStatementPool(ConnectionPool statementPool)
	{
		this.statementPool = statementPool;
	}

	public String getDialect() {
		return dialect;
	}

	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	
}
