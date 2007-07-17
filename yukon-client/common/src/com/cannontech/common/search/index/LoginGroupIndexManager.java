package com.cannontech.common.search.index;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;

import com.cannontech.common.search.YukonObjectAnalyzer;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.user.UserUtils;

/**
 * Class which manages point device lucene index creation and update.
 */
public class LoginGroupIndexManager extends AbstractIndexManager {

    public String getIndexName() {
        return "loginGroupPicker";
    }

    protected int getIndexVersion() {
        return 1;
    }

    protected Analyzer getAnalyzer() {
        return new YukonObjectAnalyzer();
    }

    protected String getDocumentQuery() {
        String query = "select *                                                        "
                + " from                                                                "
                + "     yukongroup where                                                "
                + " not groupid = " + UserUtils.USER_ADMIN_ID + " order by groupname    ";
        return query;
    }

    protected String getDocumentCountQuery() {
        String query = "select count(*)                                             "
            + " from                                                                "
            + "     yukongroup where                                                "
            + " not groupid = " + UserUtils.USER_ADMIN_ID + "                       ";
    return query;
    }

    protected Document createDocument(ResultSet rs) throws SQLException {

        Document doc = new Document();

        String groupName = rs.getString("groupname");
        int groupIDInt = rs.getInt("groupid");
        String groupID = Integer.toString(groupIDInt);
        //will not be setting password here; no need for it
        
        String all = groupName + " " + groupID;
        doc.add(new Field("group", groupName, Field.Store.YES, Field.Index.TOKENIZED));
        /*Don't store this; we don't want to display based off of this one*/
        doc.add(new Field("all", all, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("groupid", groupID, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("groupName", groupName, Field.Store.NO, Field.Index.UN_TOKENIZED));

        return doc;
    }

    protected IndexUpdateInfo processDBChange(int id, int database, String category, String type) {
        if (database == DBChangeMsg.CHANGE_YUKON_USER_DB 
               && DBChangeMsg.CAT_YUKON_USER_GROUP.equalsIgnoreCase(category) ) {
            // yukon user change msg
            return this.processGroupChange(id);
        }

        // Return null if no update is to be done
        return null;
    }

    /**
     * Helper method to process a user change
     * @param groupId - Id of changed user
     * @return Index update info for the user change
     */
    @SuppressWarnings("unchecked")
    private IndexUpdateInfo processGroupChange(int groupId) {

        Term term = new Term("groupid", Integer.toString(groupId));
        List<Document> docList = new ArrayList<Document>();

        StringBuffer sql = new StringBuffer(this.getDocumentQuery());
        sql.append(" AND groupid = ?");

        docList = this.jdbcTemplate.query(sql.toString(),
                                          new Object[] { groupId },
                                          new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }

}
