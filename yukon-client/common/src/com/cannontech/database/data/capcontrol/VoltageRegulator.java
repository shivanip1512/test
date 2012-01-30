package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import com.cannontech.capcontrol.dao.VoltageRegulatorDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.OrphanedRegulatorException;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.common.pao.PaoCategory;
import com.cannontech.common.pao.PaoClass;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.pojo.CompleteRegulator;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class VoltageRegulator extends CapControlYukonPAOBase implements YukonDevice{
    private List<VoltageRegulatorPointMapping> pointMappings;
    private int keepAliveTimer;
    private int keepAliveConfig;

    public VoltageRegulator() {
        super();
        setPAOCategory( PaoCategory.CAPCONTROL.getDbString() );
        setPAOClass( PaoClass.CAPCONTROL.getDbString() );
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
        
        PaoPersistenceService paoPersistenceService = YukonSpringHook.getBean(PaoPersistenceService.class);
        
        CompleteRegulator regulator = paoPersistenceService.retreivePao(getPaoIdentifier(), CompleteRegulator.class);
        regulator.setDisabled(CtiUtilities.trueChar.equals(getDisableFlag()));
        regulator.setKeepAliveConfig(keepAliveConfig);
        regulator.setKeepAliveTimer(keepAliveTimer);
        
        paoPersistenceService.updatePao(regulator);
        
        //Point Mappings
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

    public final static String usedVoltageRegulator(Integer regID) {
        ZoneDao zoneDao = YukonSpringHook.getBean("zoneDao", ZoneDao.class);
        try {
            return zoneDao.getZoneByRegulatorId(regID).getName();
        } catch (OrphanedRegulatorException e) {
            return null;
        }
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

    public int getKeepAliveTimer() {
        VoltageRegulatorDao voltageRegulatorDao = YukonSpringHook.getBean("voltageRegulatorDao", VoltageRegulatorDao.class);
        Integer paoId = getCapControlPAOID();
        keepAliveTimer = voltageRegulatorDao.getKeepAliveTimerForRegulator(paoId);
        return keepAliveTimer;
    }

    public int getKeepAliveConfig() {
        VoltageRegulatorDao voltageRegulatorDao = YukonSpringHook.getBean("voltageRegulatorDao", VoltageRegulatorDao.class);
        Integer paoId = getCapControlPAOID();
        keepAliveConfig = voltageRegulatorDao.getKeepAliveConfigForRegulator(paoId);
        return keepAliveConfig;
    }

    public void setKeepAliveTimer(int keepAliveTimer) {
        this.keepAliveTimer = keepAliveTimer;
    }

    public void setKeepAliveConfig(int keepAliveConfig) {
        this.keepAliveConfig = keepAliveConfig;
    }

    public boolean isDisplayTimer() {
        PaoType paoType = PaoType.getForDbString(getPAOType());
        return paoType == PaoType.LOAD_TAP_CHANGER;
    }
    
    public boolean isDisplayConfig() {
        PaoType paoType = PaoType.getForDbString(getPAOType());
        return paoType == PaoType.LOAD_TAP_CHANGER;
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(getPAObjectID(),PaoType.getForDbString(getPAOType()));
    }

}