package com.cannontech.web.search.lucene.index.site;

import static com.cannontech.message.dispatch.message.DbChangeCategory.WEB_SCHEDULE;

import java.sql.SQLException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.search.lucene.index.AbstractIndexManager.IndexUpdateInfo;

public class WebSchedulePageIndexBuilder extends DbPageIndexBuilder {

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DbChangeManager dbChangeManager;

    private final static SqlFragmentSource baseQuery;
    private final static SqlFragmentSource queryTables;
    private final static SqlFragmentSource allWhereClause;

    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT job.jobid, value");
        baseQuery = sql;

        sql = new SqlStatementBuilder();
        sql.append("FROM jobProperty jp");
        sql.append("    JOIN job job ON job.jobid = jp.jobid");
        queryTables = sql;

        allWhereClause = new SqlStatementBuilder("name LIKE 'name' ");
    }

    protected WebSchedulePageIndexBuilder() {
        super("group", DBChangeMsg.USES_NEW_CATEGORY_ENUM - WEB_SCHEDULE.ordinal(), "WebSchedule");
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

        builder.pageKey(createPageKey(jobId));

        builder.module("tools");
        builder.pageName("schedules.VIEW");
        builder.path("/group/scheduledGroupRequestExecutionResults/detail?jobId=" + jobId);

        builder.pageArgs(String.valueOf(jobId));
        builder.summaryArgs(rs.getString("value"));

        return builder.build();
    }

    @Override
    public SqlFragmentSource getWhereClauseForDbChange(int database, String category, int id) {
        return new SqlStatementBuilder("job.jobid").eq(id);
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        if (!rolePropertyDao.checkRole(YukonRole.SCHEDULER, user)) {
            return new PrefixQuery(new Term("pageKey", getPageKeyBase()));
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
