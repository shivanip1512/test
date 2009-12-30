package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.List;

import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;

public class LoadTapChanger extends CapControlYukonPAOBase {

    public LoadTapChanger() {
        super();
        setPAOCategory( PAOGroups.STRING_CAT_CAPCONTROL );
        setPAOClass( PAOGroups.STRING_CAT_CAPCONTROL );
    }

    public LoadTapChanger(Integer ltcId) {
        this();
        setCapControlPAOID( ltcId );
    }
        
    @Override
    public void add() throws SQLException {
        if (getPAObjectID() == null) {
            PaoDao paoDao = DaoFactory.getPaoDao();   
            setCapControlPAOID(paoDao.getNextPaoId());   
        }

        super.add();
    }

    @Override
    public void delete() throws SQLException {
        delete("CCSubstationBusToLTC", "LtcId", getCapControlPAOID() );
        super.delete();
    }

    @Override
    public void retrieve() throws SQLException {
        super.retrieve();
    }

    @Override
    public void setCapControlPAOID(Integer feedID) {
        super.setPAObjectID( feedID );
    }
    
    @Override
    public void setDbConnection(java.sql.Connection conn) {
        super.setDbConnection( conn );
    }

    @Override
    public void update() throws java.sql.SQLException {
        super.update();
    }

    @Override
    public List<? extends DBPersistent> getChildList() {
        return null;
    }

}