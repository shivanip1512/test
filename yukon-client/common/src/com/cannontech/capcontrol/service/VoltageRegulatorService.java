package com.cannontech.capcontrol.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.capcontrol.RegulatorPointMapping;
import com.cannontech.common.i18n.ObjectFormattingService;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.database.data.lite.LiteYukonPAObject;
import com.cannontech.user.YukonUserContext;
import com.cannontech.yukon.IDatabaseCache;
import com.google.common.collect.ImmutableSet;

public class VoltageRegulatorService {

    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    @Autowired private IDatabaseCache dbCache;
    @Autowired private ObjectFormattingService objectFormattingService;

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

        List<RegulatorPointMapping> keys = new ArrayList<RegulatorPointMapping>(mappings.keySet());
        return sortWithKeys(mappings, keys, context);
    }

    public Map<RegulatorPointMapping, Integer> sortMappingsAllKeys(Map<RegulatorPointMapping, Integer> mappings, YukonUserContext context) {

        List<RegulatorPointMapping> keys = new ArrayList<RegulatorPointMapping>(Arrays.asList(RegulatorPointMapping.values()));
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
}
