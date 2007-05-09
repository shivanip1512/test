package com.cannontech.cbc.model;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;

public interface ICapControlModel {


    public abstract HashMap getCbcStrategiesMap();


    public abstract int getCurrentStrategyID();

    public abstract EditorDataModel getCurrentStratModel();

    //public abstract void newStrategySelected(ValueChangeEvent vce);

    //public abstract TreeNode getVarTreeData();

    //public abstract TreeNode getWattTreeData();

   //public abstract TreeNode getVoltTreeData();

    //public abstract TreeNode getCapBankPoints();

    //public abstract void varPtTeeClick(ActionEvent ae);

    //public abstract void twoWayPtsTeeClick(ActionEvent ae);

/*    public abstract void selectedTwoWayPointClick(ActionEvent ae);

    public abstract void selectedAltSubBusClick(ActionEvent ae);

    public abstract void subBusPAOsClick(ActionEvent ae);

    public abstract void wattPtTeeClick(ActionEvent ae);

    public abstract void voltPtTeeClick(ActionEvent ae);*/

    public abstract void initItem(int id, int type);

    public abstract void initWizard(int paoType);

    public abstract void resetForm();

    public abstract DBPersistent getPAOBase();

    //public abstract void kwkvarPaosChanged(ValueChangeEvent ev);

    public abstract void clearfaces();

    public abstract void update() throws SQLException;

    public abstract String create();

    //public abstract void showScanRate(ValueChangeEvent ev);

    public abstract ICBControllerModel getCBControllerEditor();

    public abstract void setCBControllerEditor(ICBControllerModel cbCntrlEditor);

    public abstract boolean isEditingController();

    public abstract void setEditingController(boolean val);

    public abstract void createStrategy();

    public abstract void deleteStrategy();

    public abstract String getPAODescLabel();

    public abstract String getParent();

    public abstract void treeSwapAddAction();

    public abstract void treeSwapRemoveAction();

    public abstract void initEditorPanels();

    public abstract void setPaoDescLabel(String string);

    public abstract boolean isEditingCBCStrategy();

    public abstract void setEditingCBCStrategy(boolean b);

    //public abstract SelectItem[] getTimeInterval();

    //public abstract SelectItem[] getScheduleRepeatTime();

    public abstract List getUnassignedBanks();

    public abstract List getUnassignedFeeders();

    public abstract String getChildLabel();

    public abstract void setChildLabel(String string);

    public abstract void setStratDaysOfWeek(String[] newDaysOfWeek);

    public abstract String[] getStratDaysOfWeek();

    public abstract boolean isControllerCBC();

    public abstract boolean isBankControlPtVisible();

    public abstract void addSchedule();

    public abstract EditorDataModel getWizData();

    public abstract boolean isVoltageControl();

    public abstract LiteYukonPAObject[] getSubBusList();

    //public abstract TreeNode getSwitchPointList();

    public abstract String getSelectedSubBusFormatString();

    public abstract String getSelectedTwoWayPointsFormatString();

    //public abstract HtmlTree getDualBusSwitchPointTree();

    //public abstract void setDualBusSwitchPointTree(HtmlTree tree);

    public abstract Boolean getEnableDualBus();

    public abstract void setEnableDualBus(Boolean enableDualBus);

    public abstract boolean isDualSubBusEdited();

    public abstract void setDualSubBusEdited(boolean isDualSubBusEdited);

    public abstract Integer getOldSubBus();

    public abstract void setOldSubBus(Integer oldSubBus);

    public abstract Map getOffsetMap();

    public abstract void setOffsetMap(Map offsetMap);

    public abstract int getSelectedPanelIndex();

    public abstract String getPaoName();

    public abstract LitePoint[] getCapBankPointList();

    //public abstract void capBankPointClick(ActionEvent ae);

    public abstract boolean isSwitchPointEnabled();

    public abstract void setSwitchPointEnabled(boolean switchPointEnabled);

    //public abstract SelectItem[] getControlMethods();

    public abstract Map getPointNameMap();

    public abstract void setPointNameMap(Map m);

    public abstract Map getPaoNameMap();

    public abstract void setPaoNameMap(Map m);

    //public abstract void paoClick(ActionEvent ae);

    public abstract EditorDataModel getDataModel();

    public abstract void setDataModel(EditorDataModel dataModel);

}