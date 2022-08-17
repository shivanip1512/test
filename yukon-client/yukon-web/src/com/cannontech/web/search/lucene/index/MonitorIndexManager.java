package com.cannontech.web.search.lucene.index;

import java.sql.SQLException;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import static com.cannontech.web.search.lucene.index.MonitorTypePrefixEnum.*;

public class MonitorIndexManager extends SimpleIndexManager {
    
    @Override
    public String getIndexName() {
        return "monitor";
    }

    @Override
    protected SqlStatementBuilder getDocumentQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT id, monitorname, type");
        sql.append(getQueryGuts());
        sql.append("order by type, id, monitorname");
        return sql;
    }

    @Override
    protected SqlStatementBuilder getDocumentCountQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT count(*)");
        sql.append(getQueryGuts());
        return sql;
    }

    private SqlStatementBuilder getQueryGuts() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("FROM (");
        sql.append("SELECT MonitorId as id,name AS monitorname,'Device Data' AS type FROM DeviceDataMonitor");
        sql.append("UNION ALL");
        sql.append("SELECT OutageMonitorId as id,OutageMonitorname AS monitorname,'Outage' AS type FROM OutageMonitor");
        sql.append("UNION ALL");
        sql.append("SELECT TamperFlagMonitorId AS id,TamperFlagMonitorname AS monitorname,'Tamper Flag' as type FROM TamperFlagMonitor");
        sql.append("UNION ALL");
        sql.append("SELECT StatusPointMonitorId AS id,StatusPointMonitorName AS monitorname,'Status Point' AS type FROM StatusPointMonitor");
        sql.append("UNION ALL");
        sql.append("SELECT  MonitorId AS id,Name AS monitorname,'Porter Response' AS type FROM PorterResponseMonitor");
        sql.append("UNION ALL");
        sql.append("SELECT ValidationMonitorId AS id,ValidationMonitorName AS monitorname,'Validation' AS type FROM ValidationMonitor");
        sql.append(") monitors");
        return sql;
    }
    
    @Override
    protected Document createDocument(YukonResultSet rs) throws SQLException {
        Document doc = new Document();
        
        String monitorName = rs.getString("monitorname");
        int id = rs.getInt("id");
        String type = rs.getString("type");
        String concatId = "";
        switch (type) {
        case "Device Data":
            concatId = DEVICE_DATA_MONITOR.getPrefix().concat(Integer.toString(id));
            break;
        case "Outage":
            concatId = OUTAGE_MONITOR.getPrefix().concat(Integer.toString(id));
            break;
        case "Tamper Flag":
            concatId = TAMPER_FLAG_MONITOR.getPrefix().concat(Integer.toString(id));
            break;
        case "Status Point":
            concatId = STATUS_POINT_MONITOR.getPrefix().concat(Integer.toString(id));
            break;
        case "Porter Response":
            concatId = PORTER_RESPONSE_MONITOR.getPrefix().concat(Integer.toString(id));
            break;
        case "Validation":
            concatId = VALIDATION_MONITOR.getPrefix().concat(Integer.toString(id));
            break;
        default:
            //Non-monitor subscription
        break;
        }
        String all = monitorName + " " + id + " " + type;
        doc.add(new TextField("monitor", monitorName, Field.Store.YES));
        doc.add(new TextField("all", all, Field.Store.YES));
        doc.add(new Field("monitorName", monitorName, TYPE_STORED));
        doc.add(new Field("subId", Integer.toString(id), TYPE_STORED));
        doc.add(new Field("type", type, TYPE_STORED));
        doc.add(new Field("id", concatId, TYPE_STORED));
        
        return doc;
    }

    @Override
    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category) {
        boolean isPresent = DbChangeCategory.getMonitorCategories().stream().filter(
            c -> c.getDbChangeMsgDatabaseId() == database).findFirst().isPresent();
        if (isPresent) {
            rebuildIndex();
        }
        return null;
    }

    @Override
    public synchronized void rebuildIndex() {
        super.rebuildIndex();
    }
}
