package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.definition.attribute.lookup.AttributeDefinition;
import com.cannontech.common.pao.definition.attribute.lookup.MappedAttributeDefinition;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.PaoPointIdentifier;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class LoadTapChanger extends CapControlYukonPAOBase {
    private List<AttributePointMapping> pointMappings;

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
        eppad.saveAssignments(getCapControlPAOID(), pointMappings);
        pointMappings = null;
    }

    @Override
    public List<? extends DBPersistent> getChildList() {
        return null;
    }
    
    @SuppressWarnings("deprecation")
    public List<AttributePointMapping> getLtcPointMappings(){
        if(pointMappings == null){
            PaoDefinitionDao paoDefinitionDao = YukonSpringHook.getBean("paoDefinitionDao", PaoDefinitionDao.class);
            AttributeService attributeService = YukonSpringHook.getBean("attributeService", AttributeService.class);
            PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
            PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
            YukonPao ltc = paoDao.getYukonPao(getCapControlPAOID());
            pointMappings = Lists.newArrayList();
            Set<AttributeDefinition> ltcAttributes = paoDefinitionDao.getDefinedAttributes(PaoType.LOAD_TAP_CHANGER);
            
            for(AttributeDefinition attributeDefinition : ltcAttributes) {
                MappedAttributeDefinition mappedAttributeDefinition = (MappedAttributeDefinition) attributeDefinition;
                AttributePointMapping mapping = new AttributePointMapping();
                mapping.setAttribute(attributeDefinition.getAttribute());
                try {
                    PaoPointIdentifier paoPointId = attributeService.getPaoPointIdentifierForAttribute(ltc, attributeDefinition.getAttribute());
                    String paoName = paoDao.getYukonPAOName(paoPointId.getPaoIdentifier().getPaoId());
                    int pointId = pointDao.getPointIDByDeviceID_Offset_PointType(paoPointId.getPaoIdentifier().getPaoId()
                                                                                 , paoPointId.getPointIdentifier().getOffset()
                                                                                 , paoPointId.getPointIdentifier().getType());
                    String pointName = pointDao.getPointName(pointId);
                    mapping.setPaoName(paoName);
                    mapping.setPointName(pointName);
                    mapping.setPointId(pointId);
                
                } catch (EmptyResultDataAccessException e ){
                    mapping.setPaoName(CtiUtilities.STRING_NONE);
                    mapping.setPointName(CtiUtilities.STRING_NONE);
                }
                mapping.setPointType(mappedAttributeDefinition.getPointType());
                pointMappings.add(mapping);
            }
            
            Collections.sort(pointMappings);
            int index = 0;
            for(AttributePointMapping mapping : pointMappings) {
                mapping.setIndex(index);
                index++;
            }
        }
        return pointMappings;
    }

}