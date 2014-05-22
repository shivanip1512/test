package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.VoltageRegulatorDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.exception.OrphanedRegulatorException;
import com.cannontech.capcontrol.model.AbstractZone;
import com.cannontech.capcontrol.model.RegulatorToZoneMapping;
import com.cannontech.capcontrol.model.Zone;
import com.cannontech.capcontrol.service.VoltageRegulatorService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.pao.model.CompleteRegulator;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.enums.Phase;
import com.cannontech.enums.RegulatorPointMapping;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class VoltageRegulator extends CapControlYukonPAOBase implements YukonDevice{
    private List<VoltageRegulatorPointMapping> pointMappings;
    private int keepAliveTimer;
    private int keepAliveConfig;
    private double voltChangePerTap;

    /**
     * Valid PaoTypes are LOAD_TAP_CHANGER, GANG_OPERATED, PHASE_OPERATED
     * @param paoType
     */
    public VoltageRegulator(PaoType paoType) {
        super(PaoType.LOAD_TAP_CHANGER);
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
    
    private void validateBeforeUpdate(int voltageYPointId) {
        List<String> errors = Lists.newArrayList();
        if (this.keepAliveConfig < 0) {
            errors.add("Keep Alive Config must be greater than or equal to zero");
        }
        if (this.keepAliveTimer < 0) {
            errors.add("Keep Alive Timer must be greater than or equal to zero");
        }
        if (this.voltChangePerTap <= 0) {
            errors.add("Volt Change Per Tap must be greater than zero");
        }
        
        ZoneDao zoneDao = YukonSpringHook.getBean(ZoneDao.class);
        Zone zone = null;
        try {
            zone = zoneDao.getZoneByRegulatorId(this.getCapControlPAOID());
        } catch(OrphanedRegulatorException e) {}
        // If this regulator is assigned to a Zone and the user just selected "No Point"
        if(voltageYPointId == -1 && zone != null) {
            errors.add("The "
                       + RegulatorPointMapping.VOLTAGE_Y.name()
                       + " point cannot be unassigned while the regulator is attached to a zone ("
                       + zone.getName() + "). Please select a "
                       + RegulatorPointMapping.VOLTAGE_Y.name()
                       + " point or Return.");
        }
        if (!errors.isEmpty()) throw new IllegalArgumentException(StringUtils.join(errors, ", "));
    }

    @Override
    public void update() throws java.sql.SQLException {
        //Point Mappings
        List<ExtraPaoPointMapping> eppMappings = Lists.newArrayList();
        int voltageYPointId = 0;
        for (VoltageRegulatorPointMapping mapping : pointMappings) {
            eppMappings.add(mapping.getExtraPaoPointMapping());
            //check to see if a voltage Y point is still attached
            if (mapping.getRegulatorPointMapping() == RegulatorPointMapping.VOLTAGE_Y) {
                voltageYPointId = mapping.getPointId();
            }
        }
        
        validateBeforeUpdate(voltageYPointId);
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
        extraPaoPointAssignmentDao.saveAssignments(new PaoIdentifier(getPAObjectID(), getPaoType()) , eppMappings);
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

    public double getVoltChangePerTap() {
        VoltageRegulatorDao voltageRegulatorDao = YukonSpringHook.getBean("voltageRegulatorDao", VoltageRegulatorDao.class);
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