package com.cannontech.web.picker;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.bulk.filter.AbstractRowMapperWithBaseQuery;
import com.cannontech.common.bulk.filter.PostProcessingFilter;
import com.cannontech.common.bulk.filter.SqlFilter;
import com.cannontech.common.search.result.UltraLightYukonUser;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.stars.core.dao.EnergyCompanyDao;
import com.cannontech.stars.energyCompany.model.EnergyCompany;
import com.cannontech.user.YukonUserContext;
import com.google.common.collect.Lists;

public class ECOperatorCandidatePicker extends DatabasePicker<UltraLightYukonUser> {

    @Autowired private EnergyCompanyDao ecDao;

    private final static String[] searchColumnNames = new String[] {
        "UserId", "UserName"};
    
    private static List<OutputColumn> outputColumns;
    static {
        List<OutputColumn> columns = Lists.newArrayList();
        columns.add(new OutputColumn("userName", "yukon.web.picker.user.name"));        
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

    @Override
    protected void updateFilters(List<SqlFilter> sqlFilters,
            List<PostProcessingFilter<UltraLightYukonUser>> postProcessingFilters,
            String extraArgs, YukonUserContext userContext) {
        EnergyCompany energyCompany = ecDao.getEnergyCompany(userContext.getYukonUser());
        sqlFilters.add(new OperatorCandidateFilter(energyCompany.getId()));
    }
    
    private static class ECOperatorCandidateRowMapper extends
            AbstractRowMapperWithBaseQuery<UltraLightYukonUser> {

        @Override
        public SqlFragmentSource getBaseQuery() {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("SELECT UserId, UserName");
            sql.append("FROM YukonUser YU");
            return sql;
        }

        @Override
        public UltraLightYukonUser mapRow(YukonResultSet rs) throws SQLException {
            final String username = rs.getString("UserName");
            final int userId = rs.getInt("UserId");
            final UltraLightYukonUser user = new UltraLightYukonUser() {
                @Override
                public String getUserName() {
                    return username;
                }

                @Override
                public String getUserGroupName() {
                    return "";
                }

                @Override
                public int getUserId() {
                    return userId;
                }
            };
            return user;
        }

        @Override
        public boolean needsWhere() {
            return true;
        }
    }
    
    private class OperatorCandidateFilter implements SqlFilter {
        private int energyCompanyId;

        public OperatorCandidateFilter(int energyCompanyId) {
            this.energyCompanyId = energyCompanyId;
        }

        @Override
        public SqlFragmentSource getWhereClauseFragment() {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("YU.UserId NOT IN(SELECT OperatorLoginID from EnergyCompanyOperatorLoginList)");
            sql.append("AND (YU.UserGroupId IS NOT null AND YU.UserGroupId IN");
            sql.append("    (SELECT UserGroupId FROM ECToOperatorGroupMapping ECTRM WHERE ECTRM.EnergyCompanyId").eq(energyCompanyId).append("))");
            sql.append("            OR ((YU.UserGroupId IS NULL AND YU.UserId NOT IN");
            sql.append("                   (SELECT C.LoginId FROM CustomerAccount CA");
            sql.append("                         JOIN Customer CU ON  CU.CustomerId=CA.CustomerId");
            sql.append("                         JOIN Contact C ON  C.ContactID=CU.PrimaryContactID))");
            sql.append("            AND ((YU.UserGroupId IS NULL AND YU.UserId NOT IN");
            sql.append("                   (SELECT C.LoginId FROM CustomerAccount CA");
            sql.append("                         JOIN CustomerAdditionalContact CAC ON CA.CustomerId = CAC.CustomerId");
            sql.append("                         JOIN Contact C ON CA.CustomerId = C.ContactID))))");
            return sql;
        }
    }
}
