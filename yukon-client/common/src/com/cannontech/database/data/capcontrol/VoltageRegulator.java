package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.VoltageRegulatorDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.OrphanedRegulatorException;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.common.model.Phase;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.model.CompleteRegulator;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;

public class VoltageRegulator extends CapControlYukonPAOBase implements YukonDevice{
    private Map<RegulatorPointMapping, Integer> pointMappings;
    private int keepAliveTimer;
    private int keepAliveConfig;
    private double voltChangePerTap;

    /**
     * Valid PaoTypes are LOAD_TAP_CHANGER, GANG_OPERATED, PHASE_OPERATED
     * @param paoType
     */
    public VoltageRegulator(PaoType paoType) {
        super(paoType);

        if (!paoType.isRegulator()) {
            throw new IllegalArgumentException("Invalid PaoType for regulator: " + paoType);
        }
    }

    @Override
    public void add() throws SQLException {
        if (getPAObjectID() == null) {
            PaoDao paoDao = YukonSpringHook.getBean(PaoDao.class);   
            setCapControlPAOID(paoDao.getNextPaoId());   
        }

        super.add();
    }

    @Override
    public void setCapControlPAOID(Integer paoId) {
        super.setPAObjectID(paoId);
    }

    @Override
    public void update() throws java.sql.SQLException {
        //Point Mappings
        Map<RegulatorPointMapping, Integer> eppMappings = new HashMap<>();
        int voltageYPointId = 0;
        for (Entry<RegulatorPointMapping, Integer> mapping : pointMappings.entrySet()) {
            if (mapping.getValue() != null) {
                eppMappings.put(mapping.getKey(), mapping.getValue());
                //check to see if a voltage Y point is still attached
                if (mapping.getKey() == RegulatorPointMapping.VOLTAGE_Y) {
                    voltageYPointId = mapping.getValue();
                }
            }
        }

        super.update();
        
        PaoPersistenceService paoPersistenceService = YukonSpringHook.getBean(PaoPersistenceService.class);
        
        CompleteRegulator regulator = paoPersistenceService.retreivePao(getPaoIdentifier(), CompleteRegulator.class);
        regulator.setDisabled(CtiUtilities.trueChar.equals(getDisableFlag()));
        regulator.setKeepAliveConfig(keepAliveConfig);
        regulator.setKeepAliveTimer(keepAliveTimer);
        regulator.setVoltChangePerTap(voltChangePerTap);
        
        paoPersistenceService.updatePao(regulator);

        AbstractZone abstractZone = null;
        try {
            ZoneDao zoneDao = YukonSpringHook.getBean(ZoneDao.class);
            Zone zone = zoneDao.getZoneByRegulatorId(this.getCapControlPAOID());
            abstractZone = AbstractZone.create(zone);
        } catch(OrphanedRegulatorException e) {
            // this regulator is not assigned to a zone - which is fine - move along
        }
        
        //remove voltage Y CcMonitorBankList entry, if one exists and is different from the new one
        boolean isNewVoltageYPoint = false;
        CcMonitorBankListDao ccMonitorBankListDao = YukonSpringHook.getBean(CcMonitorBankListDao.class);
        if(voltageYPointId > 0) {
            isNewVoltageYPoint = ccMonitorBankListDao.deleteNonMatchingRegulatorPoint(getPAObjectID(), voltageYPointId);
        } else {
            ccMonitorBankListDao.removeByDeviceId(getPAObjectID(), null);
        }
        
        ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao = YukonSpringHook.getBean(ExtraPaoPointAssignmentDao.class);
        extraPaoPointAssignmentDao.saveAssignments(getPaoIdentifier() , eppMappings);
        pointMappings = null;
        
        //add voltage_y CcMonitorBankList entry, if a point is assigned
        if(isNewVoltageYPoint && abstractZone != null) {
            Phase phase = null;
            for (RegulatorToZoneMapping regToZone : abstractZone.getRegulatorsList()) {
                if (regToZone.getRegulatorId() == getPAObjectID()) {
                    phase = regToZone.getPhase();
                    break;
                }
            }
            // it's ok if phase is null here - that just means this is a gang regulator with no phase
            ccMonitorBankListDao.addRegulatorPoint(getPAObjectID(), phase, abstractZone.getSubstationBusId());
        }
    }

    @Override
    public List<? extends DBPersistent> getChildList() {
        return null;
    }

    public final static String usedVoltageRegulator(Integer regID) {
        ZoneDao zoneDao = YukonSpringHook.getBean(ZoneDao.class);
        try {
            return zoneDao.getZoneByRegulatorId(regID).getName();
        } catch (OrphanedRegulatorException e) {
            return null;
        }
    }

    public Map<RegulatorPointMapping, Integer> getPointMappings(){
        if(pointMappings == null){
            VoltageRegulatorService voltageRegulatorService = YukonSpringHook.getBean(VoltageRegulatorService.class);
            pointMappings = voltageRegulatorService.getPointIdByAttributeForRegulator(getCapControlPAOID());
        }
        return pointMappings;
    }

    public void setPointMappings(Map<RegulatorPointMapping, Integer> pointMappings) {
        this.pointMappings = pointMappings;
    }

    public int getKeepAliveTimer() {
        VoltageRegulatorDao voltageRegulatorDao = YukonSpringHook.getBean(VoltageRegulatorDao.class);
        Integer paoId = getCapControlPAOID();
        keepAliveTimer = voltageRegulatorDao.getKeepAliveTimerForRegulator(paoId);
        return keepAliveTimer;
    }

    public int getKeepAliveConfig() {
        VoltageRegulatorDao voltageRegulatorDao = YukonSpringHook.getBean(VoltageRegulatorDao.class);
        Integer paoId = getCapControlPAOID();
        keepAliveConfig = voltageRegulatorDao.getKeepAliveConfigForRegulator(paoId);
        return keepAliveConfig;
    }

    public double getVoltChangePerTap() {
        VoltageRegulatorDao voltageRegulatorDao = YukonSpringHook.getBean(VoltageRegulatorDao.class);
        Integer paoId = getCapControlPAOID();
        voltChangePerTap = voltageRegulatorDao.getVoltChangePerTapForRegulator(paoId);
        return voltChangePerTap;
    }

    public void setVoltChangePerTap(double voltChangePerTap) {
        this.voltChangePerTap = voltChangePerTap;
    }

    public void setKeepAliveTimer(int keepAliveTimer) {
        this.keepAliveTimer = keepAliveTimer;
    }

    public void setKeepAliveConfig(int keepAliveConfig) {
        this.keepAliveConfig = keepAliveConfig;
    }

    public boolean isDisplayTimer() {
        return getPaoType() == PaoType.LOAD_TAP_CHANGER;
    }
    
    public boolean isDisplayConfig() {
        return getPaoType() == PaoType.LOAD_TAP_CHANGER;
    }
    
    @Override
    public PaoIdentifier getPaoIdentifier() {
        return new PaoIdentifier(getPAObjectID(), getPaoType());
    }

}