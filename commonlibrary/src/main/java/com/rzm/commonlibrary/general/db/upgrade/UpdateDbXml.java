package com.rzm.commonlibrary.general.db.upgrade;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class UpdateDbXml {
    /**
     * 升级脚本列表
     */
    private List<UpdateStep> updateSteps;

    /**
     * 升级版本
     */
    private List<CreateVersion> createVersions;

    public UpdateDbXml(Document document) {
        {
            // 获取到xml中所有updateStep节点组成的集合
            NodeList updateSteps = document.getElementsByTagName(SqlConstant.KEY_UPDATE_STEP);
            this.updateSteps = new ArrayList<UpdateStep>();
            for (int i = 0; i < updateSteps.getLength(); i++) {
                //for循环遍历每一个updateStep节点，强转成Element对象
                Element ele = (Element) (updateSteps.item(i));
                //把每一个Element封装成Java对象UpdateStep
                UpdateStep step = new UpdateStep(ele);
                //加入集合
                this.updateSteps.add(step);
            }
        }
        {
            /**
             * 获取各升级版本
             */
            NodeList createVersions = document.getElementsByTagName(SqlConstant.KEY_CREATE_VERSION);
            this.createVersions = new ArrayList<CreateVersion>();
            for (int i = 0; i < createVersions.getLength(); i++) {
                Element ele = (Element) (createVersions.item(i));
                //封装成CreateVersion对象
                CreateVersion cv = new CreateVersion(ele);
                this.createVersions.add(cv);
            }
        }
    }

    public List<UpdateStep> getUpdateSteps() {
        return updateSteps;
    }

    public void setUpdateSteps(List<UpdateStep> updateSteps) {
        this.updateSteps = updateSteps;
    }

    public List<CreateVersion> getCreateVersions() {
        return createVersions;
    }

    public void setCreateVersions(List<CreateVersion> createVersions) {
        this.createVersions = createVersions;
    }

}
