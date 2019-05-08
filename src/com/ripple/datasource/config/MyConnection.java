package com.ripple.datasource.config;

public class MyConnection
{
	private int idleTimeout;

	private int shrinkInterval;

	private int coonnectionLevelPoolSize;

	private int statementLevelPoolSize;

	private int statementPoolSize;
	
	private MyDatabase database;
	
	public MyConnection()
	{
		this.database = new MyDatabase();
	}

	public int getCoonnectionLevelPoolSize()
	{
		return coonnectionLevelPoolSize;
	}

	public void setCoonnectionLevelPoolSize(int coonnectionLevelPoolSize)
	{
		this.coonnectionLevelPoolSize = coonnectionLevelPoolSize;
	}

	public MyDatabase getDatabase()
	{
		return database;
	}

	public void setDatabase(MyDatabase database)
	{
		this.database = database;
	}

	public int getIdleTimeout()
	{
		return idleTimeout;
	}

	public void setIdleTimeout(int idleTimeout)
	{
		this.idleTimeout = idleTimeout;
	}

	public int getShrinkInterval()
	{
		return shrinkInterval;
	}

	public void setShrinkInterval(int shrinkInterval)
	{
		this.shrinkInterval = shrinkInterval;
	}

	public int getStatementLevelPoolSize()
	{
		return statementLevelPoolSize;
	}

	public void setStatementLevelPoolSize(int statementLevelPoolSize)
	{
		this.statementLevelPoolSize = statementLevelPoolSize;
	}

	public int getStatementPoolSize()
	{
		return statementPoolSize;
	}

	public void setStatementPoolSize(int statementPoolSize)
	{
		this.statementPoolSize = statementPoolSize;
	}
	
}
