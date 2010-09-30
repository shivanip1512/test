package com.cannontech.core.roleproperties.dao.impl;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.DBPersistentDao;
import com.cannontech.core.roleproperties.DescriptiveRoleProperty;
import com.cannontech.core.roleproperties.GroupRolePropertyValueCollection;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.core.roleproperties.RolePropertyValue;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyEditorDao;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.data.lite.LiteYukonGroup;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.i18n.YukonMessageSourceResolvable;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ImmutableMap.Builder;

public class RolePropertyEditorDaoImpl implements RolePropertyEditorDao {
    
    private RolePropertyDaoImpl rolePropertyDao;
    private YukonJdbcOperations yukonJdbcOperations;
    private ImmutableMap<YukonRoleProperty, DescriptiveRoleProperty> descriptiveRoleProperties;
    private NextValueHelper nextValueHelper;
    private DBPersistentDao dbPersistentDao;
    
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
                    MessageSourceResolvable keyNameMsr = YukonMessageSourceResolvable.createDefaultWithoutCode(keyName);
                    MessageSourceResolvable descriptionMsr = YukonMessageSourceResolvable.createDefaultWithoutCode(description);

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
                value = new DescriptiveRoleProperty(yukonRoleProperty);
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
    public GroupRolePropertyValueCollection getForGroup(LiteYukonGroup group, boolean defaultForBlank) {
        GroupRolePropertyValueCollection result = getForGroupAndPredicate(group, defaultForBlank, Predicates.<YukonRoleProperty>alwaysTrue());
        return result;
    }

    @Override
    public GroupRolePropertyValueCollection getForGroupAndRole(LiteYukonGroup group, final YukonRole role, boolean defaultForBlank) {
        Predicate<YukonRoleProperty> predicate = new Predicate<YukonRoleProperty>() {
            public boolean apply(YukonRoleProperty input) {
                return input.getRole() == role;
            }
        };
        
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
        return Iterables.filter(Arrays.asList(YukonRoleProperty.values()), predicate);
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
                        DBChangeMsg.CHANGE_TYPE_UPDATE);
        dbPersistentDao.processDBChange(dbChangeMsg);
    }
    
    private void save(LiteYukonGroup liteYukonGroup, RolePropertyValue rolePropertyValue) {
        // find default value
        YukonRoleProperty yukonRoleProperty = rolePropertyValue.getYukonRoleProperty();
        DescriptiveRoleProperty descriptiveRoleProperty = descriptiveRoleProperties.get(yukonRoleProperty);
        Object defaultValue = descriptiveRoleProperty.getDefaultValue();
        Object valueToStore = rolePropertyValue.getValue();
        if (defaultValue.equals(valueToStore)) {
            valueToStore = null;
        }
        
        String dbTextValue = InputTypeFactory.convertPropertyValue(valueToStore, descriptiveRoleProperty.getYukonRoleProperty().getType());
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("update YukonGroupRole");
        sql.append("set value").eq(dbTextValue);
        sql.append("where groupId").eq(liteYukonGroup.getGroupID());
        sql.append(  "and rolePropertyId").eq(yukonRoleProperty.getPropertyId());
        
        int updated = yukonJdbcOperations.update(sql);
        if (updated == 0) {
            int nextValue = nextValueHelper.getNextValue("YukonGroupRole");
            
            SqlStatementBuilder sql2 = new SqlStatementBuilder();
            sql2.append("insert into YukonGroupRole");
            sql2.values(nextValue, liteYukonGroup.getGroupID(), yukonRoleProperty.getRole().getRoleId(), yukonRoleProperty.getPropertyId(), dbTextValue);
            
            yukonJdbcOperations.update(sql2);
        }
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
