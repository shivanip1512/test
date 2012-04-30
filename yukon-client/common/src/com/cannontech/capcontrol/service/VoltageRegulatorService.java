package com.cannontech.capcontrol.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.search.FilterType;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.core.dao.NotFoundException;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.capcontrol.VoltageRegulatorPointMapping;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.enums.RegulatorPointMapping;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

public class VoltageRegulatorService {
    
    @Autowired private PaoDao paoDao;
    @Autowired private PointDao pointDao;
    @Autowired private ExtraPaoPointAssignmentDao extraPaoPointAssignmentDao;
    
    public List<VoltageRegulatorPointMapping> getPointMappings(int regulatorId) {
        List<VoltageRegulatorPointMapping> pointMappings = Lists.newArrayList();
        YukonPao regulator = paoDao.getYukonPao(regulatorId);
        ImmutableSet<RegulatorPointMapping> regulatorMappings = RegulatorPointMapping.getPointMappingsForPaoType(regulator.getPaoIdentifier().getPaoType());
        
        for(RegulatorPointMapping regulatorMapping : regulatorMappings) {
            VoltageRegulatorPointMapping mapping = generatePointMapping(regulator, regulatorMapping);
            pointMappings.add(mapping);
        }
        
        return pointMappings;
    }
    
    private VoltageRegulatorPointMapping generatePointMapping(YukonPao regulator, RegulatorPointMapping regulatorMapping) {
        VoltageRegulatorPointMapping mapping = new VoltageRegulatorPointMapping();
        mapping.setExtraPaoPointMapping(new ExtraPaoPointMapping());
        mapping.setRegulatorPointMapping(regulatorMapping);
        try {
            LitePoint litePoint = extraPaoPointAssignmentDao.getLitePoint(regulator, regulatorMapping);
            String paoName = paoDao.getYukonPAOName(pointDao.getPaoPointIdentifier(litePoint.getPointID()).getPaoIdentifier().getPaoId());
            mapping.setPaoName(paoName);
            mapping.setPointName(litePoint.getPointName());
            mapping.setPointId(litePoint.getPointID());
        
        } catch (NotFoundException e ){
            /* No point defined for this RegulatorPointMapping */
            mapping.setPaoName(CtiUtilities.STRING_NONE);
            mapping.setPointName(CtiUtilities.STRING_NONE);
        }
        mapping.setFilterType(FilterType.getForPointType(regulatorMapping.getPointType()));
        
        return mapping;
    }
    
}