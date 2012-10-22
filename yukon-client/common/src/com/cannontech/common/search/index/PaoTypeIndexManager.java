package com.cannontech.common.search.index;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;

import com.cannontech.common.search.YukonObjectAnalyzer;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

/**
 * Class which manages point device lucene index creation and update.
 */
public class PaoTypeIndexManager extends AbstractIndexManager {

    public String getIndexName() {
        return "paoPicker";
    }

    protected int getIndexVersion() {
        return 4;
    }

    protected Analyzer getAnalyzer() {
        return new YukonObjectAnalyzer();
    }

    protected String getDocumentQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(getBaseQuery());
        sql.append(getOrderBy());
        return sql.getSql();
    }
    
    private SqlStatementBuilder getBaseQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.*, d.deviceid, dmg.meternumber");
        sql.append("from yukonpaobject ypo");
        sql.append("  left join device d on d.deviceid = ypo.paobjectid");
        sql.append("  left join devicemetergroup dmg on dmg.deviceid = ypo.paobjectid");
        return sql;
    }
    
    private SqlStatementBuilder getOrderBy() {
        return new SqlStatementBuilder("order by ypo.Category, ypo.Type, ypo.PAOName, ypo.PAObjectID");
    }

    protected String getDocumentCountQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*)");
        sql.append("from yukonpaobject");
        return sql.getSql();
    }

    protected Document createDocument(ResultSet rs) throws SQLException {

        Document doc = new Document();

        String paoName = rs.getString("paoname");
        String type = rs.getString("type");
        String category = rs.getString("category");
        String paoid = Integer.toString(rs.getInt("paobjectid"));
        String paoClass = rs.getString("paoclass");
        String all = paoName + " " + type + " " + paoid + " " + paoClass + " " + category;
        doc.add(new Field("pao", paoName, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("type", type, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("all", all, Field.Store.YES, Field.Index.ANALYZED));

        doc.add(new Field("paoid", paoid, Field.Store.YES, Field.Index.NOT_ANALYZED));

        doc.add(new Field("category", category, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("paoclass", paoClass, Field.Store.YES, Field.Index.NOT_ANALYZED));

        String isDeviceVal = rs.getString("deviceId");
        String isDevice = new Boolean(!StringUtils.isEmpty(isDeviceVal)).toString();
        doc.add(new Field("isDevice", isDevice, Field.Store.NO, Field.Index.NOT_ANALYZED));

        String isMeterVal = rs.getString("meternumber");
        String isMeter = new Boolean(!StringUtils.isEmpty(isMeterVal)).toString();
        doc.add(new Field("isMeter", isMeter, Field.Store.NO, Field.Index.NOT_ANALYZED));

        return doc;
    }

    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category, String type) {
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

        docList = this.jdbcTemplate.query(sql.getSql(),
                                          sql.getArguments(),
                                          new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }

}
