package com.rzm.commonlibrary.general.db.upgrade;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class UpdateStep {
    /**
     * 旧版本
     */
    private String versionFrom;

    /**
     * 新版本
     */
    private String versionTo;

    /**
     * 更新数据库脚本
     */
    private List<UpdateDb> updateDbs;

    // ==================================================

    public UpdateStep(Element ele) {
        //就像findViewById一样获取这个节点的属性attribute
        versionFrom = ele.getAttribute(SqlConstant.KEY_VERSION_FROM);
        versionTo = ele.getAttribute(SqlConstant.KEY_VERSION_TO);
        updateDbs = new ArrayList<UpdateDb>();

        //updateDb是一个跟updateStep一样的元素Element，这里会获取
        // 到所有同类型的元素组成的集合
        NodeList dbs = ele.getElementsByTagName(SqlConstant.KEY_UPDATE_DB);
        for (int i = 0; i < dbs.getLength(); i++) {
            //得到每一个updateDb元素封装成UpdateDb对象
            Element db = (Element) (dbs.item(i));
            UpdateDb updateDb = new UpdateDb(db);
            //加入集合
            this.updateDbs.add(updateDb);
        }
    }

    public List<UpdateDb> getUpdateDbs() {
        return updateDbs;
    }

    public void setUpdateDbs(List<UpdateDb> updateDbs) {
        this.updateDbs = updateDbs;
    }

    public String getVersionFrom() {
        return versionFrom;
    }

    public void setVersionFrom(String versionFrom) {
        this.versionFrom = versionFrom;
    }

    public String getVersionTo() {
        return versionTo;
    }

    public void setVersionTo(String versionTo) {
        this.versionTo = versionTo;
    }

}
