package com.ripple.datasource.dialect;

public class DB2Dialect implements Dialect
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
		int startOfSelect = sql.toUpperCase().indexOf( "SELECT" );

		StringBuffer pagingSelect = new StringBuffer( sql.length() + 100 )
		        .append( sql.substring( 0, startOfSelect ) ) //add the comment
				.append( "SELECT * FROM ( SELECT " ) //nest the main query in an outer select
				.append( getRowNumber( sql ) ); //add the rownnumber bit into the outer query select list
		if ( hasDistinct( sql ) )
		{
			pagingSelect.append( " row_.* FROM ( " ) //add another (inner) nested select
					    .append( sql.substring( startOfSelect ) ) //add the main query
					    .append( " ) as row_" ); //close off the inner nested select
		} else
		{
			pagingSelect.append( sql.substring( startOfSelect + 6 ) ); //add the main query
		}

		pagingSelect.append( " ) as temp_ WHERE rownumber_ " );
		pagingSelect.append( "BETWEEN ").append( startIndex + 1 ).append(" AND " ).append( startIndex + pageSize );
		return pagingSelect.toString();
	}
	
	private String getRowNumber( String sql )
	{
		StringBuffer rownumber = new StringBuffer( 50 ).append( "rownumber() over(" );

		int orderByIndex = sql.toUpperCase().indexOf( "ORDER BY" );

		if ( orderByIndex > 0 && !hasDistinct( sql ) )
		{
			rownumber.append( sql.substring( orderByIndex ) );
		}

		rownumber.append( ") as rownumber_," );

		return rownumber.toString();
	}
	
	private static boolean hasDistinct( String sql )
	{
		return sql.toUpperCase().indexOf( "SELECT DISTINCT" ) >= 0;
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
