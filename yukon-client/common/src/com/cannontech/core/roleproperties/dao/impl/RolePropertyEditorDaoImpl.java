package com.cannontech.core.roleproperties.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.roleproperties.DescriptiveRoleProperty;
import com.cannontech.core.roleproperties.GroupRolePropertyValueCollection;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.roleproperties.RolePropertyValue;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.database.SqlUtils;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class RolePropertyEditorDaoImpl implements RolePropertyEditorDao {
    
    private final class RolePredicate implements Predicate<YukonRoleProperty> {
        private final YukonRole role;

        private RolePredicate(YukonRole role) {
            this.role = role;
        }

        public boolean apply(YukonRoleProperty input) {
            return input.getRole() == role;
        }
    }

    private RolePropertyDaoImpl rolePropertyDao;
    private YukonJdbcOperations yukonJdbcOperations;
    private ImmutableMap<YukonRoleProperty, DescriptiveRoleProperty> descriptiveRoleProperties;
    private NextValueHelper nextValueHelper;
    private DBPersistentDao dbPersistentDao;
    private static boolean WRITE_NULL_FOR_DEFAULTS = false; // set to true when db editor support is no longer needed
    private static final Logger log = YukonLogManager.getLogger(RolePropertyEditorDaoImpl.class);
    
    @PostConstruct
    public void initialize() {
        final Map<YukonRoleProperty, DescriptiveRoleProperty> descriptiveRolePropertyLookup = Maps.newHashMapWithExpectedSize(YukonRoleProperty.values().length);
        final ImmutableMap<YukonRoleProperty, Object> defaultValueLookup = rolePropertyDao.getDefaultValueLookup();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT rolePropertyId, keyName, description");
        sql.append("FROM YukonRoleProperty");
        
        yukonJdbcOperations.query(sql, new YukonRowCallbackHandler() {
            public void processRow(YukonResultSet rs) throws SQLException {
                int rolePropertyId = rs.getInt("rolePropertyId");
                String keyName = rs.getString("keyName");
                String description = rs.getString("description");

                try {
                    YukonRoleProperty roleProperty = YukonRoleProperty.getForId(rolePropertyId);
                    MessageSourceResolvable keyNameMsr = 
                        YukonMessageSourceResolvable.createDefault("yukon.common.roleProperty." + roleProperty.name() + ".name", keyName);
                    MessageSourceResolvable descriptionMsr = 
                        YukonMessageSourceResolvable.createDefault("yukon.common.roleProperty." + roleProperty.name() + ".description",description);

                    DescriptiveRoleProperty descriptiveRoleProperty = new DescriptiveRoleProperty(roleProperty, defaultValueLookup.get(roleProperty), keyNameMsr, descriptionMsr);
                    descriptiveRolePropertyLookup.put(roleProperty, descriptiveRoleProperty);
                } catch (IllegalArgumentException e) {
                    // Database contains an unknown role property, this is logged elsewhere
                }
            }
        });
        
        // now build a new map in a stable order
        Builder<YukonRoleProperty, DescriptiveRoleProperty> builder = ImmutableMap.builder();
        for (YukonRoleProperty yukonRoleProperty : YukonRoleProperty.values()) {
            DescriptiveRoleProperty value = descriptiveRolePropertyLookup.get(yukonRoleProperty);
            if (value == null) {
                /* TODO Do this the right way */
//                value = new DescriptiveRoleProperty(yukonRoleProperty);
                continue; // skip rp's that don't exist in the database
            }
            builder.put(yukonRoleProperty, value);
        }
        
        descriptiveRoleProperties = builder.build();

    }
    
    @Override
    public Map<YukonRoleProperty, DescriptiveRoleProperty> getDescriptiveRoleProperties(final YukonRole role) {
        Map<YukonRoleProperty, DescriptiveRoleProperty> result = Maps.filterKeys(descriptiveRoleProperties, new Predicate<YukonRoleProperty>() {
            public boolean apply(YukonRoleProperty input) {
                return input.getRole() == role;
            }
        });
        return result;
    }

    @Override
    public GroupRolePropertyValueCollection getForGroupAndRole(LiteYukonGroup group, final YukonRole role, boolean defaultForBlank) {
        Predicate<YukonRoleProperty> predicate = new RolePredicate(role);
        
        GroupRolePropertyValueCollection result = getForGroupAndPredicate(group, defaultForBlank, predicate);
        return result;
    }
    
    public GroupRolePropertyValueCollection getForGroupAndPredicate(LiteYukonGroup group, boolean defaultForBlank, Predicate<YukonRoleProperty> predicate) {
        final Map<YukonRoleProperty, Object> actualValueLookup = Maps.newHashMap();
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select rolePropertyId, value");
        sql.append("from YukonGroupRole ygr");
        sql.append("where ygr.GroupId").eq(group.getGroupID());
        
        yukonJdbcOperations.query(sql, new YukonRowCallbackHandler() {
            public void processRow(YukonResultSet rs) throws SQLException {
                YukonRoleProperty property = YukonRoleProperty.getForId(rs.getInt("rolePropertyId"));
                String value = rs.getStringSafe("value");
                Object convertedValue = InputTypeFactory.convertPropertyValue(property.getType(), value);
                if (convertedValue == null) {
                    convertedValue = descriptiveRoleProperties.get(property).getDefaultValue();
                }
                actualValueLookup.put(property, convertedValue);
            }
        });
        
        // now get all of the properties we are supposed to return
        Iterable<YukonRoleProperty> properties = getFilteredRoleProperties(predicate);
        
        List<RolePropertyValue> rolePropertyValues = Lists.newArrayList();
        
        for (YukonRoleProperty yukonRoleProperty : properties) {
            RolePropertyValue rolePropertyValue = new RolePropertyValue(yukonRoleProperty);
            Object actualValue = actualValueLookup.get(yukonRoleProperty);
            rolePropertyValue.setValue(actualValue);
            
            rolePropertyValues.add(rolePropertyValue);
        }
        
        GroupRolePropertyValueCollection result = new GroupRolePropertyValueCollection(group, rolePropertyValues);
        
        return result;
    }

    private Iterable<YukonRoleProperty> getFilteredRoleProperties(Predicate<YukonRoleProperty> predicate) {
        Set<YukonRoleProperty> expected = Sets.newHashSet(YukonRoleProperty.values());
        Set<YukonRoleProperty> missing = rolePropertyDao.getMissingProperties();
        Set<YukonRoleProperty> existingSet = Sets.difference(expected, missing);
        return Iterables.filter(existingSet , predicate);
    }
    
    @Override
    @Transactional
    public void addRoleToGroup(LiteYukonGroup liteYukonGroup, YukonRole role) {
        Iterable<YukonRoleProperty> roleProperties = getFilteredRoleProperties(new RolePredicate(role));
        for (YukonRoleProperty yukonRoleProperty : roleProperties) {
            String dbTextValue = getStringToStoreForValue(yukonRoleProperty, null);
            
            insertGroupRoleProperty(liteYukonGroup, yukonRoleProperty, dbTextValue);
        }
        
        sendGroupDbChange(liteYukonGroup.getGroupID(), DbChangeType.UPDATE);
    }
    
    @Override
    @Transactional
    public void removeRoleFromGroup(int groupId, int roleId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("DELETE FROM YukonGroupRole");
        sql.append("WHERE GroupId").eq(groupId);
        sql.append(  "AND RoleId").eq(roleId);
        
        yukonJdbcOperations.update(sql);
        sendGroupDbChange(groupId, DbChangeType.UPDATE);
    }
    
    @Override
    @Transactional
    public void save(GroupRolePropertyValueCollection collection) {
        List<RolePropertyValue> propertyValues = collection.getRolePropertyValues();
        
        for (RolePropertyValue rolePropertyValue : propertyValues) {
            save(collection.getLiteYukonGroup(), rolePropertyValue);
        }
        
        DBChangeMsg dbChangeMsg = new DBChangeMsg(collection.getLiteYukonGroup().getGroupID(),
                        DBChangeMsg.CHANGE_YUKON_USER_DB,
                        DBChangeMsg.CAT_YUKON_USER_GROUP,
                        "",
                        DbChangeType.UPDATE);
        dbPersistentDao.processDBChange(dbChangeMsg);
    }
    
    private void save(LiteYukonGroup liteYukonGroup, RolePropertyValue rolePropertyValue) {
        // find default value
        YukonRoleProperty yukonRoleProperty = rolePropertyValue.getYukonRoleProperty();
        Object valueToStore = rolePropertyValue.getValue();
        String dbTextValue = getStringToStoreForValue(yukonRoleProperty, valueToStore);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("update YukonGroupRole");
        sql.append("set value").eq(dbTextValue);
        sql.append("where groupId").eq(liteYukonGroup.getGroupID());
        sql.append(  "and rolePropertyId").eq(yukonRoleProperty.getPropertyId());
        
        int updated = yukonJdbcOperations.update(sql);
        if (updated == 0) {
            insertGroupRoleProperty(liteYukonGroup, yukonRoleProperty, dbTextValue);
        }
    }

    private void insertGroupRoleProperty(LiteYukonGroup liteYukonGroup,
                                          YukonRoleProperty yukonRoleProperty, String dbTextValue) {
        int nextValue = nextValueHelper.getNextValue("YukonGroupRole");
        
        SqlStatementBuilder sql2 = new SqlStatementBuilder();
        sql2.append("insert into YukonGroupRole");
        sql2.values(nextValue, liteYukonGroup.getGroupID(), yukonRoleProperty.getRole().getRoleId(), yukonRoleProperty.getPropertyId(), dbTextValue);
        
        yukonJdbcOperations.update(sql2);
    }

    private String getStringToStoreForValue(YukonRoleProperty yukonRoleProperty, Object valueToStore) {
        DescriptiveRoleProperty descriptiveRoleProperty = descriptiveRoleProperties.get(yukonRoleProperty);
        Object defaultValue = descriptiveRoleProperty.getDefaultValue();
        if (WRITE_NULL_FOR_DEFAULTS && defaultValue.equals(valueToStore)) {
            valueToStore = null;
        }
        
        String dbTextValue = InputTypeFactory.convertPropertyValue(valueToStore, descriptiveRoleProperty.getYukonRoleProperty().getType());
        dbTextValue = SqlUtils.convertStringToDbValue(dbTextValue);
        return dbTextValue;
    }
    
    private void sendGroupDbChange(int groupId, DbChangeType type) {
        DBChangeMsg dbChangeMsg = new DBChangeMsg(groupId,
                                                  DBChangeMsg.CHANGE_YUKON_USER_DB,
                                                  DBChangeMsg.CAT_YUKON_USER_GROUP,
                                                  "",
                                                  type);
        dbPersistentDao.processDBChange(dbChangeMsg);
    }

    @Autowired
    public void setRolePropertyDao(RolePropertyDaoImpl rolePropertyDao) {
        this.rolePropertyDao = rolePropertyDao;
    }
    
    @Autowired
    public void setYukonJdbcOperations(YukonJdbcOperations yukonJdbcOperations) {
        this.yukonJdbcOperations = yukonJdbcOperations;
    }
    
    @Autowired
    public void setNextValueHelper(NextValueHelper nextValueHelper) {
        this.nextValueHelper = nextValueHelper;
    }
    
    @Autowired
    public void setDbPersistentDao(DBPersistentDao dbPersistentDao) {
        this.dbPersistentDao = dbPersistentDao;
    }

}