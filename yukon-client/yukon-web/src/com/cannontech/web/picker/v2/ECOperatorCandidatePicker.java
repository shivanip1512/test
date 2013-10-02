package com.cannontech.web.picker.v2;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.YukonUserDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.db.user.UltraLightYukonUser;
import com.cannontech.stars.core.service.YukonEnergyCompanyService;
import com.cannontech.stars.energyCompany.dao.EnergyCompanyDao;
import com.google.common.collect.Lists;

public class ECOperatorCandidatePicker extends DatabasePicker<UltraLightYukonUser> {

    @Autowired private YukonEnergyCompanyService yukonEnergyCompanyService;
    @Autowired private EnergyCompanyDao energyCompanyDao;
    @Autowired private YukonUserDao yukonUserDao;


    private final static String[] searchColumnNames = new String[] {
        "UserId", "Username"};
    
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        columns.add(new OutputColumn("username", "yukon.web.picker.user.name"));        
        columns.add(new OutputColumn("userId", "yukon.web.picker.user.userId"));
        outputColumns = Collections.unmodifiableList(columns);
    }

    protected ECOperatorCandidatePicker() {
        super(new ECOperatorCandidateRowMapper(), searchColumnNames);
    }
   
    @Override
    public String getIdFieldName() {
        return "userId";
    }

    @Override
    protected String getDatabaseIdFieldName() {
        return "userId";
    }

    @Override
    public List<OutputColumn> getOutputColumns() {
        return outputColumns;
    }

    public static class ECOperatorCandidateRowMapper extends
            AbstractRowMapperWithBaseQuery<UltraLightYukonUser> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT UserId, Username");
            sql.append("FROM YukonUser YU");
            sql.append("WHERE (YU.UserGroupId is null OR YU.UserGroupId not in");
            sql.append("      (SELECT UG.UserGroupId FROM ECToResidentialGroupMapping ECTGM JOIN UserGroup UG");
            sql.append("              ON UG.UserGroupId = ECTGM.UserGroupId))");
            sql.append("       AND YU.UserId not in(SELECT OperatorLoginID from EnergyCompanyOperatorLoginList)");
            sql.append("              AND YU.UserId not in(SELECT loginid from contact)");
            return sql;
        }

        @Override
        public UltraLightYukonUser mapRow(YukonResultSet rs) throws SQLException {
            UltraLightYukonUser user = new UltraLightYukonUser();
            user.setUserId(rs.getInt("UserId"));
            user.setUsername(rs.getString("Username"));
            return user;
        }
    }
}
