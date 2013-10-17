package com.cannontech.common.search.index;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.search.YukonObjectAnalyzer;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;

/**
 * Class which manages Lucene index creation and update for inventory.
 */
public class InventoryIndexManager extends AbstractIndexManager {
    private final Logger log = YukonLogManager.getLogger(InventoryIndexManager.class);

    @Autowired private HardwareUiService hardwareUiService;

    @Override
    public String getIndexName() {
        return "inventory";
    }

    @Override
    protected int getIndexVersion() {
        return 1;
    }

    @Override
    protected Analyzer getAnalyzer() {
        return new YukonObjectAnalyzer();
    }

    @Override
    protected String getDocumentQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(getBaseQuery());
        sql.append(getOrderBy());
        return sql.getSql();
    }

    private SqlStatementBuilder getBaseQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ib.inventoryId, ib.accountId, ib.deviceId, ib.deviceLabel,");
        sql.append(    "lmhw.manufacturerSerialNumber, lmhw.lmHardwareTypeId,");
        sql.append(    "meterTypeId");
        sql.append("from inventoryBase ib");
        sql.append(    "left join lmHardwareBase lmhw on lmhw.inventoryId = ib.inventoryId");
        sql.append(    "left join meterHardwareBase mhw on mhw.inventoryId = ib.inventoryId");
        sql.append("where ib.inventoryId <> 0");
        return sql;
    }

    private SqlStatementBuilder getOrderBy() {
        return new SqlStatementBuilder("order by ib.inventoryId");
    }

    @Override
    protected String getDocumentCountQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*) from inventoryBase");
        return sql.getSql();
    }

    @Override
    protected Document createDocument(ResultSet rs) throws SQLException {
        Document doc = new Document();

        int inventoryId = rs.getInt("inventoryId");
        doc.add(new Field("inventoryId", Integer.toString(inventoryId), Field.Store.YES, Field.Index.NOT_ANALYZED));
        
        Hardware hardware = hardwareUiService.getHardware(inventoryId);

        int energyCompanyId = hardware.getEnergyCompanyId();
        doc.add(new Field("energyCompanyId", Integer.toString(energyCompanyId), Field.Store.YES, Field.Index.NOT_ANALYZED));

        int accountId = rs.getInt("accountId");
        String accountIdStr = rs.wasNull() ? "" : Integer.toString(accountId);
        doc.add(new Field("accountId", accountIdStr, Field.Store.YES, Field.Index.NOT_ANALYZED));

        int deviceId = rs.getInt("deviceId");
        String deviceIdStr = rs.wasNull() ? "" : Integer.toString(deviceId);
        doc.add(new Field("deviceId", deviceIdStr, Field.Store.YES, Field.Index.NOT_ANALYZED));

        String deviceLabel = rs.getString("deviceLabel");
        doc.add(new Field("deviceLabel", deviceLabel == null ? "" : deviceLabel, Field.Store.YES, Field.Index.ANALYZED));

        String manufacturerSerialNumber = rs.getString("manufacturerSerialNumber");
        doc.add(new Field("manufacturerSerialNumber", manufacturerSerialNumber == null ? "" : manufacturerSerialNumber,
            Field.Store.YES, Field.Index.ANALYZED));

        int lmHardwareTypeId = rs.getInt("lmHardwareTypeId");
        String lmHardwareTypeIdStr = rs.wasNull() ? "" : Integer.toString(lmHardwareTypeId);
        doc.add(new Field("lmHardwareTypeId", lmHardwareTypeIdStr, Field.Store.YES, Field.Index.NOT_ANALYZED));

        String meterNumber = hardware.getMeterNumber();
        doc.add(new Field("meterNumber", meterNumber == null ? "" : meterNumber, Field.Store.YES, Field.Index.ANALYZED));

        int meterTypeId = rs.getInt("meterTypeId");
        String meterTypeIdStr = rs.wasNull() ? "" : Integer.toString(meterTypeId);
        doc.add(new Field("meterTypeId", meterTypeIdStr, Field.Store.YES, Field.Index.NOT_ANALYZED));

        String displayName = hardware.getDisplayName();
        doc.add(new Field("displayName", displayName == null ? "" : displayName, Field.Store.YES, Field.Index.ANALYZED));

        String altTrackingNumber = hardware.getAltTrackingNumber();
        doc.add(new Field("altTrackingNumber", altTrackingNumber == null ? "" : altTrackingNumber, Field.Store.YES,
            Field.Index.ANALYZED));

        return doc;
    }

    @Override
    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category,
            String type) {
        if (log.isDebugEnabled()) {
            log.debug("processDBChange(" + dbChangeType + ", " + id + ", " + database + ", " + category + ", " + type);
        }

        if (database == DBChangeMsg.CHANGE_INVENTORY_DB) {
            if (id == 0) {
                rebuildIndex();
                return null;
            }

            Term term = new Term("inventoryId", Integer.toString(id));
            if (dbChangeType == DbChangeType.DELETE) {
                return new IndexUpdateInfo(null, term);
            }
            List<Document> docList = new ArrayList<Document>();

            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append(getBaseQuery());
            sql.append("AND ib.inventoryId").eq(id);

            docList = jdbcTemplate.query(sql.getSql(), sql.getArguments(), new DocumentMapper());
            return new IndexUpdateInfo(docList, term);
        }

        // Return null if no update is to be done
        return null;
    }
}
