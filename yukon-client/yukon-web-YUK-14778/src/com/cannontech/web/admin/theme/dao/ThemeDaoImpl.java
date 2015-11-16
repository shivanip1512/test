package com.cannontech.web.admin.theme.dao;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.SqlParameterSink;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowCallbackHandler;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.web.admin.theme.model.Theme;
import com.cannontech.web.admin.theme.model.ThemePropertyType;

public class ThemeDaoImpl implements ThemeDao {
    
    @Autowired private YukonJdbcTemplate template;
    @Autowired private NextValueHelper nvh;
    
    private final static YukonRowMapper<Theme> THEME_MAPPER = new YukonRowMapper<Theme>() {
        @Override
        public Theme mapRow(YukonResultSet rs) throws SQLException {
            Theme theme = new Theme();
            int themeId = rs.getInt("ThemeId");
            theme.setThemeId(themeId);
            theme.setName(rs.getString("Name"));
            theme.setEditable(themeId >= 0); /* default themes have negative id's */
            theme.setCurrentTheme(rs.getBoolean("IsCurrent"));
            return theme;
        }
    };
    
    private Theme getTheme(SqlFragmentSource where) {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ThemeId, Name, IsCurrent");
        sql.append("from Theme");
        sql.appendFragment(where);
        
        final Theme theme = template.queryForObject(sql, THEME_MAPPER);
        
        sql = new SqlStatementBuilder();
        sql.append("select Property, Value");
        sql.append("from ThemeProperty");
        sql.append("where ThemeId").eq(theme.getThemeId());
        
        template.query(sql, new YukonRowCallbackHandler() {
            @Override
            public void processRow(YukonResultSet rs) throws SQLException {
                ThemePropertyType type = rs.getEnum("Property", ThemePropertyType.class);
                String value = rs.getString("Value");
                theme.getProperties().put(type, value);
            }
        });
        
        return theme;
    }

    @Override
    public Theme getTheme(final int themeId) {
        
        SqlStatementBuilder where = new SqlStatementBuilder();
        where.append("where ThemeId").eq(themeId);
        
        return getTheme(where);
    }
    
    @Override
    public Theme getCurrentTheme() {
        
        SqlStatementBuilder where = new SqlStatementBuilder();
        where.append("where IsCurrent").eq(true);
        
        return getTheme(where);
    }
    
    @Override
    public List<Theme> getThemes() {
        
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ThemeId, Name, IsCurrent");
        sql.append("from Theme");
        
        List<Theme> themes = template.query(sql, THEME_MAPPER);
        
        for (final Theme theme : themes) {
            sql = new SqlStatementBuilder();
            sql.append("select Property, Value");
            sql.append("from ThemeProperty");
            sql.append("where ThemeId").eq(theme.getThemeId());
            
            template.query(sql, new YukonRowCallbackHandler() {
                @Override
                public void processRow(YukonResultSet rs) throws SQLException {
                    ThemePropertyType type = rs.getEnum("Property", ThemePropertyType.class);
                    String value = rs.getString("Value");
                    theme.getProperties().put(type, value);
                }
            });
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
            values.addValue("IsCurrent", theme.isCurrentTheme());
            
            template.update(sql);
            theme.setThemeId(themeId);
            
        } else {
            /** update existing theme */
            SqlParameterSink values = sql.update("Theme");
            values.addValue("Name", theme.getName());
            values.addValue("IsCurrent", theme.isCurrentTheme());
            sql.append("where ThemeId").eq(theme.getThemeId());
            
            template.update(sql);
            
        }
        
        sql = new SqlStatementBuilder();
        sql.append("delete from ThemeProperty where ThemeId").eq(theme.getThemeId());
        template.update(sql);
        
        for (ThemePropertyType property : theme.getProperties().keySet()) {
            
            sql = new SqlStatementBuilder();
            SqlParameterSink values = sql.insertInto("ThemeProperty");
            values.addValue("ThemeId", theme.getThemeId());
            values.addValue("Property", property);
            values.addValue("Value", theme.getProperties().get(property));
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
        template.update(sql);
    }

    @Override
    @Transactional
    public void setCurrentTheme(int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("update Theme").set("IsCurrent", false);
        
        template.update(sql);
        
        sql = new SqlStatementBuilder();
        sql.append("update Theme").set("IsCurrent", true);
        sql.append("where ThemeId").eq(id);
        
        template.update(sql);
    }

}