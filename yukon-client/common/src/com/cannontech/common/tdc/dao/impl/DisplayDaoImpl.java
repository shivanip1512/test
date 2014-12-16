package com.cannontech.common.tdc.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.tdc.dao.DisplayDao;
import com.cannontech.common.tdc.model.Column;
import com.cannontech.common.tdc.model.ColumnType;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayType;
import com.cannontech.common.tdc.model.IDisplay;
import com.cannontech.common.util.ChunkingSqlTemplate;
import com.cannontech.common.util.SqlFragmentGenerator;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.message.dispatch.message.Signal;
import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DisplayDaoImpl implements DisplayDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private AlarmCatDao alarmCatDao;
    private final YukonRowMapper<Display> displayRowMapper = createDisplayRowMapper();

    @Override
    public List<Display> getDisplayByType(DisplayType type) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISPLAYNUM, NAME, TYPE, TITLE, DESCRIPTION");
        sql.append("FROM DISPLAY");
        sql.append("WHERE TYPE").eq_k(type);
        sql.append("ORDER BY DISPLAYNUM");
        List<Display> displays = yukonJdbcTemplate.query(sql, displayRowMapper);
        Map<Integer, String> mappedDisplayIdToAlarmCategoryName = mapDisplayIdToAlarmCategoryName();
        Map<Integer, List<Column>> displayToColumns = getColumnsByDisplayIds(displays);
        for (Display display : displays) {
            /* Substitute display name with the category name */
            subDisplayNameWithCatName(mappedDisplayIdToAlarmCategoryName, display);
            List<Column> columns = displayToColumns.get(display.getDisplayId());
            display.setColumns(columns);
        }
        return displays;
    }

    @Override
    public Display getDisplayById(int displayId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISPLAYNUM, NAME, TYPE, TITLE, DESCRIPTION");
        sql.append("FROM DISPLAY");
        sql.append("WHERE DISPLAYNUM").eq(displayId);
        Display display = yukonJdbcTemplate.queryForObject(sql, displayRowMapper);
        List<Column> columns = getColumnsByDisplayId(display);
        display.setColumns(columns);
        Map<Integer, String> mappedDisplayIdToAlarmCategoryName = mapDisplayIdToAlarmCategoryName();
        /* Substitute display name with the category name */
        subDisplayNameWithCatName(mappedDisplayIdToAlarmCategoryName, display);
        return display;
    }

    /**
     * Substitutes display name with category name if applicable
     */
    private void subDisplayNameWithCatName(Map<Integer, String> mappedDisplayIdToAlarmCategoryName,
                                           Display display) {
        if (display.getType() == DisplayType.ALARMS_AND_EVENTS
            && display.getDisplayId() != IDisplay.SOE_LOG_DISPLAY_NUMBER
            && display.getDisplayId() != IDisplay.TAG_LOG_DISPLAY_NUMBER
            && display.getDisplayId() != IDisplay.EVENT_VIEWER_DISPLAY_NUMBER
            && display.getDisplayId() != IDisplay.GLOBAL_ALARM_DISPLAY) {
            String categoryName = mappedDisplayIdToAlarmCategoryName.get(display.getDisplayId());
            if (StringUtils.isNotEmpty(categoryName)) {
                display.setName(categoryName);
            }
        }
    }

    /**
     * Method to map display ids to category names.
     */
    private Map<Integer, String> mapDisplayIdToAlarmCategoryName() {

        List<LiteAlarmCategory> categories = alarmCatDao.getAlarmCategories();
        Map<Integer, String> mappedDisplays = new HashMap<>();

        int startingDisplayNumber = IDisplay.GLOBAL_ALARM_DISPLAY + 1;
        for (LiteAlarmCategory cat : categories) {
            if (cat.getAlarmCategoryId() != Signal.EVENT_SIGNAL) {
                mappedDisplays.put(startingDisplayNumber++, cat.getCategoryName());
            }
        }
        return mappedDisplays;
    }

    private List<Column> getColumnsByDisplayId(Display display) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISPLAYNUM, TITLE, TYPENUM, ORDERING, WIDTH");
        sql.append("FROM DISPLAYCOLUMNS");
        sql.append("WHERE DISPLAYNUM").eq(display.getDisplayId());
        sql.append("ORDER BY ORDERING");
        Map<Integer, Display> mappedDisplays =
            Maps.uniqueIndex(Lists.newArrayList(display), new Function<Display, Integer>() {
                public Integer apply(Display display) {
                    return display.getDisplayId();
                }
            });
        YukonRowMapper<Column> columnRowMapper = createColumnRowMapper(mappedDisplays);

        return yukonJdbcTemplate.query(sql, columnRowMapper);
    }

    private Map<Integer, List<Column>> getColumnsByDisplayIds(List<Display> displays) {
        ChunkingSqlTemplate template = new ChunkingSqlTemplate(yukonJdbcTemplate);
        final List<Integer> ids = Lists.transform(displays, new Function<Display, Integer>() {
            @Override
            public Integer apply(Display display) {
                return display.getDisplayId();
            }
        });

        SqlFragmentGenerator<Integer> sqlGenerator = new SqlFragmentGenerator<Integer>() {
            public SqlFragmentSource generate(List<Integer> subList) {

                SqlStatementBuilder sql = new SqlStatementBuilder();
                sql.append("SELECT DISPLAYNUM, TITLE, TYPENUM, ORDERING, WIDTH");
                sql.append("FROM DISPLAYCOLUMNS");
                sql.append("WHERE DISPLAYNUM").in(subList);
                sql.append("ORDER BY ORDERING");
                return sql;
            }
        };
        Map<Integer, Display> mappedDisplays =
            Maps.uniqueIndex(displays, new Function<Display, Integer>() {
                public Integer apply(Display display) {
                    return display.getDisplayId();
                }
            });

        Map<Integer, List<Column>> displayToColumns = new HashMap<Integer, List<Column>>();
        List<Column> columns =
            template.query(sqlGenerator, ids, createColumnRowMapper(mappedDisplays));
        for (Column column : columns) {
            List<Column> columnForDisplay = displayToColumns.get(column.getDisplayId());
            if (columnForDisplay == null) {
                columnForDisplay = new ArrayList<Column>();
            }
            columnForDisplay.add(column);
            displayToColumns.put(column.getDisplayId(), columnForDisplay);
        }
        return displayToColumns;
    }

    private YukonRowMapper<Display> createDisplayRowMapper() {

        final YukonRowMapper<Display> mapper = new YukonRowMapper<Display>() {
            @Override
            public Display mapRow(YukonResultSet rs) throws SQLException {
                final Display display = new Display();
                display.setDisplayId(rs.getInt("DISPLAYNUM"));
                display.setName(rs.getStringSafe("NAME"));
                display.setType(rs.getEnum("TYPE", DisplayType.class));
                display.setTitle(rs.getStringSafe("TITLE"));
                display.setDescription(rs.getStringSafe("DESCRIPTION"));
                /*
                 * For the displays where acknowledge is set to true the acknowledge button will be
                 * displayed on the page
                 */
                display
                    .setAcknowledgable((display.getDisplayId() == IDisplay.GLOBAL_ALARM_DISPLAY
                                     || (display.getType() == DisplayType.ALARMS_AND_EVENTS
                                         && display.getDisplayId() != IDisplay.SOE_LOG_DISPLAY_NUMBER
                                         && display.getDisplayId() != IDisplay.TAG_LOG_DISPLAY_NUMBER
                                         && display.getDisplayId() != IDisplay.EVENT_VIEWER_DISPLAY_NUMBER)
                                     || display.getType() == DisplayType.CUSTOM_DISPLAYS));
                return display;
            }
        };
        return mapper;
    }

    private YukonRowMapper<Column> createColumnRowMapper(final Map<Integer, Display> mappedDisplays) {

        final YukonRowMapper<Column> mapper = new YukonRowMapper<Column>() {
            @Override
            public Column mapRow(YukonResultSet rs) throws SQLException {
                final Column column = new Column();
                column.setDisplayId(rs.getInt("DISPLAYNUM"));
                column.setTitle(rs.getStringSafe("TITLE"));
                column.setOrder(rs.getInt("ORDERING"));
                if (mappedDisplays.get(column.getDisplayId()).getType() == DisplayType.CUSTOM_DISPLAYS) {
                    column.setType(ColumnType.getByTypeId(rs.getInt("TYPENUM")));
                } else {
                    column.setType(ColumnType.getByName(column.getTitle()));
                }
                column.setWidth(rs.getInt("WIDTH"));
                return column;
            }
        };
        return mapper;
    }
}
