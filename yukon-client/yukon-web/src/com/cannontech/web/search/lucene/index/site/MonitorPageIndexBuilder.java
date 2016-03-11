package com.cannontech.web.search.lucene.index.site;

import static com.cannontech.message.dispatch.message.DbChangeCategory.MONITOR;

import java.sql.SQLException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DBChangeMsg;

public class MonitorPageIndexBuilder extends DbPageIndexBuilder {

    @Autowired private RolePropertyDao rolePropertyDao;

    private final static SqlFragmentSource baseQuery;
    private final static SqlFragmentSource queryTables;

    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT id,monitorname,type FROM (");
        sql.append(" SELECT MonitorId as id,name AS monitorname,'DEVICEDATA' AS type FROM DeviceDataMonitor");
        sql.append(" UNION ALL");
        sql.append(" SELECT OutageMonitorId as id,OutageMonitorname AS monitorname,'OUTAGE' AS type FROM OutageMonitor");
        sql.append(" UNION ALL");
        sql.append(" SELECT TamperFlagMonitorId AS id,TamperFlagMonitorname AS monitorname,'TAMPERFLAG' as type FROM TamperFlagMonitor");
        sql.append(" UNION ALL");
        sql.append(" SELECT StatusPointMonitorId AS id,StatusPointMonitorName AS monitorname,'STATUSPOINT' AS type FROM StatusPointMonitor");
        sql.append(" UNION ALL");
        sql.append(" SELECT  MonitorId AS id,Name AS monitorname,'PORTERRESPONSE' AS type FROM PorterResponseMonitor");
        sql.append(" UNION ALL");
        sql.append(" SELECT ValidationMonitorId AS id,ValidationMonitorName AS monitorname,'VALIDATION' AS type FROM ValidationMonitor");
        sql.append(" ) monitors");
        baseQuery = sql;

        sql = new SqlStatementBuilder();
        queryTables = sql;
    }

    protected MonitorPageIndexBuilder() {
        super("amr", DBChangeMsg.USES_NEW_CATEGORY_ENUM - MONITOR.ordinal(), "Monitor");
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
        return null;
    }

    @Override
    protected Document createDocument(YukonResultSet rs) throws SQLException {
        DocumentBuilder builder = new DocumentBuilder();

        String monitorname = rs.getString("monitorname");
        String type = rs.getString("type");
        int monitorId = rs.getInt("id");
        builder.pageKey(createPageKey(monitorId));
        builder.module("amr");
        if (type.equalsIgnoreCase("DEVICEDATA")) {
            builder.pageName("deviceDataMonitor.VIEW");
            builder.path("/amr/deviceDataMonitor/view?monitorId=" + monitorId);
        } else if (type.equalsIgnoreCase("OUTAGE")) {
            builder.pageName("outageMonitorConfig.EDIT");
            builder.path("/amr/outageProcessing/process/process?outageMonitorId=" + monitorId);
        } else if (type.equalsIgnoreCase("STATUSPOINT")) {
            builder.pageName("statusPointMonitorView");
            builder.path("/amr/statusPointMonitoring/viewPage?statusPointMonitorId=" + monitorId);
        } else if (type.equalsIgnoreCase("TAMPERFLAG")) {
            builder.pageName("tamperFlagEditor.EDIT");
            builder.path("/amr/tamperFlagProcessing/process/process?tamperFlagMonitorId=" + monitorId);
        } else if (type.equalsIgnoreCase("VALIDATION")) {
            builder.pageName("validationEditor.EDIT");
            builder.path("/amr/vee/monitor/edit?validationMonitorId=" + monitorId);
        } else if (type.equalsIgnoreCase("PORTERRESPONSE")) {
            builder.pageName("porterResponseMonitor.VIEW");
            builder.path("/amr/porterResponseMonitor/viewPage?monitorId=" + monitorId);
        }

        builder.pageArgs(monitorname);
        builder.summaryArgs(monitorname);

        return builder.build();
    }

    @Override
    public SqlFragmentSource getWhereClauseForDbChange(int database, String category, int id) {
        return new SqlStatementBuilder("id").eq(id);
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        if (!rolePropertyDao.checkProperty(YukonRoleProperty.DEVICE_DATA_MONITORING, user)
            || !rolePropertyDao.checkProperty(YukonRoleProperty.OUTAGE_PROCESSING, user)
            || !rolePropertyDao.checkProperty(YukonRoleProperty.TAMPER_FLAG_PROCESSING, user)
            || !rolePropertyDao.checkProperty(YukonRoleProperty.STATUS_POINT_MONITORING, user)
            || !rolePropertyDao.checkProperty(YukonRoleProperty.PORTER_RESPONSE_MONITORING, user)
            || !rolePropertyDao.checkProperty(YukonRoleProperty.VALIDATION_ENGINE, user)) {
            return new PrefixQuery(new Term("pageKey", getPageKeyBase()));
        }
        return null;
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user) {
        return true;
    }
}
