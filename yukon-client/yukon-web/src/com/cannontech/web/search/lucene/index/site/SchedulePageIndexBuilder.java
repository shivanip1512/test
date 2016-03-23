package com.cannontech.web.search.lucene.index.site;

import static com.cannontech.message.dispatch.message.DbChangeCategory.SCHEDULE;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.search.lucene.index.AbstractIndexManager.IndexUpdateInfo;

public class SchedulePageIndexBuilder extends DbPageIndexBuilder {

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DbChangeManager dbChangeManager;

    private final static SqlFragmentSource baseQuery;
    private final static SqlFragmentSource queryTables;
    private final static SqlFragmentSource allWhereClause;

    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT job.jobid, value, beanName");
        baseQuery = sql;

        sql = new SqlStatementBuilder();
        sql.append("FROM jobProperty jp");
        sql.append("    JOIN job job ON job.jobid = jp.jobid");
        queryTables = sql;

        List<String> scheduleType = new ArrayList<>();
        scheduleType.add("scheduledArchivedDataFileExportJobDefinition");
        scheduleType.add("scheduledBillingFileExportJobDefinition");
        scheduleType.add("scheduledWaterLeakFileExportJobDefinition");
        scheduleType.add("scheduledMeterEventsFileExportJobDefinition");
        scheduleType.add("scheduledGroupRequestExecutionJobDefinition");
        
        sql = new SqlStatementBuilder();
        sql.append("name").eq("name");
        sql.append("AND disabled").neq("D");
        sql.append("AND beanName").in(scheduleType);
        allWhereClause = new SqlStatementBuilder("name").eq("name");
    }

    protected SchedulePageIndexBuilder() {
        super("jobs", DBChangeMsg.USES_NEW_CATEGORY_ENUM - SCHEDULE.ordinal(), "Schedule");
    }

    @Override
    protected SqlFragmentSource getBaseQuery() {
        return baseQuery;
    }

    @Override
    protected SqlFragmentSource getQueryTables() {
        return queryTables;
    }

    @Override
    protected SqlFragmentSource getAllWhereClause() {
        return allWhereClause;
    }

    @Override
    protected Document createDocument(YukonResultSet rs) throws SQLException {
        DocumentBuilder builder = new DocumentBuilder();

        int jobId = rs.getInt("jobId");
        String beanName = rs.getString("beanName");
        
        builder.pageKey(createPageKey(jobId));
        
        if ("scheduledArchivedDataFileExportJobDefinition".equals(beanName)) {
            builder.module("tools");
            builder.pageName("bulk.archivedValueExporter");
            builder.path("/tools/data-exporter/view");
            
        } else if ("scheduledBillingFileExportJobDefinition".equals(beanName)) {
            builder.module("amr");
            builder.pageName("billing");
            builder.path("/billing/home/");

        } else if ("scheduledWaterLeakFileExportJobDefinition".equals(beanName)) {
            builder.module("amr");
            builder.pageName("waterLeakReport.report");
            builder.path("/amr/waterLeakReport/report");

        } else if ("scheduledMeterEventsFileExportJobDefinition".equals(beanName)) {
            builder.module("amr");
            builder.pageName("meterEventsReport.report");
            builder.path("/amr/meterEventsReport/home");

        } else if ("scheduledGroupRequestExecutionJobDefinition".equals(beanName)) {
            builder.module("tools");
            builder.pageName("schedules.VIEW");
            builder.path("/group/scheduledGroupRequestExecutionResults/jobs");
            
        }
        
        builder.pageArgs(rs.getString("value"));
        return builder.build();
    }

    @Override
    public SqlFragmentSource getWhereClauseForDbChange(int database, String category, int id) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("job.jobid").eq(id);
        sql.append("AND disabled").neq("D");
        return sql;
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        
        PrefixQuery fileExportQuery = null;
        if (!rolePropertyDao.checkProperty(YukonRoleProperty.ARCHIVED_DATA_EXPORT, user)) {
            fileExportQuery = new PrefixQuery(new Term("pageName", "bulk.archivedValueExporter"));
            
        }
        
        PrefixQuery billingFileExportQuery = null;
        if (!rolePropertyDao.checkRole(YukonRole.APPLICATION_BILLING, user)) {
            billingFileExportQuery = new PrefixQuery(new Term("pageName", "billing"));
        }
        
        PrefixQuery waterLeakExportQuery = null;
        if (!rolePropertyDao.checkRole(YukonRole.REPORTING, user) && !rolePropertyDao.checkRole(YukonRole.METERING, user)) {
            waterLeakExportQuery = new PrefixQuery(new Term("pageName", "waterLeakReport.report"));

        }
        
        PrefixQuery meterEventsExportQuery = null;
        if (!rolePropertyDao.checkProperty(YukonRoleProperty.METER_EVENTS, user)) {
            meterEventsExportQuery = new PrefixQuery(new Term("pageName", "meterEventsReport.report"));
 
        }
        
        PrefixQuery scheduleGroupExecutionResultQuery = null;
        if (!rolePropertyDao.checkRole(YukonRole.SCHEDULER, user)) {
            scheduleGroupExecutionResultQuery = new PrefixQuery(new Term("pageName", "schedules.VIEW"));
        }
        
        if (fileExportQuery != null && billingFileExportQuery != null && waterLeakExportQuery != null && meterEventsExportQuery != null
                && scheduleGroupExecutionResultQuery != null) {
                BooleanQuery.Builder limitingQuery = new BooleanQuery.Builder();
                limitingQuery.add(fileExportQuery, Occur.SHOULD);
                limitingQuery.add(billingFileExportQuery, Occur.SHOULD);
                limitingQuery.add(waterLeakExportQuery, Occur.SHOULD);
                limitingQuery.add(meterEventsExportQuery, Occur.SHOULD);
                limitingQuery.add(scheduleGroupExecutionResultQuery, Occur.SHOULD);
                return limitingQuery.build();
            } else if (fileExportQuery != null) {
                return fileExportQuery;
            } else if (billingFileExportQuery != null) {
                return billingFileExportQuery;
            } else if (waterLeakExportQuery != null) {
                return waterLeakExportQuery;
            } else if (meterEventsExportQuery != null) {
                return meterEventsExportQuery;
            } else if (scheduleGroupExecutionResultQuery != null) {
                return scheduleGroupExecutionResultQuery;
            }
        
        return null;
    }

    @Override
    public IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database,
            String category) {
        return super.processDBChange(dbChangeType, id, database, category);
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user) {
        return true;
    }
}
