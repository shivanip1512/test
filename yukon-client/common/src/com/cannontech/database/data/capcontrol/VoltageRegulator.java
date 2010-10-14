package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.data.pao.DeviceClasses;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class VoltageRegulator extends CapControlYukonPAOBase {
    private List<VoltageRegulatorPointMapping> pointMappings;

    public VoltageRegulator() {
        super();
        setPAOCategory( PAOGroups.STRING_CAT_CAPCONTROL );
        setPAOClass( DeviceClasses.STRING_CLASS_VOLTAGEREGULATOR );
    }

    public VoltageRegulator(Integer regulatorId) {
        this();
        setCapControlPAOID( regulatorId );
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
        for(VoltageRegulatorPointMapping mapping : pointMappings) {
            eppMappings.add(mapping.getExtraPaoPointMapping());
        }
        
        PaoType paoType = PaoType.getForDbString(getPAOType());
        
        eppad.saveAssignments(new PaoIdentifier(getCapControlPAOID(), paoType) , eppMappings);
        pointMappings = null;
    }

    @Override
    public List<? extends DBPersistent> getChildList() {
        return null;
    }
    
    public List<VoltageRegulatorPointMapping> getPointMappings(){
        if(pointMappings == null){
            VoltageRegulatorService voltageRegulatorService = YukonSpringHook.getBean("voltageRegulatorService", VoltageRegulatorService.class);
            pointMappings = voltageRegulatorService.getPointMappings(getCapControlPAOID());
            Collections.sort(pointMappings);
            int index = 0;
            for(VoltageRegulatorPointMapping mapping : pointMappings) {
                mapping.setIndex(index);
                index++;
            }
        }
        return pointMappings;
    }

}