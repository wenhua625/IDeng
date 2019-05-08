package com.ripple.datasource.dialect;

public class InformixDialect implements Dialect
{
	public boolean supportsCommitAndRollback()
	{
		return true;
	}

	public boolean supportsSavePoints()
	{
		return false;
	}

	public boolean supportPageLimit()
	{
		return false;
	}
	
	public String getPageString( String sql, int startIndex, int pageSize )
	{
		return sql;
	}
    
    public String getLockTableString( String tableName )
    {
        return new StringBuffer("LOCK TABLE ").append( tableName).append(" IN EXCLUSIVE MODE").toString();
    }

    public String getUnLockTableString()
    {
        return null;
    }
}
