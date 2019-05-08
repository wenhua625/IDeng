package com.ripple.web;

import java.util.ArrayList;
import java.util.List;

import com.ripple.module.MyDataTable;

public class ActionTable extends DisplayTable
{
	private List actionParam;
	
	public ActionTable()
	{
		super();
		actionParam = new ArrayList();
	}
	
	public ActionTable( MyDataTable dataTable )
	{
		super();
		this.setDataTable( dataTable );
		actionParam = new ArrayList();
	}
	
	public ActionTable( MyDataTable dataTable,List params)
	{
		super();
		this.setDataTable( dataTable );
		this.actionParam = params;
	}

	public List getActionParams()
	{
		return actionParam;
	}

	public void addActionParam( LinkAction param )
	{
		actionParam.add( param );
	}
	
}
