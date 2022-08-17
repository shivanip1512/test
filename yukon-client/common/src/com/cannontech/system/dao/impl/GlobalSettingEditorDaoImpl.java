package com.cannontech.system.dao.impl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.DecoderException;
import org.apache.logging.log4j.Logger;
import org.jdom2.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.util.Pair;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.InputTypeFactory;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.encryption.CryptoException;
import com.cannontech.system.GlobalSettingCryptoUtils;
import com.cannontech.system.GlobalSettingSubCategory;
import com.cannontech.system.GlobalSettingType;
import com.cannontech.system.dao.GlobalSettingEditorDao;
import com.cannontech.system.model.GlobalSetting;
import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class GlobalSettingEditorDaoImpl implements GlobalSettingEditorDao {
    private final Logger log = YukonLogManager.getLogger(GlobalSettingEditorDaoImpl.class);

    @Autowired private YukonJdbcTemplate jdbcTemplate;
    
    @Override
    public Map<GlobalSettingType, GlobalSetting> getSettingsForCategory(GlobalSettingSubCategory category) {
        Set<GlobalSettingType> all = GlobalSettingType.getSettingsForCategory(category);
        return getSettings(all);
    }

    @Override
    public Map<GlobalSettingType, GlobalSetting> getSettings(Set<GlobalSettingType> all) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT GlobalSettingId, Name, Value, Comments, LastChangedDate");
        sql.append("FROM GlobalSetting");
        sql.append("WHERE Name").in(all);
        
        final Set<GlobalSettingType> found = Sets.newHashSet();
        final Map<GlobalSettingType, GlobalSetting> settings = Maps.newHashMap();
        
        jdbcTemplate.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {

                GlobalSettingType type = rs.getEnum(("Name"), GlobalSettingType.class);
                Object value = InputTypeFactory.convertPropertyValue(type.getType(), rs.getString("Value"));
                if (value != null && type.isSensitiveInformation()) {
                    try {
                        if (GlobalSettingCryptoUtils.isEncrypted((String) value)) {
                            value = GlobalSettingCryptoUtils.decryptValue((String) value);
                        }
                    } catch (CryptoException | IOException | JDOMException | DecoderException e) {
                        value = type.getDefaultValue();
                        log.error("Unable to decrypt value for setting " + type + ". Using the default value. ", e);
                    }
                }
                GlobalSetting setting = new GlobalSetting(type,value);

                setting.setId(rs.getInt("GlobalSettingId"));
                setting.setComments(rs.getString("Comments"));
                setting.setLastChanged(rs.getInstant("LastChangedDate"));
                
                settings.put(type, setting);
                found.add(type);
            }
        });
        
        Set<GlobalSettingType> missing = Sets.difference(all, found);
        for (GlobalSettingType type : missing) {
            GlobalSetting setting = new GlobalSetting(type, type.getDefaultValue());
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