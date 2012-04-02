package com.cannontech.stars.dr.displayable.dao.impl;

import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.RowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.stars.dr.appliance.model.AssignedProgramName;
import com.cannontech.stars.dr.displayable.dao.DisplayableInventoryEnrollmentDao;
import com.cannontech.stars.dr.displayable.model.DisplayableInventoryEnrollment;
import com.cannontech.stars.dr.hardware.model.LMHardwareControlGroup;

public class DisplayableInventoryEnrollmentDaoImpl implements
        DisplayableInventoryEnrollmentDao {
    private YukonJdbcTemplate yukonJdbcTemplate;

    private RowMapperWithBaseQuery<DisplayableInventoryEnrollment> rowMapper =
        new AbstractRowMapperWithBaseQuery<DisplayableInventoryEnrollment>() {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT wp.programId AS assignedProgramId,");
            sql.append(    "wp.deviceId as programId,");
            sql.append(    "pao.paoName AS programPaoName,");
            sql.append(    "wc.alternateDisplayName,");
            sql.append(    "hcg.lmGroupId, lgPao.paoName as loadGroupName,");
            sql.append(    "hcg.relay, hcg.inventoryId");
            sql.append("FROM lmHardwareControlGroup hcg");
            sql.append(    "JOIN lmProgramWebPublishing wp ON hcg.programId = wp.programId");
            sql.append(    "JOIN yukonPAObject pao ON wp.deviceId = pao.paobjectId");
            sql.append(    "JOIN yukonPAObject lgPao ON lgPao.paobjectId = hcg.lmGroupId");
            sql.append(    "JOIN yukonWebConfiguration wc ON wc.ConfigurationId = wp.websettingsId");
            sql.append("WHERE hcg.groupEnrollStart IS NOT NULL");
            sql.append(    "AND hcg.groupEnrollStop IS NULL");
            sql.append(    "AND hcg.type").eq(LMHardwareControlGroup.ENROLLMENT_ENTRY);
            return sql;
        }

        @Override
        public boolean needsWhere() {
            return true;
        }

        @Override
        public DisplayableInventoryEnrollment mapRow(YukonResultSet rs)
                throws SQLException {
            int assignedProgramId = rs.getInt("assignedProgramId");
            int programId = rs.getInt("programId");
            AssignedProgramName name =
                new AssignedProgramName(rs.getString("programPaoName"),
                                     rs.getString("alternateDisplayName"));
            int loadGroupId = rs.getInt("lmGroupId");
            String loadGroupName = rs.getString("loadGroupName");
            int relay = rs.getInt("relay");

            return new DisplayableInventoryEnrollment(assignedProgramId,
                                                      programId, name,
                                                      loadGroupId, loadGroupName,
                                                      relay);
        }
    };

    @Override
    @Transactional(readOnly=true)
    public DisplayableInventoryEnrollment find(int accountId, int inventoryId,
            int assignedProgramId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("AND hcg.inventoryId").eq(inventoryId);
        sql.append("AND hcg.programId").eq(assignedProgramId);
        sql.append("AND hcg.accountId").eq(accountId);

        try {
            DisplayableInventoryEnrollment retVal =
                yukonJdbcTemplate.queryForObject(sql, rowMapper);
            return retVal;
        } catch (EmptyResultDataAccessException erdae) {
            // this just means they're not enrolled in this program
        }
        return null;
    }

    @Override
    @Transactional(readOnly=true)
    public List<DisplayableInventoryEnrollment> find(int accountId,
            int inventoryId) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(rowMapper.getBaseQuery());
        sql.append("AND hcg.inventoryId").eq(inventoryId);
        sql.append("AND hcg.accountId").eq(accountId);

        List<DisplayableInventoryEnrollment> retVal =
            yukonJdbcTemplate.query(sql, rowMapper);
        return retVal;
    }

    @Autowired
    public void setYukonJdbcTemplate(YukonJdbcTemplate yukonJdbcTemplate) {
        this.yukonJdbcTemplate = yukonJdbcTemplate;
    }
}
