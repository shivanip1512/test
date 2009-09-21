package com.cannontech.common.device.groups.dao.impl.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.device.groups.dao.DeviceGroupComposedGroupDao;
import com.cannontech.common.device.groups.dao.DeviceGroupProviderDao;
import com.cannontech.common.device.groups.model.DeviceGroup;
import com.cannontech.common.device.groups.model.DeviceGroupComposed;
import com.cannontech.common.device.groups.model.DeviceGroupComposedCompositionType;
import com.cannontech.common.device.groups.model.DeviceGroupComposedGroup;
import com.cannontech.common.device.groups.service.DeviceGroupComposedService;
import com.cannontech.common.pao.YukonDevice;
import com.cannontech.common.util.SimpleSqlFragment;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.common.util.predicate.AggregateAndPredicate;
import com.cannontech.common.util.predicate.Predicate;

public class ComposedGroupProvider extends DeviceGroupProviderSqlBase {

    private DeviceGroupProviderDao deviceGroupDao;
    private DeviceGroupComposedService deviceGroupComposedService;
    private DeviceGroupComposedGroupDao deviceGroupComposedGroupDao;
    
    private Logger log = YukonLogManager.getLogger(ComposedGroupProvider.class);
    
    @Override
    public List<DeviceGroup> getChildGroups(DeviceGroup group) {
        return Collections.emptyList();
    }
    
    @Override
    public boolean isChildDevice(DeviceGroup group, YukonDevice device) {
        
        int deviceId = device.getPaoIdentifier().getPaoId();
        
        DeviceGroupComposed deviceGroupComposed = deviceGroupComposedService.getDeviceGroupComposed(group);
        DeviceGroupComposedCompositionType compositionType = deviceGroupComposed.getDeviceGroupComposedCompositionType();
        
        if (compositionType.equals(DeviceGroupComposedCompositionType.INTERSECTION)) {
            
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT ypo.PAObjectID");
            sql.append("FROM YukonPAObject ypo");
            sql.append("JOIN Device d ON ypo.PAObjectID = d.DEVICEID");
            sql.append("WHERE ypo.PAObjectID = ").appendArgument(deviceId);
            sql.append("AND");
            
            SqlFragmentSource childDeviceGroupSqlWhereClause = getChildDeviceGroupSqlWhereClause(group, "ypo.PAObjectID");
            sql.append(childDeviceGroupSqlWhereClause);
            
            try {
                getSimpleJdbcTemplate().queryForInt(sql.getSql(), sql.getArguments());
                return true;
            } catch (EmptyResultDataAccessException e) {
                return false;
            }
            
        // TODO Is this a worthwhile optimization? Is it an optimization at all? Is there a better way to optimize the "any" case?
        } else if (compositionType.equals(DeviceGroupComposedCompositionType.UNION)) {
            
            List<DeviceGroupComposedGroup> compositionGroups = deviceGroupComposedGroupDao.getComposedGroupsForId(deviceGroupComposed.getDeviceGroupComposedId());
            for (DeviceGroupComposedGroup compositionGroup : compositionGroups) {
                
                boolean not = compositionGroup.isNot();
                DeviceGroup searchGroup = compositionGroup.getDeviceGroup();
                if (searchGroup == null) {
                    log.debug("Composition group does not exist, device cannot be a child, skipping search in this group.");
                    continue;
                }
                
                if ((!not && deviceGroupDao.isDeviceInGroup(searchGroup, device)) || (not && !deviceGroupDao.isDeviceInGroup(searchGroup, device))) {
                    return true;
                }
            }

            return false;
            
        } else {
            throw new IllegalArgumentException("Unhandled DeviceGroupComposedCompositionType: " + compositionType.name());
        }
    }
    
    @Override
    public SqlFragmentSource getChildDeviceGroupSqlWhereClause(DeviceGroup group, String identifier) {
        
        CompositionTypeAndFragments compositionTypeAndFragments = getCompositionTypeAndFragmentsForGroup(group);
        DeviceGroupComposedCompositionType compositionType = compositionTypeAndFragments.getCompositionType();
        List<SqlFragmentSource> compositionFragments = compositionTypeAndFragments.getSqlFragments();
        if (compositionFragments.size() == 0) {
            return new SimpleSqlFragment("0 = 1"); 
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(identifier, " IN ( ");
        sql.append("SELECT ypo.PAObjectID");
        sql.append("FROM YukonPAObject ypo");
        sql.append("JOIN Device d ON ypo.PAObjectID = d.DEVICEID");
        sql.append("WHERE");
        
        boolean isFirst = true;
        for (SqlFragmentSource compositionFragment : compositionFragments) {
            
            if (!isFirst) {
                sql.append(compositionType.getSqlFragmentCombiner());
            } else {
                isFirst = false;
            }
            
            sql.append("(");
            sql.appendFragment(compositionFragment);
            sql.append(")");
        }
        
        sql.append(")");
        
        return sql;
    }
    
    @Override
    public boolean isGroupCanMoveUnderGroup(DeviceGroup groupToMove, DeviceGroup proposedParent) {
        return getGroupCanMovePredicate(groupToMove).evaluate(proposedParent);
    }
    
    @Override
    public Predicate<DeviceGroup> getGroupCanMovePredicate(DeviceGroup groupToMove) {
        
        Predicate<DeviceGroup> basicCheckPredicate = super.getGroupCanMovePredicate(groupToMove);
        
        List<DeviceGroupComposedGroup> compositionGroups = deviceGroupComposedService.getCompositionGroups(groupToMove);
        final List<DeviceGroup> compositionGroupGroups = new ArrayList<DeviceGroup>(compositionGroups.size());
        for (DeviceGroupComposedGroup compositionGroup : compositionGroups) {
            
            DeviceGroup compositionGroupDeviceGroup = compositionGroup.getDeviceGroup();
            if (compositionGroupDeviceGroup == null) {
                continue;
            }
            compositionGroupGroups.add(compositionGroupDeviceGroup);
        }
        
        Predicate<DeviceGroup> composedGroupCanMoveUnderPredicate = new Predicate<DeviceGroup>(){
            
            @Override
            public boolean evaluate(DeviceGroup deviceGroup) {
                
                for (DeviceGroup compositionGroupGroup : compositionGroupGroups) {
                    if (compositionGroupGroup.isEqualToOrDescendantOf(deviceGroup)) {
                        return false;
                    }
                }
                
                return true;
            }
        };
        
        List<Predicate<DeviceGroup>> predicates = new ArrayList<Predicate<DeviceGroup>>();
        predicates.add(basicCheckPredicate);
        predicates.add(composedGroupCanMoveUnderPredicate);
        
        return new AggregateAndPredicate<DeviceGroup>(predicates);
    };
    
    
    // HELPERS
    private CompositionTypeAndFragments getCompositionTypeAndFragmentsForGroup(DeviceGroup group) throws IllegalArgumentException {
        
        DeviceGroupComposed deviceGroupComposed = deviceGroupComposedService.getDeviceGroupComposed(group);
        List<DeviceGroupComposedGroup> compositionGroups = deviceGroupComposedGroupDao.getComposedGroupsForId(deviceGroupComposed.getDeviceGroupComposedId());
        
        List<SqlFragmentSource> sqlFragments = new ArrayList<SqlFragmentSource>(compositionGroups.size());
        for (DeviceGroupComposedGroup compositionGroup : compositionGroups) {
            
            DeviceGroup deviceGroup = compositionGroup.getDeviceGroup(); // is null if couldn't be resolved when it was loaded
            
            SqlFragmentSource sqlFragmentSource;
            if (deviceGroup != null) {
                sqlFragmentSource = deviceGroupService.getDeviceGroupSqlWhereClause(Collections.singletonList(deviceGroup), "ypo.PAObjectID");
            } else {
                sqlFragmentSource = new SimpleSqlFragment("1=0");
            }
            
            boolean isNot = compositionGroup.isNot();
            if (isNot) {
                
                SqlStatementBuilder notSql = new SqlStatementBuilder();
                notSql.append("(NOT (");
                notSql.appendFragment(sqlFragmentSource);
                notSql.append("))");
                
                sqlFragmentSource = notSql;
            }
            
            sqlFragments.add(sqlFragmentSource);
        }
        
        return new CompositionTypeAndFragments(deviceGroupComposed.getDeviceGroupComposedCompositionType(), sqlFragments);
    }
    
    private class CompositionTypeAndFragments {
        
        private DeviceGroupComposedCompositionType compositionType;
        private List<SqlFragmentSource> sqlFragments;
        
        public CompositionTypeAndFragments(DeviceGroupComposedCompositionType compositionType, List<SqlFragmentSource> sqlFragments) {
            this.compositionType = compositionType;
            this.sqlFragments = sqlFragments;
        }
        
        public DeviceGroupComposedCompositionType getCompositionType() {
            return compositionType;
        }
        public List<SqlFragmentSource> getSqlFragments() {
            return sqlFragments;
        }
    }
    
    @Autowired
    public void setDeviceGroupDao(DeviceGroupProviderDao deviceGroupDao) {
        this.deviceGroupDao = deviceGroupDao;
    }
    @Autowired
    public void setDeviceGroupComposedService(DeviceGroupComposedService deviceGroupComposedService) {
        this.deviceGroupComposedService = deviceGroupComposedService;
    }
    @Autowired
    public void setDeviceGroupComposedGroupDao(DeviceGroupComposedGroupDao deviceGroupComposedGroupDao) {
        this.deviceGroupComposedGroupDao = deviceGroupComposedGroupDao;
    }
}
