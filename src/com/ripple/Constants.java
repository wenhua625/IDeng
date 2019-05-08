package com.ripple;

public interface Constants
{
	//数据库类型
	public static final String DATABASE_INFORMIX = "informix";
	public static final String DATABASE_HSQLDB = "hsqldb";
	public static final String DATABASE_ORACLE = "oracle";
	public static final String DATABASE_SQLSERVER = "sqlserver";
	public static final String DATABASE_DB2 = "db2";
	public static final String DATABASE_SYBASE = "sybase";
	public static final String DATABASE_MYSQL = "mysql";
	public static final String DATABASE_POSTGRESQL = "postgresql";
	//数据库连接缓冲类型
    public static int StatementLevel  = 0;
    public static int ConnectionLevel = 1;
    //事务类型
    public static int TRANSACTION_JDBC = 0;
    public static int TRANSACTION_JTA  = 1;
    //事务隔离级别
    public static final int ISOLATION_READ_UNCOMMITTED = 1;
    public static final int ISOLATION_READ_COMMITTED   = 2;
    public static final int ISOLATION_REPEATABLE_READ  = 4;
    public static final int ISOLATION_SERIALIZABLE     = 8;
}
