package com.cannontech.capcontrol.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.BuiltInAttribute;
import com.cannontech.common.pao.attribute.model.MappableAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.capcontrol.VoltageRegulatorPointMapping;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.collect.Lists;

public class VoltageRegulatorService {
    
    private AttributeService attributeService;
    private PaoDao paoDao;
    private PointDao pointDao;
    
    public List<VoltageRegulatorPointMapping> getPointMappings(int regulatorId) {
        List<VoltageRegulatorPointMapping> pointMappings = Lists.newArrayList();
        YukonPao regulator = paoDao.getYukonPao(regulatorId);
        PaoType paoType = regulator.getPaoIdentifier().getPaoType();
        Set<MappableAttribute> regulatorAttributes = attributeService.getMappableAttributes(paoType);
        
        for(MappableAttribute mappableAttribute : regulatorAttributes) {
            VoltageRegulatorPointMapping mapping = generatePointMapping(regulator, mappableAttribute);
            pointMappings.add(mapping);
        }
        
        return pointMappings;
    }
    
    private VoltageRegulatorPointMapping generatePointMapping(YukonPao regulator, MappableAttribute mappableAttribute) {
        VoltageRegulatorPointMapping mapping = new VoltageRegulatorPointMapping();
        mapping.setExtraPaoPointMapping(new ExtraPaoPointMapping());
        mapping.setAttribute(mappableAttribute.getAttribute());
        try {
            LitePoint litePoint = attributeService.getPointForAttribute(regulator, mappableAttribute.getAttribute());
            String paoName = paoDao.getYukonPAOName(pointDao.getPaoPointIdentifier(litePoint.getPointID()).getPaoIdentifier().getPaoId());
            mapping.setPaoName(paoName);
            mapping.setPointName(litePoint.getPointName());
            mapping.setPointId(litePoint.getPointID());
        
        } catch (IllegalUseOfAttribute e ){
            /* No point defined for this attribute */
            mapping.setPaoName(CtiUtilities.STRING_NONE);
            mapping.setPointName(CtiUtilities.STRING_NONE);
        }
        mapping.setFilterType(mappableAttribute.getFilterType());
        
        return mapping;
    }
    
    @Autowired
    public void setAttributeService(AttributeService attributeService) {
        this.attributeService = attributeService;
    }
    
    @Autowired
    public void setPaoDao(PaoDao paoDao) {
        this.paoDao = paoDao;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
}