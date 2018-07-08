package com.rzm.commonlibrary.general.db.upgrade;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class UpdateDb {
    /**
     * 数据库名称
     */
    private String dbName;

    /**
     *
     */
    private List<String> sqlBefores;

    /**
     *
     */
    private List<String> sqlAfters;

    public UpdateDb(Element ele) {
        //获取name属性
        dbName = ele.getAttribute(SqlConstant.KEY_DB_NAME);

        sqlBefores = new ArrayList<String>();
        sqlAfters = new ArrayList<String>();

        //这个元素中还包含有别的元素节点，使用同样的方式获取
        {
            NodeList sqls = ele.getElementsByTagName(SqlConstant.KEY_SQL_BEFORE);
            for (int i = 0; i < sqls.getLength(); i++) {
                //获取到这个节点中的文字内容
                String sql_before = sqls.item(i).getTextContent();
                //存入集合
                this.sqlBefores.add(sql_before);
            }
        }
        //同上
        {
            NodeList sqls = ele.getElementsByTagName(SqlConstant.KEY_SQL_AFTER);
            for (int i = 0; i < sqls.getLength(); i++) {
                String sql_after = sqls.item(i).getTextContent();
                this.sqlAfters.add(sql_after);
            }
        }

    }

    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }

    public List<String> getSqlBefores() {
        return sqlBefores;
    }

    public void setSqlBefores(List<String> sqlBefores) {
        this.sqlBefores = sqlBefores;
    }

    public List<String> getSqlAfters() {
        return sqlAfters;
    }

    public void setSqlAfters(List<String> sqlAfters) {
        this.sqlAfters = sqlAfters;
    }
}
