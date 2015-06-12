package com.cannontech.cbc.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.cbc.exceptions.PAODoesntHaveNameException;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.database.db.capcontrol.CapControlStrategy;

public interface ICapControlModel {


    public abstract HashMap<Integer, CapControlStrategy> getCbcStrategiesMap();

    public abstract void initItem(int id, int type);

    public abstract void initWizard(int paoType);

    public abstract DBPersistent getPAOBase();

    public abstract void clearfaces();

    public abstract void update() throws SQLException;

    public abstract String create() throws PAODoesntHaveNameException;

    public abstract ICBControllerModel getCBControllerEditor();

    public abstract void setCBControllerEditor(ICBControllerModel cbCntrlEditor);

    public abstract boolean isEditingController();

    public abstract void setEditingController(boolean val);

    public abstract String getPAODescLabel();

    public abstract String getParent();

    public abstract void treeSwapAddAction();

    public abstract void treeSwapRemoveAction();

    public abstract void initEditorPanels();

    public abstract void setPaoDescLabel(String string);

    public abstract List<LiteYukonPAObject> getUnassignedBanks();

    public abstract List<LiteYukonPAObject> getUnassignedFeeders();

    public abstract String getChildLabel();

    public abstract void setChildLabel(String string);

    public abstract boolean isControllerCBC();

    public abstract boolean isBankControlPtVisible();

    public abstract String addSchedule();

    public abstract EditorDataModel getWizData();

    public abstract LiteYukonPAObject[] getSubBusList();

    public abstract String getSelectedSubBusFormatString();

    public abstract Boolean getEnableDualBus();

    public abstract void setEnableDualBus(Boolean enableDualBus);

    public abstract boolean isDualSubBusEdited();

    public abstract void setDualSubBusEdited(boolean isDualSubBusEdited);

    public abstract Integer getOldSubBus();

    public abstract void setOldSubBus(Integer oldSubBus);

    public abstract int getSelectedPanelIndex();

    public abstract String getPaoName();

    public abstract LitePoint[] getCapBankPointList();

    public abstract Map<Integer, String> getPointNameMap();

    public abstract Map<Integer, String> getPaoNameMap();

    public abstract EditorDataModel getDataModel();

    public abstract void setDataModel(EditorDataModel dataModel);
    
    public List<CapControlStrategy> getAllCBCStrats();

}