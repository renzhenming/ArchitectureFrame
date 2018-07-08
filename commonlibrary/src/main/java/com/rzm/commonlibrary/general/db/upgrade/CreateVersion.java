package com.rzm.commonlibrary.general.db.upgrade;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;


public class CreateVersion {
    /**
     * 版本信息
     */
    private String version;

    /**
     * 创建数据库表脚本
     */
    private List<CreateDb> createDbs;

    public CreateVersion(Element ele) {
        version = ele.getAttribute(SqlConstant.KEY_DB_VERSION);

        {
            createDbs = new ArrayList<CreateDb>();
            NodeList cs = ele.getElementsByTagName(SqlConstant.KEY_CREATE_DB);
            for (int i = 0; i < cs.getLength(); i++) {
                Element ci = (Element) (cs.item(i));
                CreateDb cd = new CreateDb(ci);
                this.createDbs.add(cd);
            }
        }
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<CreateDb> getCreateDbs() {
        return createDbs;
    }

    public void setCreateDbs(List<CreateDb> createDbs) {
        this.createDbs = createDbs;
    }

}
