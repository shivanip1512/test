package com.cannontech.cbc.oneline.model.cap;

import java.util.List;

import com.cannontech.cbc.dao.CommentAction;
import com.cannontech.cbc.model.CapControlComment;
import com.cannontech.cbc.oneline.elements.HiddenTextElement;
import com.cannontech.cbc.oneline.model.AbstractHiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.cbc.util.CBCUtils;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.data.pao.CapControlTypes;
import com.loox.jloox.LxGraph;

@SuppressWarnings("serial")
public class CapBankHiddenStates extends AbstractHiddenStates {

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
        final int paoId = getCurrentCapIdFromMessage();

        String elementID = "CapState_" + paoId;
        HiddenTextElement stateInfo = new HiddenTextElement("HiddenTextElement", elementID);
        stateInfo.addProperty("isDisable", isDisabled().toString());
        stateInfo.addProperty("isOVUVDis", Boolean.toString(isOVUVDisabled()));
        stateInfo.addProperty("isStandalone", Boolean.toString(isStandalone()));
        stateInfo.addProperty("isFixed", Boolean.toString(isFixed()));
        stateInfo.addProperty("isSwitched", Boolean.toString(isSwitched()));
        
        String disableReason = getReason(paoId, CommentAction.DISABLED);
        stateInfo.addProperty("disableCapReason", disableReason);
        
        String disableCapOVUVReason = getReason(paoId, CommentAction.DISABLED_OVUV, CapControlTypes.CAP_CONTROL_CAPBANK);
        stateInfo.addProperty("disableCapOVUVReason", disableCapOVUVReason);
        
        String standAloneReason = getReason(paoId, CommentAction.STANDALONE_REASON);
        stateInfo.addProperty("standAloneReason", standAloneReason);

        stateInfo.addProperty("paoName", parent.getStreamable().getCcName());
        Integer id = parent.getStreamable().getControlDeviceID();
        LiteYukonPAObject lite = DaoFactory.getPaoDao().getLiteYukonPAO(id);
        boolean isTwoWay = CBCUtils.isTwoWay( lite );
        stateInfo.addProperty("scanOptionDis", "" + !isTwoWay);

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

    @Override
    public LxGraph getGraph() {
        return graph;
    }

    public void setGraph(LxGraph graph) {
        this.graph = graph;
    }

}
