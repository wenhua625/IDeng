package com.ripple.datasource.dialect;

public class OracleDialect implements Dialect {
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
		sql = sql.trim();
		StringBuffer pagingSelect = new StringBuffer( sql.length() + 100 );
		if ( startIndex > 0 )
		{
			pagingSelect.append( "select * from ( select row_.*, rownum rownum_ from ( " );
		} else
		{
			pagingSelect.append( "select * from ( " );
		}
		pagingSelect.append( sql );
		if ( startIndex > 0 )
		{
			pagingSelect.append( " ) row_ ) where rownum_ <= ").append( startIndex + pageSize ).append(" and rownum_ > ?" ).append( startIndex + 1 );
		} else
		{
			pagingSelect.append( " ) where rownum <= " ).append( startIndex + pageSize );
		}

		return pagingSelect.toString();
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
