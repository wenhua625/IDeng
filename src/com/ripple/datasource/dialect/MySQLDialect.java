package com.ripple.datasource.dialect;

public class MySQLDialect implements Dialect
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
		return true;
	}
	
	public String getPageString( String sql, int startIndex, int pageSize )
	{
		String limitStr = null;
		if( startIndex > 0 )
		{
			limitStr = new StringBuffer(" limit ").append( startIndex ).append(",").append( pageSize ).toString();
		}else
		{
			limitStr = new StringBuffer(" limit ").append( pageSize ).toString();
		}
		return new StringBuffer( sql.length() + 20 ).append( sql ).append( limitStr ).toString();
	}

    public String getLockTableString( String tableName )
    {
        return new StringBuffer("LOCK TABLE ").append( tableName ).append( " WRITE").toString();
    }

    public String getUnLockTableString()
    {
        return "UNLOCK TABLES";
    }

}
