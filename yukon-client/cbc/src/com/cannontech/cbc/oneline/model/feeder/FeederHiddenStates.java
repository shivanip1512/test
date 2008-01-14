package com.cannontech.cbc.oneline.model.feeder;

import java.util.List;

import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.cbc.oneline.elements.HiddenTextElement;
import com.cannontech.cbc.oneline.model.AbstractHiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.database.data.pao.CapControlTypes;
import com.cannontech.yukon.cbc.Feeder;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class FeederHiddenStates extends AbstractHiddenStates {

    private LxGraph graph;
    private OnelineFeeder parent;

    public FeederHiddenStates(LxGraph graph, OnelineFeeder parent) {
        this.graph = graph;
        this.parent = parent;
    }

    public void addStateInfo() {
        final int paoId = getCurrentFeederIdFromMessage();
        
        String name = "FeederState_" + paoId;
        HiddenTextElement stateInfo = new HiddenTextElement("HiddenTextElement", name);
        stateInfo.addProperty("isDisable", isDisabled().toString() );
        stateInfo.addProperty("isOVUVDis", isOVUVDisabled().toString() );

        String disableReason = getReason(paoId, CommentAction.DISABLED);
        stateInfo.addProperty("disableFdrReason", disableReason);

        String disableOVUVReason = getReason(paoId, CommentAction.DISABLED_OVUV, CapControlTypes.CAP_CONTROL_FEEDER);
        stateInfo.addProperty("disableOVUVFdrReason", disableOVUVReason);
        
        //Adding The last 5 comments to the Pop-up.
	    List<CapControlComment> lastFive = commentDao.getUserCommentsByPaoId(parent.getPaoId(), 5);
	    
	    StringBuilder fiveToAdd = new StringBuilder("");
	    for (final CapControlComment comment : lastFive) {
	        String text = comment.getComment();
	        fiveToAdd.append(text);
	        fiveToAdd.append(";");
	    }
	    stateInfo.addProperty("capControlComments", fiveToAdd.toString());
        
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

    @Override
    public LxGraph getGraph() {
        return graph;

    }

}
