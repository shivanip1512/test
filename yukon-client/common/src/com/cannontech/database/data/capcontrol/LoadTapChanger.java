package com.cannontech.database.data.capcontrol;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.attribute.model.MappableAttribute;
import com.cannontech.common.pao.attribute.service.AttributeService;
import com.cannontech.common.pao.attribute.service.IllegalUseOfAttribute;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.DaoFactory;
import com.cannontech.core.dao.ExtraPaoPointAssignmentDao;
import com.cannontech.core.dao.ExtraPaoPointMapping;
import com.cannontech.core.dao.PaoDao;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.data.lite.LitePoint;
import com.cannontech.database.data.pao.PAOGroups;
import com.cannontech.database.db.DBPersistent;
import com.cannontech.spring.YukonSpringHook;
import com.google.common.collect.Lists;

public class LoadTapChanger extends CapControlYukonPAOBase {
    private List<LtcPointMapping> pointMappings;

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
        List<ExtraPaoPointMapping> eppMappings = Lists.newArrayList();
        for(LtcPointMapping mapping : pointMappings) {
            eppMappings.add(mapping.getExtraPaoPointMapping());
        }
        eppad.saveAssignments(new PaoIdentifier(getCapControlPAOID(), PaoType.LOAD_TAP_CHANGER) , eppMappings);
        pointMappings = null;
    }

    @Override
    public List<? extends DBPersistent> getChildList() {
        return null;
    }
    
    public List<LtcPointMapping> getLtcPointMappings(){
        if(pointMappings == null){
            AttributeService attributeService = YukonSpringHook.getBean("attributeService", AttributeService.class);
            PaoDao paoDao = YukonSpringHook.getBean("paoDao", PaoDao.class);
            PointDao pointDao = YukonSpringHook.getBean("pointDao", PointDao.class);
            YukonPao ltc = paoDao.getYukonPao(getCapControlPAOID());
            pointMappings = Lists.newArrayList();
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
                mapping.setPointType(mappableAttribute.getPointTypeFilter());
                pointMappings.add(mapping);
            }
            
            Collections.sort(pointMappings);
            int index = 0;
            for(LtcPointMapping mapping : pointMappings) {
                mapping.setIndex(index);
                index++;
            }
        }
        return pointMappings;
    }

}