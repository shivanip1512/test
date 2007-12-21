package com.cannontech.cbc.oneline.model.sub;

import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.oneline.elements.HiddenTextElement;
import com.cannontech.cbc.oneline.model.AbstractHiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.yukon.cbc.SubBus;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class SubHiddenStates extends AbstractHiddenStates {
    private SubBus subBusMsg;
    private LxGraph graph;
    private OnelineSub parent;
    
    public SubHiddenStates(LxGraph g, OnelineObject o) {
        graph = g;
        parent = (OnelineSub) o;
        subBusMsg = parent.getSubBusMsg();
    }

    public void addStateInfo() {
        final int paoId = subBusMsg.getCcId();
        
        String name = "SubState_" + paoId;
        HiddenTextElement stateInfo = new HiddenTextElement ("HiddenTextElement", name);
        stateInfo.addProperty("isOVUVDis", isOVUVDisabled().toString() );
        stateInfo.addProperty("isDisable", isDisabled().toString() );
        stateInfo.addProperty("isVerify", subBusMsg.getVerificationFlag().toString());
        
        String disableReason = getReason(paoId, CommentAction.DISABLED);
        stateInfo.addProperty("subDisableReason", disableReason);
        
        String disableOVUVReason = getReason(paoId, CommentAction.DISABLED_OVUV, CapControlTypes.CAP_CONTROL_SUBBUS);
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
    
    @Override
    public LxGraph getGraph() {
        return graph;
        
    }

}
