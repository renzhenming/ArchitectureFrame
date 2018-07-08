package com.rzm.commonlibrary.general.db.upgrade;

/**
 * 定义脚本文件的node
 */
public class SqlConstant {

    /**
     * 在脚本文件中用于设置数据库的名称
     */
    public static final String KEY_DB_NAME = "name";

    /**
     * 创建数据库sql命令
     */
    public static final String KEY_SQL_CREATE_TABLE = "sql_createTable";

    /**
     * 数据库版本
     */
    public static final String KEY_DB_VERSION = "version";
    
    public static final String KEY_CREATE_DB = "createDb";
    public static final String KEY_SQL_BEFORE = "sql_before";
    public static final String KEY_SQL_AFTER = "sql_after";
    public static final String KEY_UPDATE_STEP = "updateStep";
    public static final String KEY_CREATE_VERSION = "createVersion";
    public static final String KEY_VERSION_FROM = "versionFrom";
    public static final String KEY_VERSION_TO = "versionTo";
    public static final String KEY_UPDATE_DB = "updateDb";
}
