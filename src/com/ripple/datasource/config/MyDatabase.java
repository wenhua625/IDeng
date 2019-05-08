package com.ripple.datasource.config;

public class MyDatabase
{
	private String driver;

	private String URL;

	private String user;

	private String Password;

	private String encoding;
	
	public MyDatabase()
	{
	}
	
	public String getConnectionURL()
	{
		return URL;
	}
	
	public void setConnectionURL(String connectionURL)
	{
		this.URL = connectionURL;
	}
	
	public String getDriverClass()
	{
		return driver;
	}
	
	public void setDriverClass(String driverClass)
	{
		this.driver = driverClass;
	}
	
	public String getPassword()
	{
		return Password;
	}
	
	public void setPassword(String password)
	{
		Password = password;
	}
	
	public String getUser()
	{
		return user;
	}
	
	public void setUser(String user)
	{
		this.user = user;
	}
	
	public String getEncoding()
	{
		return encoding;
	}
	
	public void setEncoding(String encoding)
	{
		this.encoding = encoding;
	}
}
