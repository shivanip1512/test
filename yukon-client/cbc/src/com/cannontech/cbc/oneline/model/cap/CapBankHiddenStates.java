package com.cannontech.cbc.oneline.model.cap;

import com.cannontech.cbc.oneline.elements.HiddenTextElement;
import com.cannontech.cbc.oneline.model.HiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.oneline.tag.CBCTagHandler;
import com.cannontech.cbc.oneline.tag.OnelineTags;
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
        String name = "CapState_" + getCurrentCapIdFromMessage();
        HiddenTextElement stateInfo = new HiddenTextElement("HiddenTextElement",
                                                            name);
        stateInfo.addProperty("isDisable", isDisabled().toString());
        stateInfo.addProperty("isOVUVDis", String.valueOf(isOVUVDisabled()));
        stateInfo.addProperty("isStandalone", String.valueOf(isStandalone()));
        String standAloneReason = CBCTagHandler.getReason(OnelineTags.TAGGRP_CB_OPERATIONAL_STATE,
                                                          getCurrentCapIdFromMessage());
        stateInfo.addProperty("standAloneReason", standAloneReason);
        String disableReason = CBCTagHandler.getReason(OnelineTags.TAGGRP_ENABLEMENT,
                                                       getCurrentCapIdFromMessage());
        stateInfo.addProperty("disableCapReason", disableReason);
        stateInfo.addProperty("paoName", parent.getStreamable().getCcName());
        graph.add(stateInfo);

    }

    protected boolean isStandalone() {
        return parent.isStandalone();
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
