package com.ripple.web;

import java.util.LinkedList;

import com.ripple.module.MyDataTable;

public class DisplayTable
{
	private String pageTitle; 
	
	private String navigation;
	
	private MyDataTable dataTable;
	
	private LinkedList pageActions;
	
	private LinkedList rowActions;
	
	private LinkedList showColumns;
	
	public DisplayTable()
	{
		pageActions = new LinkedList();
		rowActions = new LinkedList();
		showColumns = new LinkedList();
	}
	
	public DisplayTable( MyDataTable dataTable )
	{
		this.dataTable = dataTable;
		pageActions = new LinkedList();
		rowActions = new LinkedList();
		showColumns = new LinkedList();
	}

	public MyDataTable getDataTable()
	{
		return dataTable;
	}

	public void setDataTable( MyDataTable dataTable )
	{
		this.dataTable = dataTable;
	}

	public String getNavigation()
	{
		return navigation;
	}

	public void setNavigation(String navigation)
	{
		this.navigation = navigation;
	}

	public LinkedList getPageActions()
	{
		return pageActions;
	}

	public void addPageAction( LinkAction action )
	{
		pageActions.add( action );
	}

	public String getPageTitle()
	{
		return pageTitle;
	}

	public void setPageTitle(String pageTitle)
	{
		this.pageTitle = pageTitle;
	}

	public LinkedList getRowActions()
	{
		return rowActions;
	}

	public void addRowAction(LinkAction action)
	{
		rowActions.add( action );
	}

	public LinkedList getShowColumns()
	{
		return showColumns;
	}

	public void addShowColumn( TableColumn column )
	{
		showColumns.add( column );
	}
}
