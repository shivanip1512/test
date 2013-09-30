package com.cannontech.system.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.system.dao.ThemeDao;
import com.cannontech.system.model.Theme;
import com.cannontech.system.model.ThemeProperty;
import com.cannontech.system.model.ThemePropertyType;

public class ThemeDaoImpl implements ThemeDao {
    
    @Autowired private YukonJdbcTemplate template;
    @Autowired private NextValueHelper nvh;
    
    private final static YukonRowMapper<ThemeProperty> PROPERTYMAPPER_MAPPER = new YukonRowMapper<ThemeProperty>() {
        @Override
        public ThemeProperty mapRow(YukonResultSet rs) throws SQLException {
            ThemeProperty property = new ThemeProperty();
            property.setType(rs.getEnum("Property", ThemePropertyType.class));
            property.setValue(rs.getString("Value"));
            return property;
        }
    };
    
    private final static YukonRowMapper<Theme> THEME_MAPPER = new YukonRowMapper<Theme>() {
        @Override
        public Theme mapRow(YukonResultSet rs) throws SQLException {
            Theme theme = new Theme();
            theme.setThemeId(rs.getInt("ThemeId"));
            theme.setName(rs.getString("Name"));
            return theme;
        }
    };

    @Override
    public Theme getTheme(final int themeId) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ThemeId, Name");
        sql.append("from Theme");
        sql.append("where ThemeId").eq(themeId);
        
        Theme theme = template.queryForObject(sql, THEME_MAPPER);
        
        sql = new SqlStatementBuilder();
        sql.append("select Property, Value");
        sql.append("from ThemeProperty");
        sql.append("where ThemeId").eq(themeId);
        
        List<ThemeProperty> properties = template.query(sql, PROPERTYMAPPER_MAPPER);
        
        theme.setProperties(properties);
        
        return theme;
    }
    
    @Override
    public List<Theme> getDefaultThemes() {
        
        List<Theme> defaults = new ArrayList<>();
        
        /* TODO add yukon blue later */
        defaults.add(getTheme(-1));
        
        return defaults;
    }

    @Override
    public List<Theme> getNonDefaultThemes() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ThemeId, Name");
        sql.append("from Theme");
        sql.append("where ThemeId").gte(0);
        
        List<Theme> themes = template.query(sql, THEME_MAPPER);
        
        for (Theme theme : themes) {
            sql = new SqlStatementBuilder();
            sql.append("select Property, Value");
            sql.append("from ThemeProperty");
            sql.append("where ThemeId").eq(theme.getThemeId());
            
            List<ThemeProperty> properties = template.query(sql, PROPERTYMAPPER_MAPPER);
            
            theme.setProperties(properties);
        }
        
        return themes;
    }

    @Override
    @Transactional
    public Theme saveTheme(Theme theme) {
        
        /* Don't allow changing default themes (they will have negative id's) */
        if (theme.getThemeId() != null && theme.getThemeId() < 0) {
            throw new IllegalArgumentException("Default themes are not changable");
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        
        if (theme.getThemeId() == null) {
            /** create new theme */
            int themeId = nvh.getNextValue("Theme");
            SqlParameterSink values = sql.insertInto("Theme");
            values.addValue("ThemeId", themeId);
            values.addValue("Name", theme.getName());
            
            template.update(sql);
            theme.setThemeId(themeId);
            
        } else {
            /** update existing theme */
            SqlParameterSink values = sql.update("Theme");
            values.addValue("Name", theme.getName());
            sql.append("where ThemeId").eq(theme.getThemeId());
            
            template.update(sql);
            
        }
        
        sql = new SqlStatementBuilder();
        sql.append("delete from ThemeProperty where ThemeId").eq(theme.getThemeId());
        template.update(sql);
        
        for (ThemeProperty property : theme.getProperties()) {
            
            sql = new SqlStatementBuilder();
            SqlParameterSink values = sql.insertInto("ThemeProperty");
            values.addValue("ThemeId", theme.getThemeId());
            values.addValue("Property", property.getType());
            values.addValue("Value", property.getValue());
            template.update(sql);
            
        }
        
        return theme;
    }

    @Override
    public void deleteTheme(int themeId) {
        
        if (themeId < 0) {
            throw new IllegalArgumentException("Default themes cannot be deleted");
        }
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("delete from Theme where ThemeId").eq(themeId);
    }

}