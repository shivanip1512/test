package com.cannontech.capcontrol.service;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.MappableAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.capcontrol.LtcPointMapping;
import com.cannontech.database.data.lite.LitePoint;
import com.google.common.collect.Lists;

public class LtcService {
    
    private AttributeService attributeService;
    private PaoDao paoDao;
    private PointDao pointDao;

    public List<LtcPointMapping> getLtcPointMappings(int ltcId) {
        List<LtcPointMapping> pointMappings = Lists.newArrayList();
        YukonPao ltc = paoDao.getYukonPao(ltcId);
        Set<MappableAttribute> ltcAttributes = attributeService.getMappableAttributes(PaoType.LOAD_TAP_CHANGER);
        
        for(MappableAttribute mappableAttribute : ltcAttributes) {
            LtcPointMapping mapping = new LtcPointMapping();
            mapping.setExtraPaoPointMapping(new ExtraPaoPointMapping());
            mapping.setAttribute(mappableAttribute.getAttribute());
            try {
                LitePoint litePoint = attributeService.getPointForAttribute(ltc, mappableAttribute.getAttribute());
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
            pointMappings.add(mapping);
        }
        return pointMappings;
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