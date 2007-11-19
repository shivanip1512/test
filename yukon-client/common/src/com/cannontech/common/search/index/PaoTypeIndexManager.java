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
import com.cannontech.message.dispatch.message.DBChangeMsg;

/**
 * Class which manages point device lucene index creation and update.
 */
public class PaoTypeIndexManager extends AbstractIndexManager {

    public String getIndexName() {
        return "paoPicker";
    }

    protected int getIndexVersion() {
        return 2;
    }

    protected Analyzer getAnalyzer() {
        return new YukonObjectAnalyzer();
    }

    protected String getDocumentQuery() {
        String query = "select ypo.*, d.deviceid "
                + " from"
                + "  yukonpaobject ypo "
                + "  left join device d on d.deviceid = ypo.paobjectid ";
        return query;
    }

    protected String getDocumentCountQuery() {
        String query = "select count(*)                                                 "
                + " from                                                                "
                + "     yukonpaobject                                                   ";
        return query;
    }

    protected Document createDocument(ResultSet rs) throws SQLException {

        Document doc = new Document();

        String paoName = rs.getString("paoname");
        String type = rs.getString("type");
        String category = rs.getString("category");
        String paoid = Integer.toString(rs.getInt("paobjectid"));
        String paoClass = rs.getString("paoclass");
        String all = paoName + " " + type + " " + paoid + " " + paoClass + " " + category;
        doc.add(new Field("pao", paoName, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("type", type, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("all", all, Field.Store.YES, Field.Index.TOKENIZED));

        doc.add(new Field("paoid", paoid, Field.Store.YES, Field.Index.UN_TOKENIZED));

        doc.add(new Field("category", category, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("paoclass", paoClass, Field.Store.YES, Field.Index.UN_TOKENIZED));

        String isDeviceVal = rs.getString("deviceId");
        String isDevice = new Boolean(!StringUtils.isEmpty(isDeviceVal)).toString();
        doc.add(new Field("isDevice", isDevice, Field.Store.YES, Field.Index.UN_TOKENIZED));

        return doc;
    }

    protected IndexUpdateInfo processDBChange(int id, int database, String category, String type) {
        if (database == DBChangeMsg.CHANGE_PAO_DB) {
            // Device change msg
            
            if(id == 0) {
                // Bulk dbchange msg - rebuild index;
                this.rebuildIndex();
                return null;
            }
            
            return this.processPaoChange(id);
        }

        // Return null if no update is to be done
        return null;
    }

    /**
     * Helper method to process a pao change
     * @param paoId - Id of changed pao
     * @return Index update info for the pao change
     */
    @SuppressWarnings("unchecked")
    private IndexUpdateInfo processPaoChange(int paoId) {

        Term term = new Term("paoid", Integer.toString(paoId));
        List<Document> docList = new ArrayList<Document>();

        StringBuffer sql = new StringBuffer(this.getDocumentQuery());
        sql.append(" WHERE paobjectid = ?");

        docList = this.jdbcTemplate.query(sql.toString(),
                                          new Object[] { paoId },
                                          new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }

}
