package com.cannontech.common.pao.definition.service;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.service.DeviceGroupService;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.pao.YukonPao;
import com.cannontech.common.pao.definition.dao.PaoDefinitionDao;
import com.cannontech.common.pao.definition.model.CalcPointComponent;
import com.cannontech.common.pao.definition.model.PaoDefinition;
import com.cannontech.common.pao.definition.model.PointIdentifier;
import com.cannontech.common.pao.definition.model.PointTemplate;
import com.cannontech.common.pao.service.PointCreationService;
import com.cannontech.common.util.MapUtil;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.PointDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.point.PointBase;
import com.google.common.base.Predicate;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PaoDefinitionServiceImpl implements PaoDefinitionService {
    
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private DeviceGroupService deviceGroupService;
    
    private PaoDefinitionDao paoDefinitionDao = null;
    private PointCreationService pointCreationService;
    private PointDao pointDao = null;

    @Autowired
    public void setPaoDefinitionDao(PaoDefinitionDao paoDefinitionDao) {
        this.paoDefinitionDao = paoDefinitionDao;
    }
    
    @Autowired
    public void setPointCreationService(PointCreationService pointCreationService) {
        this.pointCreationService = pointCreationService;
    }
    
    @Autowired
    public void setPointDao(PointDao pointDao) {
        this.pointDao = pointDao;
    }
    
    @Override
    public List<PointBase> createDefaultPointsForPao(YukonPao pao) {
        
        Set<PointTemplate> initPointTemplates = paoDefinitionDao.getInitPointTemplates(pao.getPaoIdentifier().getPaoType());
        List<PointBase> pointList = createPointsForPaoHelper(pao, initPointTemplates);
        return pointList;
    }
    
    @Override
    public List<PointBase> createAllPointsForPao(YukonPao pao) {
        
        Set<PointTemplate> allPointTemplates = paoDefinitionDao.getAllPointTemplates(pao.getPaoIdentifier().getPaoType());
        List<PointBase> pointList = createPointsForPaoHelper(pao, allPointTemplates);
        return pointList;
    }
    
    private List<PointBase> createPointsForPaoHelper(YukonPao pao, Set<PointTemplate> pointTemplates) {
        List<PointBase> pointList = Lists.newArrayList();
        Map<PointIdentifier, Integer> pointIdLookupMap = Maps.newHashMap();
        
        // Non-calculated points
        for (PointTemplate template : pointTemplates) {
            if (template.getCalcPointInfo() == null) {
                PointBase pointBase = pointCreationService.createPoint(pao.getPaoIdentifier(), template);
                pointList.add(pointBase);
                if (pointBase.getPoint() != null) {
                    pointIdLookupMap.put(template.getPointIdentifier(), pointBase.getPoint().getPointID());
                }
            }
        }
        
        // Populate CalcPointComponent.pointId values
        for (PointTemplate template : pointTemplates) {
            if (template.getCalcPointInfo() != null) {
                for (CalcPointComponent pointComponent : template.getCalcPointInfo().getComponents()) {
                    if (pointComponent.getPointIdentifier() != null) {
                        Integer pointId = pointIdLookupMap.get(pointComponent.getPointIdentifier());
                        pointComponent.setPointId(pointId);
                    }
                }
            }
        }
        
        // Calculated points (requires above points to first be created)
        for (PointTemplate template : pointTemplates) {
            if (template.getCalcPointInfo() != null) {
                pointList.add(pointCreationService.createPoint(pao.getPaoIdentifier(), template));
            }
        }
        
        return pointList;
    }
    
    @Override
    public ListMultimap<String, PaoDefinition> getPaoDisplayGroupMap() {
        return paoDefinitionDao.getPaoDisplayGroupMap();
    }
    
    private ListMultimap<String, PaoDefinition> getPaoDisplayGroupMap(Predicate<PaoDefinition> predicate) {
        ListMultimap<String, PaoDefinition> paoDisplayGroupMap = paoDefinitionDao.getPaoDisplayGroupMap();
        return MapUtil.filterLinkedListMultimap(paoDisplayGroupMap, predicate); 
    }
    
    @Override
    public ListMultimap<String, PaoDefinition> getCreatablePaoDisplayGroupMap() {
        return getPaoDisplayGroupMap(new Predicate<PaoDefinition>() {
            @Override
            public boolean apply(PaoDefinition input) {
                return input.isCreatable();
            }
        });
    }
    
    @Override
    public boolean isPaoTypeChangeable(YukonPao pao) {
        return isPaoTypeChangeable(pao.getPaoIdentifier().getPaoType());
    }
    
    @Override
    public boolean isPaoTypeChangeable(PaoType paoType) {
        PaoDefinition paoDefinition = paoDefinitionDao.getPaoDefinition(paoType);
        return paoDefinition.isChangeable();
    }
    
    @Override
    public Set<PaoDefinition> getChangeablePaos(YukonPao pao) {
        return getChangeablePaos(pao.getPaoIdentifier().getPaoType());
    }
    
    @Override
    public Set<PaoDefinition> getChangeablePaos(PaoType paoType) {
        
        // Make sure this paoType can be changed
        if (!this.isPaoTypeChangeable(paoType)) {
            return Collections.emptySet();
        }
        
        PaoDefinition paoDefinition = paoDefinitionDao.getPaoDefinition(paoType);
        
        // Get all of the paoDefinitions in the paoType's change group
        Set<PaoDefinition> paos = paoDefinitionDao.getPaosThatPaoCanChangeTo(paoDefinition);
        return paos;
    }

    @Override
    public List<PaoType> findListOfPaoTypesInGroup(DeviceGroup group, Collection<PaoType> possiblePaoTypes) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISTINCT(type) FROM YukonPAObject ypo");
        sql.append("WHERE type").in(possiblePaoTypes);
        SqlFragmentSource groupSqlWhereClause =
            deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singleton(group), "YPO.paObjectId");
        sql.append("AND").appendFragment(groupSqlWhereClause);
        
        List<PaoType> paoTypes = yukonJdbcTemplate.query(sql, new YukonRowMapper<PaoType>() {
            @Override
            public PaoType mapRow(YukonResultSet rs) throws SQLException {
                return rs.getEnum("type", PaoType.class);
        }});
        
        return paoTypes;
    }
    
}