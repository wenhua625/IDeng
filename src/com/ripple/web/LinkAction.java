package com.ripple.web;

public class LinkAction
{
	private String linkValue;
	
	private String linkName;
	
	public LinkAction()
	{}
	
	public LinkAction( String linkName,String linkValue )
	{
		this.linkName = linkName;
		this.linkValue = linkValue;
	}
	
	public String getLinkValue()
	{
		return linkValue;
	}

	public void setLinkValue(String linkValue)
	{
		this.linkValue = linkValue;
	}

	public String getShowName()
	{
		return linkName;
	}

	public void setShowName(String linkName)
	{
		this.linkName = linkName;
	}
}
