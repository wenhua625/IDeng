package com.ripple.datasource.dialect;

public class MSSQLDialect implements Dialect
{
	public boolean supportsCommitAndRollback() 
	{
		return false;
	}

	public boolean supportsSavePoints() 
	{
		return false;
	}
	
	public boolean supportPageLimit() 
	{
		return false;
	}
	
	public String getPageString(String sql, int startIndex, int pageSize) 
	{
		return sql;
	}
    
    public String getLockTableString( String tableName )
    {
        //return new StringBuffer("LOCK TABLE ").append( tableName).append(" IN EXCLUSIVE MODE").toString();
    	return null;
    }

    public String getUnLockTableString()
    {
        return null;
    }
}
