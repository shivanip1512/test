package com.cannontech.cbc.oneline.model.cap;

import java.util.List;

import com.cannontech.cbc.dao.CapbankCommentDao;
import com.cannontech.cbc.oneline.elements.HiddenTextElement;
import com.cannontech.cbc.oneline.model.HiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.tag.CBCTagHandler;
import com.cannontech.cbc.oneline.tag.OnelineTags;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.spring.YukonSpringHook;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class CapBankHiddenStates extends LxAbstractView implements HiddenStates {

    LxGraph graph;
    OnelineCap parent;
    

    public CapBankHiddenStates(LxGraph g, OnelineObject p) {
        graph = g;
        parent = (OnelineCap) p;
    }

    public OnelineObject getParentOnelineObject() {
        return parent;
    }

    public void setParentOnelineObject(OnelineObject parent) {
        this.parent = (OnelineCap) parent;
    }

    public void draw() {
        addStateInfo();

    }

    public void addStateInfo() {
        String elementID = "CapState_" + getCurrentCapIdFromMessage();
        String disableReason = CBCTagHandler.getReason(OnelineTags.TAGGRP_ENABLEMENT,
                                                       getCurrentCapIdFromMessage(), parent.getPointCache());
        String disableCapOVUVReason = CBCTagHandler.getReason(OnelineTags.TAGGRP_OVUV_ENABLEMENT,
                                                              getCurrentCapIdFromMessage(), parent.getPointCache());
        String standAloneReason = CBCTagHandler.getReason(OnelineTags.TAGGRP_CB_OPERATIONAL_STATE,
                                                          getCurrentCapIdFromMessage(), parent.getPointCache());

        HiddenTextElement stateInfo = new HiddenTextElement("HiddenTextElement",
                                                            elementID);
        stateInfo.addProperty("isDisable", isDisabled().toString());
        boolean ovuvDisabled = isOVUVDisabled();
        stateInfo.addProperty("isOVUVDis", String.valueOf(ovuvDisabled));
        boolean standalone = isStandalone();
        stateInfo.addProperty("isStandalone", String.valueOf(standalone));
        stateInfo.addProperty("standAloneReason", standAloneReason);
        stateInfo.addProperty("disableCapReason", disableReason);
        stateInfo.addProperty("disableCapOVUVReason", disableCapOVUVReason);

        stateInfo.addProperty("paoName", parent.getStreamable().getCcName());
        Integer id = parent.getStreamable().getControlDeviceID();
        LiteYukonPAObject lite = DaoFactory.getPaoDao().getLiteYukonPAO(id);
        boolean isTwoWay = CBCUtils.isTwoWay( lite );
        stateInfo.addProperty("scanOptionDis", "" + !isTwoWay);

        /*Start*************************/
            //Adding The last 5 comments to the Pop-up.
        CapbankCommentDao ccDao = (CapbankCommentDao) YukonSpringHook.getBean("capbankCommentDao");
        List<String> lastFive = ccDao.getLastFiveByPaoId( parent.getPaoId() );
        
        
        
        String fiveToAdd = "";
        for( String str : lastFive )
        {
            //TODO going to want to use a delimeter and escapes... later.
            //TODO If more than ...  30 characters replace the rest with ...
            fiveToAdd += str + ";";
        }
        
        
        stateInfo.addProperty("capbankComments", fiveToAdd);
        /*End*************************/
        
        graph.add(stateInfo);

    }

    protected boolean isStandalone() {
        return parent.isStandalone();
    }
    protected boolean isSwitched() {
        return parent.isSwitched();
    }
    protected boolean isFixed() {
        return parent.isFixed();
    }
    protected boolean isUninstalled() {
        return parent.isUninstalled();
    }
    protected boolean isOVUVDisabled() {
        return parent.isOVUVDisabled();
    }

    protected Boolean isDisabled() {
        return parent.isDisabled();
    }

    Integer getCurrentCapIdFromMessage() {

        return parent.getCurrentCapIdFromMessage();
    }

    public LxGraph getGraph() {
        return graph;
    }

    public void setGraph(LxGraph graph) {
        this.graph = graph;
    }

}
