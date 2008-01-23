package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.SeasonScheduleDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;

public class CapControlSubstation extends CapControlYukonPAOBase implements EditorPanel {

    private com.cannontech.database.db.capcontrol.CapControlSubstation CapControlSubstation;
    private ArrayList<CCSubstationSubBusList> substationSubBuses;

    public CapControlSubstation() {
        super();
        setPAOCategory(PAOGroups.STRING_CAT_CAPCONTROL);
        setPAOClass(PAOGroups.STRING_CAT_CAPCONTROL);
        getYukonPAObject().setType(PAOGroups.STRING_CAPCONTROL_SUBSTATION);
    }

    public void retrieve() throws SQLException {
        super.retrieve();
        getCapControlSubstation().retrieve();
        List<CCSubstationSubBusList> allSubstationSubBuses = CCSubstationSubBusList.getCCSubBusesOnSubstation(getCapControlPAOID());
        for (CCSubstationSubBusList assignment : allSubstationSubBuses) {
        	 substationSubBuses.add(assignment);
        }
    }

    public void delete() throws SQLException {
        // remove all the associations of Subs to this Area
        com.cannontech.database.db.capcontrol.CCSubstationSubBusList.deleteCCSubBusFromSubstationList(getSubstationID(), null, getDbConnection());
        SeasonScheduleDao ssDao = DaoFactory.getSeasonSchedule();
        ssDao.deleteStrategyAssigment(getSubstationID());
        // Delete from all dynamic objects
        delete("DynamicCCSubstation", "substationID", getSubstationID());
        
        getCapControlSubstation().delete();
        super.delete();
    }

    private Integer getSubstationID() {
        return getPAObjectID();
    }

    public void add() throws SQLException {
        if (getPAObjectID() == null) {
            PaoDao paoDao = DaoFactory.getPaoDao();
            setCapControlPAOID(paoDao.getNextPaoId());
        }
        super.add();
        getCapControlSubstation().add();
        for (int i = 0; i < getChildList().size(); i++) {
            getChildList().get(i).add();
        }
        
    }

    public com.cannontech.database.db.capcontrol.CapControlSubstation getCapControlSubstation() {
        if (CapControlSubstation == null) {
            CapControlSubstation = new com.cannontech.database.db.capcontrol.CapControlSubstation();
        }
        return CapControlSubstation;
    }

    public ArrayList<CCSubstationSubBusList> getChildList() {
        if (substationSubBuses == null) {
        	substationSubBuses = new ArrayList<CCSubstationSubBusList>();
        }
        return substationSubBuses;
    }

    public void setCapControlPAOID(Integer paoID) {
        super.setPAObjectID(paoID);
        getCapControlSubstation().setSubstationID(paoID);
        for (int i = 0; i < getChildList().size(); i++) {
            getChildList().get(i).setSubstationID(paoID);
        }
    }

    public void setDbConnection(Connection conn) {
        super.setDbConnection(conn);
        getCapControlSubstation().setDbConnection(conn);
        for (int i = 0; i < getChildList().size(); i++) {
            getChildList().get(i).setDbConnection(conn);
        }
    }

    public void setSubstationSubBuses(ArrayList<CCSubstationSubBusList> substationSubBuses) {
        this.substationSubBuses = substationSubBuses;
    }

    public void setCapControlSubstation( com.cannontech.database.db.capcontrol.CapControlSubstation CapControlSubstation) {
        this.CapControlSubstation = CapControlSubstation;
    }

    public void update() throws java.sql.SQLException {
        super.update();
        getCapControlSubstation().update();
        CCSubstationSubBusList.deleteCCSubBusFromSubstationList(getCapControlPAOID(), null, getDbConnection());
        Connection connection = PoolManager.getInstance().getConnection(CtiUtilities.getDatabaseAlias());
        for (int i = 0; i < getChildList().size(); i++) {
        	CCSubstationSubBusList substationSubBusList = getChildList().get(i);
        	substationSubBusList.setDbConnection(connection );
        	substationSubBusList.add();
        }
        connection.close();
    }

}
