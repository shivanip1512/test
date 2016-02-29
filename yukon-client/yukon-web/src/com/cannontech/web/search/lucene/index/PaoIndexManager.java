package com.cannontech.web.search.lucene.index;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.DisplayablePao;
import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.service.PaoLoadingService;
import com.cannontech.database.YukonResultSet;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

/**
 * Class which manages point device lucene index creation and update.
 */
public class PaoIndexManager extends SimpleIndexManager {
    @Autowired private PaoLoadingService paoLoadingService;

    @Override
    public String getIndexName() {
        return "pao";
    }

    @Override
    protected SqlStatementBuilder getDocumentQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(getBaseQuery());
        sql.append(getOrderBy());
        return sql;
    }
    
    private SqlStatementBuilder getBaseQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.paobjectId, ypo.paoName, ypo.type, ypo.category, ypo.paoClass, ypo.description,");
        sql.append(    "d.deviceId, dmg.meterNumber");
        sql.append("from yukonpaobject ypo");
        sql.append(    "left join device d on d.deviceId = ypo.paobjectId");
        sql.append(    "left join deviceMeterGroup dmg on dmg.deviceId = ypo.paobjectId");
        return sql;
    }
    
    private SqlStatementBuilder getOrderBy() {
        return new SqlStatementBuilder("order by ypo.Category, ypo.Type, ypo.PAOName, ypo.PAObjectID");
    }

    @Override
    protected SqlStatementBuilder getDocumentCountQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*)");
        sql.append("from yukonpaobject");
        return sql;
    }

    @Override
    protected Document createDocument(YukonResultSet rs) throws SQLException {
        Document doc = new Document();

        int paoId = rs.getInt("paobjectId");
        String paoIdStr = Integer.toString(paoId);
        doc.add(new Field("paoid", paoIdStr, TYPE_STORED));

        String paoName = rs.getString("paoName");
        doc.add(new TextField("pao", paoName, Field.Store.YES));

        String type = rs.getString("type");
        doc.add(new Field("type", type, TYPE_STORED));

        String category = rs.getString("category");
        String paoClass = rs.getString("paoClass");
        doc.add(new Field("category", category, TYPE_STORED));
        doc.add(new Field("paoclass", paoClass, TYPE_STORED));

        String all = paoName + " " + type + " " + paoIdStr + " " + paoClass + " " + category;
        doc.add(new TextField("all", all, Field.Store.YES));

        String deviceId = rs.getString("deviceId");
        String isDevice = String.valueOf(!StringUtils.isEmpty(deviceId));
        deviceId = deviceId == null ? "" : deviceId;
        doc.add(new Field("deviceId", deviceId, TYPE_STORED));
        doc.add(new Field("isDevice", isDevice, TYPE_NOT_STORED));

        String meterNumber = rs.getString("meternumber");
        String isMeter = String.valueOf(!StringUtils.isEmpty(meterNumber));
        meterNumber = meterNumber == null ? "" : meterNumber;
        doc.add(new TextField("meterNumber", meterNumber, Field.Store.YES));
        doc.add(new Field("isMeter", isMeter, TYPE_NOT_STORED));

        DisplayablePao displayablePao =
                paoLoadingService.getDisplayablePao(new PaoIdentifier(paoId, PaoType.getForDbString(type)));
        doc.add(new TextField("deviceName", displayablePao.getName(), Field.Store.YES));
        return doc;
    }

    @Override
    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category) {
        if (database == DBChangeMsg.CHANGE_PAO_DB) {
            // Device change msg
            
            if(id == 0) {
                // Bulk dbchange msg - rebuild index;
                this.rebuildIndex();
                return null;
            }
            
            return this.processPaoChange(dbChangeType, id);
        }

        // Return null if no update is to be done
        return null;
    }

    /**
     * Helper method to process a pao change
     * @param paoId - Id of changed pao
     * @return Index update info for the pao change
     */
    private IndexUpdateInfo processPaoChange(DbChangeType dbChangeType, int paoId) {

        Term term = new Term("paoid", Integer.toString(paoId));
        if (dbChangeType == DbChangeType.DELETE) {
            return new IndexUpdateInfo(null, term);
        }
        List<Document> docList = new ArrayList<Document>();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(getBaseQuery());
        sql.append("WHERE ypo.paobjectid").eq(paoId);
        sql.append(getOrderBy());

        docList = this.jdbcTemplate.query(sql, new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }

}
