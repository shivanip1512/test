package mock;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cannontech.cbc.db.CapControlFactory;
import com.cannontech.cbc.model.EditorDataModel;
import com.cannontech.cbc.model.ICBControllerModel;
import com.cannontech.cbc.model.ICapControlModel;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.database.db.DBPersistent;

public class MockCapControlForm implements ICapControlModel {

    private int paoType;
    private int itemID;
    private MockCBCWizardModel wizData;

    public void addSchedule() {
        throw new UnsupportedOperationException();

    }

/*    public void capBankPointClick(ActionEvent ae) {
        
    }*/

    public void clearfaces() {
        throw new UnsupportedOperationException();

    }

    public String create() {
        MockCBCWizardModel wizData = (MockCBCWizardModel) getWizData();
        itemID = CapControlFactory.createCapControlObject(wizData);
        return new Integer (itemID).toString();

    }


    public void createStrategy() {
        throw new UnsupportedOperationException();

    }

    public void deleteStrategy() {
        throw new UnsupportedOperationException();

    }

    public ICBControllerModel getCBControllerEditor() {
        throw new UnsupportedOperationException();
    }

    public LitePoint[] getCapBankPointList() {
        throw new UnsupportedOperationException();
    }
/*
    public TreeNode getCapBankPoints() {
        throw new UnsupportedOperationException();
    }

    public SelectItem[] getCbcStrategies() {
        throw new UnsupportedOperationException();
    }*/

    public HashMap getCbcStrategiesMap() {
        throw new UnsupportedOperationException();
    }

    public String getChildLabel() {
        throw new UnsupportedOperationException();
    }

/*    public SelectItem[] getControlMethods() {
        throw new UnsupportedOperationException();
    }*/

    public EditorDataModel getCurrentStratModel() {
        throw new UnsupportedOperationException();
    }

    public int getCurrentStrategyID() {
        throw new UnsupportedOperationException();
    }

    public EditorDataModel getDataModel() {
        throw new UnsupportedOperationException();
    }

/*    public HtmlTree getDualBusSwitchPointTree() {
        throw new UnsupportedOperationException();
    }*/

    public Boolean getEnableDualBus() {
        throw new UnsupportedOperationException();
    }

/*    public SelectItem[] getKwkvarPaos() {
        throw new UnsupportedOperationException();
    }*/

/*    public SelectItem[] getKwkvarPoints() {
        throw new UnsupportedOperationException();
    }*/

    public Map getOffsetMap() {
        throw new UnsupportedOperationException();
    }

    public Integer getOldSubBus() {
        throw new UnsupportedOperationException();
    }

    public DBPersistent getPAOBase() {
        throw new UnsupportedOperationException();
    }

    public String getPAODescLabel() {
        throw new UnsupportedOperationException();
    }

    public String getPaoName() {
        throw new UnsupportedOperationException();
    }

    public Map getPaoNameMap() {
        throw new UnsupportedOperationException();
    }

    public String getParent() {
        throw new UnsupportedOperationException();
    }

    public Map getPointNameMap() {
        throw new UnsupportedOperationException();
    }

/*    public SelectItem[] getScheduleRepeatTime() {
        throw new UnsupportedOperationException();
    }*/

    public int getSelectedPanelIndex() {
        throw new UnsupportedOperationException();
    }

    public String getSelectedSubBusFormatString() {
        throw new UnsupportedOperationException();
    }

    public String getSelectedTwoWayPointsFormatString() {
        throw new UnsupportedOperationException();
    }

    public String[] getStratDaysOfWeek() {
        throw new UnsupportedOperationException();
    }

    public LiteYukonPAObject[] getSubBusList() {
        throw new UnsupportedOperationException();
    }

/*    public TreeNode getSwitchPointList() {
        throw new UnsupportedOperationException();
    }*/

/*    public SelectItem[] getTimeInterval() {
        throw new UnsupportedOperationException();
    }*/

    public List getUnassignedBanks() {
        throw new UnsupportedOperationException();
    }

    public List getUnassignedFeeders() {
        throw new UnsupportedOperationException();
    }

/*    public TreeNode getVarTreeData() {
        throw new UnsupportedOperationException();
    }

    public TreeNode getVoltTreeData() {
        throw new UnsupportedOperationException();
    }*/

/*    public TreeNode getWattTreeData() {
        throw new UnsupportedOperationException();
    }
*/
    public EditorDataModel getWizData() {

        if (wizData == null)
            wizData = new MockCBCWizardModel();

        return wizData;
    }

    public void initEditorPanels() {
        throw new UnsupportedOperationException();

    }

    public void initItem(int id, int type) {
        throw new UnsupportedOperationException();

    }

    public void initWizard(int type) {
        paoType = type;
        ((MockCBCWizardModel) getWizData()).setWizPaoType(paoType);

    }

    public boolean isBankControlPtVisible() {
        throw new UnsupportedOperationException();
    }

    public boolean isControllerCBC() {
        throw new UnsupportedOperationException();
    }

    public boolean isDualSubBusEdited() {
        throw new UnsupportedOperationException();
    }

    public boolean isEditingCBCStrategy() {
        throw new UnsupportedOperationException();
    }

    public boolean isEditingController() {
        throw new UnsupportedOperationException();
    }

    public boolean isSwitchPointEnabled() {
        throw new UnsupportedOperationException();
    }

    public boolean isVoltageControl() {
        throw new UnsupportedOperationException();
    }

/*    public void kwkvarPaosChanged(ValueChangeEvent ev) {
        throw new UnsupportedOperationException();

    }

    public void newStrategySelected(ValueChangeEvent vce) {
        throw new UnsupportedOperationException();

    }

    public void paoClick(ActionEvent ae) {
        throw new UnsupportedOperationException();

    }*/

    public void resetForm() {
        throw new UnsupportedOperationException();

    }

/*    public void selectedAltSubBusClick(ActionEvent ae) {
        throw new UnsupportedOperationException();

    }

    public void selectedTwoWayPointClick(ActionEvent ae) {
        throw new UnsupportedOperationException();

    }*/

    public void setCBControllerEditor(ICBControllerModel cbCntrlEditor) {
        throw new UnsupportedOperationException();

    }

    public void setChildLabel(String string) {
        throw new UnsupportedOperationException();

    }

    public void setDataModel(EditorDataModel dataModel) {
        throw new UnsupportedOperationException();

    }

/*    public void setDualBusSwitchPointTree(HtmlTree tree) {
        throw new UnsupportedOperationException();

    }*/

    public void setDualSubBusEdited(boolean isDualSubBusEdited) {
        throw new UnsupportedOperationException();

    }

    public void setEditingCBCStrategy(boolean b) {
        throw new UnsupportedOperationException();

    }

    public void setEditingController(boolean val) {
        throw new UnsupportedOperationException();

    }

    public void setEnableDualBus(Boolean enableDualBus) {
        throw new UnsupportedOperationException();

    }

    public void setOffsetMap(Map offsetMap) {
        throw new UnsupportedOperationException();

    }

    public void setOldSubBus(Integer oldSubBus) {
        throw new UnsupportedOperationException();

    }

    public void setPaoDescLabel(String string) {
        throw new UnsupportedOperationException();

    }

    public void setPaoNameMap(Map m) {
        throw new UnsupportedOperationException();

    }

    public void setPointNameMap(Map m) {
        throw new UnsupportedOperationException();

    }

    public void setStratDaysOfWeek(String[] newDaysOfWeek) {
        throw new UnsupportedOperationException();

    }

    public void setSwitchPointEnabled(boolean switchPointEnabled) {
        throw new UnsupportedOperationException();

    }

/*    public void showScanRate(ValueChangeEvent ev) {
        throw new UnsupportedOperationException();

    }*/

/*    public void subBusPAOsClick(ActionEvent ae) {
        throw new UnsupportedOperationException();

    }*/

    public void treeSwapAddAction() {
        throw new UnsupportedOperationException();

    }

    public void treeSwapRemoveAction() {
        throw new UnsupportedOperationException();

    }

/*    public void twoWayPtsTeeClick(ActionEvent ae) {
        throw new UnsupportedOperationException();

    }*/

    public void update() throws SQLException {
        throw new UnsupportedOperationException();

    }

/*    public void varPtTeeClick(ActionEvent ae) {
        throw new UnsupportedOperationException();

    }*/

/*    public void voltPtTeeClick(ActionEvent ae) {
        throw new UnsupportedOperationException();

    }*/

/*    public void wattPtTeeClick(ActionEvent ae) {
        throw new UnsupportedOperationException();

    }*/

}
