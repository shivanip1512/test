package com.cannontech.database.data.capcontrol;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.cannontech.common.editor.EditorPanel;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.PoolManager;
import com.cannontech.database.db.capcontrol.CCSubAreaAssignment;
import com.cannontech.database.db.capcontrol.CCSubSpecialAreaAssignment;
import com.cannontech.database.db.capcontrol.CCSubstationSubBusList;
import com.cannontech.spring.YukonSpringHook;

public class CapControlSubstation extends CapControlYukonPAOBase implements EditorPanel {
    private com.cannontech.database.db.capcontrol.CapControlSubstation CapControlSubstation;
    private List<CCSubstationSubBusList> substationSubBuses;

    @SuppressWarnings("static-access")
    public CapControlSubstation() {
        super(PaoType.CAP_CONTROL_SUBSTATION);
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
        getCapControlSubstation().retrieve();
        List<CCSubstationSubBusList> allSubstationSubBuses = CCSubstationSubBusList.getCCSubBusesOnSubstation(getCapControlPAOID());
        for (CCSubstationSubBusList assignment : allSubstationSubBuses) {
        	 substationSubBuses.add(assignment);
        }
    }

    @Override
    public void delete() throws SQLException {
        CCSubAreaAssignment.deleteSubstation(getSubstationID());
        CCSubSpecialAreaAssignment.deleteSubstation(getSubstationID());
        CCSubstationSubBusList.deleteCCSubBusFromSubstationList(getSubstationID(), null, getDbConnection());
        // Delete from all dynamic objects
        delete("DynamicCCSubstation", "substationID", getSubstationID());
        delete("CapControlComment", "paoID", getSubstationID());
        getCapControlSubstation().delete();
        super.delete();
    }

    private Integer getSubstationID() {
        return getPAObjectID();
    }

    @Override
    public void add() throws SQLException {
        if (getPAObjectID() == null) {
            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);
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

    @Override
    public List<CCSubstationSubBusList> getChildList() {
        if (substationSubBuses == null) {
        	substationSubBuses = new ArrayList<CCSubstationSubBusList>();
        }
        return substationSubBuses;
    }

    @Override
    public void setCapControlPAOID(Integer paoID) {
        super.setPAObjectID(paoID);
        getCapControlSubstation().setSubstationID(paoID);
        for (int i = 0; i < getChildList().size(); i++) {
            getChildList().get(i).setSubstationID(paoID);
        }
    }

    @Override
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

    @Override
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
