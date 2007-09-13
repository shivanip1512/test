package com.cannontech.cbc.oneline.model.feeder;

import com.cannontech.cbc.oneline.elements.HiddenTextElement;
import com.cannontech.cbc.oneline.model.HiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.tag.CBCTagHandler;
import com.cannontech.cbc.oneline.tag.OnelineTags;
import com.cannontech.yukon.cbc.Feeder;
import com.loox.jloox.LxAbstractView;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class FeederHiddenStates extends LxAbstractView implements HiddenStates {

    private LxGraph graph;
    private OnelineFeeder parent;

    public FeederHiddenStates(LxGraph graph, OnelineFeeder parent) {
        this.graph = graph;
        this.parent = parent;
    }

    public void addStateInfo() {
        String name = "FeederState_" + getCurrentFeederIdFromMessage();
        HiddenTextElement stateInfo = new HiddenTextElement("HiddenTextElement",
                                                            name);
        stateInfo.addProperty("isDisable", isDisabled().toString() );
        stateInfo.addProperty("isOVUVDis", isOVUVDisabled().toString() );
        String disableReason = CBCTagHandler.getReason(OnelineTags.TAGGRP_ENABLEMENT,
                                                       getCurrentFeederIdFromMessage());
        String disableOVUVReason = CBCTagHandler.getReason(OnelineTags.TAGGRP_OVUV_ENABLEMENT,
                                                       getCurrentFeederIdFromMessage());
        stateInfo.addProperty("disableFdrReason", disableReason);
        stateInfo.addProperty("disableOVUVFdrReason", disableOVUVReason);
        graph.add(stateInfo);

    }

    public Boolean isDisabled() {
        return getStreamable().getCcDisableFlag();
    }
    
    public Boolean isOVUVDisabled() {
        return getStreamable().getOvUvDisabledFlag();
    }

    private Feeder getStreamable() {
        return parent.getStreamable();
    }

    private Integer getCurrentFeederIdFromMessage() {
        return parent.getStreamable().getCcId();
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
        parent = (OnelineFeeder) p;

    }

    public LxGraph getGraph() {
        return graph;

    }

}
