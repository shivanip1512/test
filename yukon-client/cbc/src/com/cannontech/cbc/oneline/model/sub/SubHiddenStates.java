package com.cannontech.cbc.oneline.model.sub;

import com.cannontech.cbc.oneline.elements.HiddenTextElement;
import com.cannontech.cbc.oneline.model.HiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.tag.CBCTagHandler;
import com.cannontech.cbc.oneline.tag.OnelineTags;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class SubHiddenStates extends LxAbstractView implements HiddenStates {

    private SubBus subBusMsg;
    private LxGraph graph;
    private OnelineSub parent;
    
    public SubHiddenStates(LxGraph g, OnelineObject o) {
        graph = g;
        parent = (OnelineSub) o;
        subBusMsg = parent.getSubBusMsg();
    }

    public void addStateInfo() {
        String name = "SubState_" + subBusMsg.getCcId();
        HiddenTextElement stateInfo = new HiddenTextElement ("HiddenTextElement", name);
        stateInfo.addProperty("isOVUVDis", isOVUVDisabled().toString() );
        stateInfo.addProperty("isDisable", isDisabled().toString() );
        stateInfo.addProperty("isVerify", subBusMsg.getVerificationFlag().toString());
        String disableReason = CBCTagHandler.getReason(OnelineTags.TAGGRP_ENABLEMENT,
                                                                                 subBusMsg.getCcId(), parent.getPointCache());
        String disableOVUVReason = CBCTagHandler.getReason(OnelineTags.TAGGRP_OVUV_ENABLEMENT,
                                                       subBusMsg.getCcId(), parent.getPointCache());
        stateInfo.addProperty("subDisableReason", disableReason);
        stateInfo.addProperty("subDisableOVUVReason", disableOVUVReason);

        graph.add( stateInfo);

    }

    public Boolean isDisabled() {
        return subBusMsg.getCcDisableFlag();
    }
    
    public Boolean isOVUVDisabled() {
        return subBusMsg.getOvUvDisabledFlag();
    }

    public void draw() {
        addStateInfo();
    }

    public OnelineObject getParentOnelineObject() {
        return parent;
    }

    public void setGraph(LxGraph g) {
        graph = g;
    }

    public void setParentOnelineObject(OnelineObject p) {
        parent = (OnelineSub) p;
    }
    public LxGraph getGraph() {
        return graph;
        
    }

}
