package com.ripple.module;

public class MyMetaData
{
	private String columnName;
	
	private int columnType;
	
	private int columnSize;

	public String getColumnName()
	{
		return columnName;
	}

	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	public int getColumnSize()
	{
		return columnSize;
	}

	public void setColumnSize(int columnSize)
	{
		this.columnSize = columnSize;
	}

	public int getColumnType()
	{
		return columnType;
	}

	public void setColumnType(int columnType)
	{
		this.columnType = columnType;
	}
}
