package com.ripple.web;

public class TableColumn
{
	private String columnTitle;
	
	private String columnName;
	
	private String align;
	
	private String link;
	
	private int length;
	
	public TableColumn()
	{}
	
	public TableColumn(String columnTitle,String columnName,String link,String align,int length)
	{
		this.columnName = columnName;
		this.columnTitle = columnTitle;
		this.align = align;
		this.link=link;
		this.length=length;
	}

	public String getAlign()
	{
		return align;
	}

	public void setAlign(String align)
	{
		this.align = align;
	}

	public String getColumnName()
	{
		return columnName;
	}

	public void setColumnName(String columnName)
	{
		this.columnName = columnName;
	}

	public String getColumnTitle()
	{
		return columnTitle;
	}

	public void setColumnTitle(String columnTitle)
	{
		this.columnTitle = columnTitle;
	}

	public String getLink()
	{
		return link;
	}

	public void setLink(String link)
	{
		this.link = link;
	}

	public int getLength()
	{
		return length;
	}

	public void setLength(int length)
	{
		this.length = length;
	}
}
