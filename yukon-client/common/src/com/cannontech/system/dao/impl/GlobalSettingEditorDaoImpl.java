package com.cannontech.system.dao.impl;

import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.Pair;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.YukonJdbcOperations;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.system.GlobalSettingSubCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingEditorDao;
import com.cannontech.system.model.GlobalSetting;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class GlobalSettingEditorDaoImpl implements GlobalSettingEditorDao {

    @Autowired private YukonJdbcOperations yukonJdbcOperations;
    
    @Override
    public Map<GlobalSettingType, GlobalSetting> getSettingsForCategory(GlobalSettingSubCategory category) {
        Set<GlobalSettingType> all = GlobalSettingType.getSettingsForCategory(category);
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GlobalSettingId, Name, Value, Comments, LastChangedDate");
        sql.append("FROM GlobalSetting");
        sql.append("WHERE Name").in(all);
        
        final Set<GlobalSettingType> found = Sets.newHashSet();
        final Map<GlobalSettingType, GlobalSetting> settings = Maps.newHashMap();
        
        yukonJdbcOperations.query(sql, new YukonRowCallbackHandler() {
            public void processRow(YukonResultSet rs) throws SQLException {

                GlobalSetting setting = new GlobalSetting();
                setting.setId(rs.getInt("GlobalSettingId"));
                GlobalSettingType type = rs.getEnum(("Name"), GlobalSettingType.class);
                setting.setType(type);
                setting.setValue(InputTypeFactory.convertPropertyValue(type.getType(), rs.getString("Value")));
                setting.setComments(rs.getString("Comments"));
                setting.setLastChanged(rs.getInstant("LastChangedDate"));
                
                settings.put(type, setting);
                found.add(type);
            }
        });
        
        Set<GlobalSettingType> missing = Sets.difference(all, found);
        for (GlobalSettingType type : missing) {
            GlobalSetting setting = new GlobalSetting();
            setting.setType(type);
            setting.setValue(type.getDefaultValue());
            settings.put(type, setting);
        }
        
        return settings;
    }
    
    @Override
    public Map<GlobalSettingType, Pair<Object, String>> getValuesAndCommentsForCategory(GlobalSettingSubCategory category) {
        Map<GlobalSettingType, GlobalSetting> settings = getSettingsForCategory(category);
        return Maps.transformValues(settings, new Function<GlobalSetting, Pair<Object, String>>() {
            @Override
            public Pair<Object, String> apply(GlobalSetting input) {
                Pair<Object, String> valueAndComment = Pair.of(input.getValue(), input.getComments());
                return valueAndComment;
            }
        });
    }
    
}