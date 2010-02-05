package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.cannontech.capcontrol.service.LtcService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class LoadTapChanger extends CapControlYukonPAOBase {
    private List<LtcPointMapping> pointMappings;

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
        delete("ExtraPaoPointAssignment", "PaobjectId", getCapControlPAOID() );
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
        ExtraPaoPointAssignmentDao eppad = YukonSpringHook.getBean("extraPaoPointAssignmentDao", ExtraPaoPointAssignmentDao.class);
        List<ExtraPaoPointMapping> eppMappings = Lists.newArrayList();
        for(LtcPointMapping mapping : pointMappings) {
            eppMappings.add(mapping.getExtraPaoPointMapping());
        }
        eppad.saveAssignments(new PaoIdentifier(getCapControlPAOID(), PaoType.LOAD_TAP_CHANGER) , eppMappings);
        pointMappings = null;
    }

    @Override
    public List<? extends DBPersistent> getChildList() {
        return null;
    }
    
    public List<LtcPointMapping> getLtcPointMappings(){
        if(pointMappings == null){
            LtcService ltcService = YukonSpringHook.getBean("ltcService", LtcService.class);
            pointMappings = ltcService.getLtcPointMappings(getCapControlPAOID());
            Collections.sort(pointMappings);
            int index = 0;
            for(LtcPointMapping mapping : pointMappings) {
                mapping.setIndex(index);
                index++;
            }
        }
        return pointMappings;
    }

}