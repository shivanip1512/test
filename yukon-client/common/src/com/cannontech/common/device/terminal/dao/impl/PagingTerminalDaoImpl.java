package com.cannontech.common.device.terminal.dao.impl;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.device.terminal.dao.PagingTerminalDao;
import com.cannontech.common.device.terminal.model.TerminalBase;
import com.cannontech.common.model.Direction;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;

public class PagingTerminalDaoImpl implements PagingTerminalDao {
    @Autowired private YukonJdbcTemplate jdbcTemplate;
    private final YukonRowMapper<TerminalBase> rowMapper = createRowMapper();
    private static final List<String> paoTypes = new ArrayList<String>() {
        {
            add(PaoType.SNPP_TERMINAL.getDbString());
            add(PaoType.TNPP_TERMINAL.getDbString());
            add(PaoType.WCTP_TERMINAL.getDbString());
            add(PaoType.TAPTERMINAL.getDbString());
        }
    };

    @Override
    public List<TerminalBase> getAllTerminals(SortBy sortBy, Direction direction, String terminalName) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT PAObjectID, PAOName, Type, DisableFlag FROM YukonPAObject WHERE TYPE ");
        sql.in(paoTypes);
        if (StringUtils.isNotEmpty(terminalName)) {
            sql.append("AND UPPER(PAOName) LIKE");
            sql.append("'%" + terminalName.toUpperCase() + "%'");
        }

        if (sortBy != null) {
            sql.append("ORDER BY");
            sql.append(sortBy.getDbString());
            if (direction != null) {
                sql.append(direction);
            }
        }
        return jdbcTemplate.query(sql, rowMapper);

    }

    private YukonRowMapper<TerminalBase> createRowMapper() {
        final YukonRowMapper<TerminalBase> mapper = new YukonRowMapper<TerminalBase>() {
            public TerminalBase mapRow(YukonResultSet rs) throws SQLException {
                final TerminalBase terminal = new TerminalBase();
                terminal.setId(rs.getInt("PAObjectID"));
                terminal.setName(rs.getString("PAOName"));
                terminal.setEnabled(rs.getString("DisableFlag").equals("N"));
                terminal.setType(PaoType.getForDbString(rs.getString("Type")));
                return terminal;
            }
        };
        return mapper;
    }

}
