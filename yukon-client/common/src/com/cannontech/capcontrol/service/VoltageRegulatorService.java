package com.cannontech.capcontrol.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.capcontrol.dao.CcMonitorBankListDao;
import com.cannontech.capcontrol.dao.ZoneDao;
import com.cannontech.capcontrol.model.Regulator;
import com.cannontech.common.device.config.dao.DeviceConfigurationDao;
import com.cannontech.common.device.config.dao.InvalidDeviceTypeException;
import com.cannontech.common.device.config.model.LightDeviceConfiguration;
import com.cannontech.common.device.config.service.DeviceConfigurationService;
import com.cannontech.common.device.model.SimpleDevice;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.model.CompleteRegulator;
import com.cannontech.common.pao.service.PaoPersistenceService;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableSet;

public class VoltageRegulatorService {

    @Autowired private CcMonitorBankListDao ccMonitorBankListDao;
    @Autowired private DeviceConfigurationDao deviceConfigurationDao;
    @Autowired private DeviceConfigurationService deviceConfigurationService;
    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private ObjectFormattingService objectFormattingService;
    @Autowired private PaoPersistenceService paoPersistenceService;
    @Autowired private ZoneDao zoneDao;


    public Map<RegulatorPointMapping, Integer> getPointIdByAttributeForRegulator(int regulatorId) {

        LiteYukonPAObject regulator = dbCache.getAllPaosMap().get(regulatorId);

        Map<RegulatorPointMapping, Integer> attributeToPointId = new HashMap<>();
        ImmutableSet<RegulatorPointMapping> regulatorMappings = RegulatorPointMapping.getPointMappingsForPaoType(regulator.getPaoIdentifier().getPaoType());

        for (RegulatorPointMapping regulatorMapping : regulatorMappings) {
            try {
                int pointId = extraPaoPointAssignmentDao.getPointId(regulator, regulatorMapping);
                attributeToPointId.put(regulatorMapping, pointId);
            } catch (NotFoundException e ){
                /* No point defined for this RegulatorPointMapping */
                attributeToPointId.put(regulatorMapping, null);
            }
        }

        return attributeToPointId ;
    }

    public Map<RegulatorPointMapping, Integer> sortMappings(Map<RegulatorPointMapping, Integer> mappings, YukonUserContext context) {

        List<RegulatorPointMapping> keys = new ArrayList<>(mappings.keySet());
        return sortWithKeys(mappings, keys, context);
    }

    public Map<RegulatorPointMapping, Integer> sortMappingsAllKeys(Map<RegulatorPointMapping, Integer> mappings, YukonUserContext context) {

        List<RegulatorPointMapping> keys = new ArrayList<>(Arrays.asList(RegulatorPointMapping.values()));
        return sortWithKeys(mappings, keys, context);
    }

    private Map<RegulatorPointMapping, Integer> sortWithKeys(Map<RegulatorPointMapping, Integer> mappings, List<RegulatorPointMapping> keys, YukonUserContext context) {

        keys = objectFormattingService.sortDisplayableValues(keys, null, null, context);

        Map<RegulatorPointMapping, Integer> sortedMappings = new LinkedHashMap<>();
        for (RegulatorPointMapping key : keys) {
            sortedMappings.put(key, mappings.get(key));
        }

        return sortedMappings;
    }

    @Transactional
    public int save(Regulator regulator) throws InvalidDeviceTypeException {

        //Base object

        CompleteRegulator completeRegulator = regulator.asCompletePao();

        if (regulator.getId() == null) {
            paoPersistenceService.createPaoWithDefaultPoints(completeRegulator, regulator.getType());
        } else {
            paoPersistenceService.updatePao(completeRegulator);
        }

        //Device config assignment

        SimpleDevice device = new SimpleDevice(completeRegulator);
        LightDeviceConfiguration config = new LightDeviceConfiguration(regulator.getConfigId(), null, null);

        deviceConfigurationService.assignConfigToDevice(config, device, YukonUserContext.system.getYukonUser(),
            regulator.getName());

        //Point Mappings

        Map<RegulatorPointMapping, Integer> eppMappings = new HashMap<>();
        int voltagePointId = 0;
        for (Entry<RegulatorPointMapping, Integer> mapping : regulator.getMappings().entrySet()) {
            if (mapping.getValue() != null) {
                eppMappings.put(mapping.getKey(), mapping.getValue());
                //check to see if a voltage point is still attached
                if (mapping.getKey() == RegulatorPointMapping.VOLTAGE) {
                    voltagePointId = mapping.getValue();
                }
            }
        }

        extraPaoPointAssignmentDao.saveAssignments(completeRegulator.getPaoIdentifier(), eppMappings);
        
        //Regulator voltage point for CcMonitorBankList

        if (voltagePointId > 0) {
            ccMonitorBankListDao.updateRegulatorVoltagePoint(completeRegulator.getPaObjectId(), voltagePointId);
        } else {
            ccMonitorBankListDao.removeByDeviceId(completeRegulator.getPaObjectId(), null);
        }

        return completeRegulator.getPaObjectId();
    }

    public Regulator getRegulatorById(int id) {

        PaoType type = dbCache.getAllPaosMap().get(id).getPaoType();

        CompleteRegulator completeRegulator = new CompleteRegulator();

        completeRegulator.setPaoIdentifier(PaoIdentifier.of(id, type));
        completeRegulator = paoPersistenceService.retreivePao(completeRegulator, CompleteRegulator.class);

        Regulator regulator = Regulator.fromCompletePao(completeRegulator);

        Map<RegulatorPointMapping, Integer> pointMappings = getPointIdByAttributeForRegulator(id);
        regulator.setMappings(pointMappings);

        LightDeviceConfiguration config;
        try {
            SimpleDevice device = SimpleDevice.of(completeRegulator.getPaoIdentifier());
            config = deviceConfigurationDao.getConfigurationForDevice(device);
        } catch (NotFoundException e) {
            config = deviceConfigurationDao.getDefaultRegulatorConfiguration();
        }
        regulator.setConfigId(config.getConfigurationId());

        return regulator;
    }

    public void delete(int id) {

        LiteYukonPAObject regulator = dbCache.getAllPaosMap().get(id);

        CompleteRegulator completeRegulator = new CompleteRegulator();
        completeRegulator.setPaoIdentifier(regulator.getPaoIdentifier());

        paoPersistenceService.deletePao(completeRegulator);
    }
}
