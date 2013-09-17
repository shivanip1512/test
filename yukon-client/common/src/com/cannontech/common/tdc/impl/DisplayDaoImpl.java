package com.cannontech.common.tdc.impl;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.tdc.dao.DisplayDao;
import com.cannontech.common.tdc.model.Column;
import com.cannontech.common.tdc.model.ColumnTypeEnum;
import com.cannontech.common.tdc.model.Display;
import com.cannontech.common.tdc.model.DisplayTypeEnum;
import com.cannontech.common.tdc.model.IDisplay;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.AlarmCatDao;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.database.data.lite.LiteAlarmCategory;
import com.cannontech.message.dispatch.message.Signal;

public class DisplayDaoImpl implements DisplayDao {

    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private AlarmCatDao alarmCatDao;
    private final YukonRowMapper<Display> displayRowMapper = createDisplayRowMapper();

    @Override
    public List<Display> getDisplayByType(DisplayTypeEnum type) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT DISPLAYNUM, NAME, TYPE, TITLE, DESCRIPTION");
        sql.append("FROM DISPLAY");
        sql.append("WHERE TYPE").eq(type.getDatabaseRepresentation());
        sql.append("ORDER BY DISPLAYNUM");
        List<Display> displays = yukonJdbcTemplate.query(sql, displayRowMapper);
        Map<Integer, String> mappedDisplayIdToAlarmCategoryName = mapDisplayIdToAlarmCategoryName();
        for (Display display : displays) {
            /* Substitute display name with the category name */
            subDisplayNameWithCatName(mappedDisplayIdToAlarmCategoryName, display);
            List<Column> columns = getColumnsByDisplayId(display);
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
     * Method to substitute display name with category name if applicable
     * @param mappedDisplayIdToAlarmCategoryName
     * @param display
     */
    private void subDisplayNameWithCatName(Map<Integer, String> mappedDisplayIdToAlarmCategoryName,
                                           Display display) {
        if (display.getType() == DisplayTypeEnum.ALARMS_AND_EVENTS
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
     * @return
     */
    private Map<Integer, String> mapDisplayIdToAlarmCategoryName() {

        List<LiteAlarmCategory> categories = alarmCatDao.getAlarmCategories();
        Map<Integer, String> mappedDisplays = new HashMap<>();

        int startingDisplayNumber = IDisplay.GLOBAL_ALARM_DISPLAY + 1;
        for (LiteAlarmCategory cat : categories) {
            if (cat.getAlarmStateID() != Signal.EVENT_SIGNAL) {
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
        YukonRowMapper<Column> columnRowMapper = createColumnRowMapper(display.getType());

        return yukonJdbcTemplate.query(sql, columnRowMapper);
    }

    private YukonRowMapper<Display> createDisplayRowMapper() {

        final YukonRowMapper<Display> mapper = new YukonRowMapper<Display>() {
            @Override
            public Display mapRow(YukonResultSet rs) throws SQLException {
                final Display display = new Display();
                display.setDisplayId(rs.getInt("DISPLAYNUM"));
                display.setName(rs.getStringSafe("NAME"));
                display.setType(rs.getEnum("TYPE", DisplayTypeEnum.class));
                display.setTitle(rs.getStringSafe("TITLE"));
                display.setDescription(rs.getStringSafe("DESCRIPTION"));
                /*
                 * For the displays where acknowledge is set to true the acknowledge button will be
                 * displayed on the page
                 */
                display
                    .setAcknowledge((display.getDisplayId() == IDisplay.GLOBAL_ALARM_DISPLAY
                                     || (display.getType() == DisplayTypeEnum.ALARMS_AND_EVENTS
                                         && display.getDisplayId() != IDisplay.SOE_LOG_DISPLAY_NUMBER
                                         && display.getDisplayId() != IDisplay.TAG_LOG_DISPLAY_NUMBER
                                         && display.getDisplayId() != IDisplay.EVENT_VIEWER_DISPLAY_NUMBER)
                                     || display.getType() == DisplayTypeEnum.CUSTOM_DISPLAYS));
                return display;
            }
        };
        return mapper;
    }

    private YukonRowMapper<Column> createColumnRowMapper(final DisplayTypeEnum type) {

        final YukonRowMapper<Column> mapper = new YukonRowMapper<Column>() {
            @Override
            public Column mapRow(YukonResultSet rs) throws SQLException {
                final Column column = new Column();
                column.setDisplayId(rs.getInt("DISPLAYNUM"));
                column.setTitle(rs.getStringSafe("TITLE"));
                column.setOrder(rs.getInt("ORDERING"));
                if (type == DisplayTypeEnum.CUSTOM_DISPLAYS) {
                    column.setType(ColumnTypeEnum.getByTypeId(rs.getInt("TYPENUM")));
                } else {
                    column.setType(ColumnTypeEnum.getByName(column.getTitle()));
                }
                column.setWidth(rs.getInt("WIDTH"));
                return column;
            }
        };
        return mapper;
    }
}
