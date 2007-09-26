package com.cannontech.cbc.oneline.model.cap;

import java.sql.Connection;
import java.sql.SQLException;

import com.cannontech.cbc.oneline.elements.HiddenTextElement;
import com.cannontech.cbc.oneline.model.HiddenStates;
import com.cannontech.cbc.oneline.model.OnelineObject;
import com.cannontech.clientutils.CTILogger;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.loox.jloox.LxGraph;

public class CapBankAdditionalInfo implements HiddenStates {

    public static final String HIDDEN_NAME_PREFIX = "CapHiddenInfo_";
    LxGraph graph;
    OnelineCap parent;
    

    public CapBankAdditionalInfo(LxGraph g, OnelineObject p) {
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
        String name = HIDDEN_NAME_PREFIX + getCurrentCapIdFromMessage();
        HiddenTextElement stateInfo = new HiddenTextElement("HiddenTextElement",
                                                            name);
        
        CapBankAdditional info = new CapBankAdditional();
        info.setDeviceID(getCurrentCapIdFromMessage());
        populateInfo(info);
        LiteYukonPAObject lite = DaoFactory.getPaoDao().getLiteYukonPAO(getCurrentCapIdFromMessage());
        //all these property names should be kept in synch with
        //cc.js REF_LABELS global var
        stateInfo.addProperty("maintArea", info.getMaintAreaID().toString());
        stateInfo.addProperty("poleNum", info.getPoleNumber().toString());
        stateInfo.addProperty("latit",  info.getLatit().toString());        
        stateInfo.addProperty("longit", info.getLongtit().toString());
        stateInfo.addProperty("config",info.getCapBankConfig().toString());
        stateInfo.addProperty("medium",info.getCommMedium());
        stateInfo.addProperty("strength", info.getCommStrengh().toString());
        stateInfo.addProperty("isAntenna", info.getExtAntenna());
        stateInfo.addProperty("antType", info.getAntennaType());
        stateInfo.addProperty("maintVis", info.getLastInspVisit().toString());
        stateInfo.addProperty("inspVis", info.getLastInspVisit().toString());
        stateInfo.addProperty("opRstDt", info.getOpCountResetDate().toString());
        stateInfo.addProperty("potentTrfmr", info.getPotentTransformer());
        stateInfo.addProperty("maintReqPend", info.getMaintReqPending());
        stateInfo.addProperty("otrCmnts", info.getOtherComments());
        stateInfo.addProperty("opTmCmnts", info.getOpTeamComments());
        stateInfo.addProperty("cbcInsDt", info.getCbcBattInstallDate().toString());
        stateInfo.addProperty("address",  lite.getPaoDescription()); 
        stateInfo.addProperty("dir",  info.getDriveDir()); 
        
        graph.add(stateInfo);

    }

    private void populateInfo(CapBankAdditional info) {
        Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        info.setDbConnection(connection);
        try {
            info.retrieve();
        } catch (SQLException e) {
            CTILogger.error(e);
        }
        if (connection != null) {try {
            connection.close();
        } catch (SQLException e) {
            CTILogger.error(e);
        }}
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
