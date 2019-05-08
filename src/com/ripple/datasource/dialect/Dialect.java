package com.ripple.datasource.dialect;

public interface Dialect
{
    
    /**
     * Returns true if the DB supports transactions and savepoints.
     * @return
     */
    public boolean supportsCommitAndRollback();
    /**
     * Returns true if the DB supports transactions and savepoints.
     * @return
     */
    public boolean supportsSavePoints();
    /**
     * Returns true if the db supports page limit.
     * @param
     * @return
     */    
    public boolean supportPageLimit();
    /**
     * Returns lock sql.
     * @param sql, startIndex, pageSize
     * @return
     */ 
    public String getLockTableString( String tableName );
    /**
     * Returns unlock sql.
     * @param sql, startIndex, pageSize
     * @return
     */ 
    public String getUnLockTableString();
    /**
     * Returns Paged sql.
     * @param sql, startIndex, pageSize
     * @return
     */ 
    public String getPageString( String sql, int startIndex, int pageSize );
}
