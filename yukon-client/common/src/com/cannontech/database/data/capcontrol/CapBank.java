package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.cannontech.capcontrol.BankOpState;
import com.cannontech.capcontrol.CapBankCommunicationMedium;
import com.cannontech.capcontrol.service.ZoneService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.database.db.capcontrol.CCMonitorBankList;
import com.cannontech.database.db.capcontrol.CapBankAdditional;
import com.cannontech.spring.YukonSpringHook;

public class CapBank extends CapControlDeviceBase {
    // OPStates that a CapBank can be in
    public final static String SWITCHED_OPSTATE = BankOpState.SWITCHED.getDbString();
    public final static String FIXED_OPSTATE = BankOpState.FIXED.getDbString();
    public final static String UNINSTALLED_OPSTATE = BankOpState.UNINSTALLED.getDbString();
    public final static String STANDALONE_OPSTATE = BankOpState.STANDALONE.getDbString();

    public final static String[] OPSTATES = { SWITCHED_OPSTATE, FIXED_OPSTATE,
            UNINSTALLED_OPSTATE, STANDALONE_OPSTATE };
    // all control type of switches
    public final static String CONTROL_TYPE_DLC = "CTI DLC";
    public final static String CONTROL_TYPE_PAGING = "CTI Paging";
    public final static String CONTROL_TYPE_TELEMETRIC = "Telemetric";
    public final static String CONTROL_TYPE_FM = "CTI FM";
    public final static String CONTROL_TYPE_FP_PAGING = "FP Paging";

    // all manufactures of switches
    public final static String SWITCHMAN_WESTING = "Westinghouse";
    public final static String SWITCHMAN_ABB = "ABB";
    public final static String SWITCHMAN_COOPER = "Cooper";
    public final static String SWITCHMAN_SIEMENS = "Siemens";
    public final static String SWITCHMAN_TRINETICS = "Trinetics";

    // all types of switches
    public final static String SWITCHTYPE_OIL = "Oil";
    public final static String SWITCHTYPE_VACUUM = "Vacuum";

    public static final String ENABLE_OPSTATE = "capEnabled";
    public static final String DISABLE_OPSTATE = "capDisabled";

    public static final String ENABLE_OVUV_OPSTATE = "capOVUVEnabled";
    public static final String DISABLE_OVUV_OPSTATE = "capOVUVDisabled";

    private com.cannontech.database.db.capcontrol.CapBank capBank = new com.cannontech.database.db.capcontrol.CapBank();
    private List<CCMonitorBankList> ccMonitorBankList = new ArrayList<CCMonitorBankList>();
    private CapBankAdditional capbankAdditionalInfo = new CapBankAdditional();

    public CapBank() {
        super(PaoType.CAPBANK);
    }
    
    public String getName() {
        return getPAOName();
    }

    public void setName(String name) {
        setPAOName(name);
    }
  
    public Integer getId() {
        return capBank.getDeviceID();
    }

    public void setId(Integer id) {
        setPAObjectID(id);
        capBank.setDeviceID(id);
    }

    @Override
    public void add() throws java.sql.SQLException {
        super.add();
        getCapBank().add();
        CCMonitorBankList.deleteMonitorPointsOnCapBankList(getCapBank().getDeviceID());
        for (Iterator<CCMonitorBankList> iter = ccMonitorBankList.iterator(); iter.hasNext();) {
            CCMonitorBankList item = iter.next();
            item.add();
        }
        capbankAdditionalInfo.add();
    }

    @Override
    public void delete() throws java.sql.SQLException {
        // remove all the associations this CapBank to any Feeder
        com.cannontech.database.db.capcontrol.CCFeederBankList.deleteCapBanksFromFeederList(null,
                                                                                            getCapBank().getDeviceID(),
                                                                                            getDbConnection());
        ZoneService zoneService = YukonSpringHook.getBean("zoneService",
                                                          ZoneService.class);
        zoneService.unassignBank(getPAObjectID());

        // Delete from all dynamic CabBank tables here
        deleteMonitorPoints();
        delete("DynamicCCOperationStatistics", "PaobjectId", getPAObjectID());
        delete("DynamicCCMonitorPointResponse", "DeviceID", getPAObjectID());
        delete("DynamicCCMonitorBankHistory", "DeviceID", getPAObjectID());
        delete("DynamicCCCapBank", "CapBankID", getPAObjectID());
        delete("CapBankAdditional", "DeviceID", getPAObjectID());
        delete("CapControlComment", "paoID", getPAObjectID());
        CCMonitorBankList.deleteMonitorPointsOnCapBankList(getPAObjectID());
        getCapBank().delete();

        super.delete();

    }

    public com.cannontech.database.db.capcontrol.CapBank getCapBank() {
        if (capBank == null) {
            capBank = new com.cannontech.database.db.capcontrol.CapBank();
        }

        return capBank;
    }

    public String getLocation() {
        return getPAODescription();
    }

    @Override
    public void retrieve() throws java.sql.SQLException {
        super.retrieve();
        getCapBank().retrieve();
        // init the cap bank points list
        ccMonitorBankList = CCMonitorBankList.getMonitorPointsOnCapBankList(getCapBank().getDeviceID());
        
        //retrieve capbank additional info
        capbankAdditionalInfo.setDeviceID(getCapBank().getDeviceID());
        capbankAdditionalInfo.retrieve();
        
        //check for custom communication medium
        capbankAdditionalInfo.setCommMediumCustom(capbankAdditionalInfo.getCommMedium());
        String commMedium = capbankAdditionalInfo.getCommMedium();
        if(commMedium != null) {
            if(commMedium.equals(CapBankAdditional.STR_NONE)) {
                capbankAdditionalInfo.setCustomCommMedium(false);
            } else {
                CapBankCommunicationMedium[] commMediums = CapBankCommunicationMedium.values();
                for (CapBankCommunicationMedium med : commMediums) {
                    if(med.getDisplayName().equals(commMedium)) {
                        capbankAdditionalInfo.setCustomCommMedium(false);
                        break;
                    }
                }
            }
        }
    }

    public void setCapBank(
            com.cannontech.database.db.capcontrol.CapBank newValue) {
        this.capBank = newValue;
    }

    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection(conn);
        getCapBank().setDbConnection(conn);

        for (Iterator<CCMonitorBankList> iter = ccMonitorBankList.iterator(); iter.hasNext();) {
            CCMonitorBankList item = iter.next();
            item.setDbConnection(conn);
        }
        capbankAdditionalInfo.setDbConnection(conn);
    }

    @Override
    public void setDeviceID(Integer deviceID) {
        super.setDeviceID(deviceID);
        getCapBank().setDeviceID(deviceID);
    }

    public void setLocation(String loc) {
        getYukonPAObject().setDescription(loc);
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
        getCapBank().update();
        CCMonitorBankList.deleteMonitorPointsOnCapBankList(getCapBank().getDeviceID());
        for (Iterator<CCMonitorBankList> iter = ccMonitorBankList.iterator(); iter.hasNext();) {
            CCMonitorBankList item = iter.next();
            if (item.getMonitorPoint() != null)
                item.add();
        }
        capbankAdditionalInfo.update();
    }

    public List<CCMonitorBankList> getCcMonitorBankList() {
        return ccMonitorBankList;
    }

    public void setCcMonitorBankList(List<CCMonitorBankList> ccMonitorBankList) {
        this.ccMonitorBankList = ccMonitorBankList;
    }

    /**
     * @throws SQLException
     */
    private void deleteMonitorPoints() throws SQLException {
        List<CCMonitorBankList> monitorPoints = CCMonitorBankList.getMonitorPointsOnCapBankList(getPAObjectID());
        if (monitorPoints != null) {
            for (Iterator<CCMonitorBankList> iter = monitorPoints.iterator(); iter.hasNext();) {
                CCMonitorBankList element = iter.next();
                delete("DynamicCCMonitorPointResponse",
                       "PointID",
                       element.getPointId());
            }
        }
    }

    public static boolean isOpstate(String tagStateName) {

        for (int i = 0; i < OPSTATES.length; i++) {
            String opstate = OPSTATES[i];
            if (opstate.equalsIgnoreCase(tagStateName)) {
                return true;
            }
        }
        return false;
    }

    public CapBankAdditional getCapbankAdditionalInfo() {
        return capbankAdditionalInfo;
    }

    public void setCapbankAdditionalInfo(CapBankAdditional capbankAdditionalInfo) {
        this.capbankAdditionalInfo = capbankAdditionalInfo;
    }

}
