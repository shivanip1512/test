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
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.user.UserUtils;

/**
 * Class which manages point device lucene index creation and update.
 */
public class UserIndexManager extends AbstractIndexManager {

    public String getIndexName() {
        return "userPicker";
    }

    protected int getIndexVersion() {
        return 2;
    }

    protected Analyzer getAnalyzer() {
        return new YukonObjectAnalyzer();
    }

    protected String getDocumentQuery() {
        String query = "select * from yukonuser where userid > " + UserUtils.USER_DEFAULT_ID;
        return query;
    }

    protected String getDocumentCountQuery() {
        String query = "select count(*) from yukonuser where userid > " + UserUtils.USER_DEFAULT_ID + " ";
        return query;
    }

    protected Document createDocument(ResultSet rs) throws SQLException {

        Document doc = new Document();

        String userName = rs.getString("username");
        String status = rs.getString("status");
        int userIDInt = rs.getInt("userid");
        String userID = Integer.toString(userIDInt);
        //will not be setting password here; no need for it
        
        String all = userName + " " + userID;
        doc.add(new Field("user", userName, Field.Store.YES, Field.Index.TOKENIZED));
        /*Don't store this; we don't want to display based off of this one*/
        doc.add(new Field("all", all, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("userid", userID, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("status", status, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("userName", userName, Field.Store.NO, Field.Index.UN_TOKENIZED));

        return doc;
    }

    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category, String type) {
        if (database == DBChangeMsg.CHANGE_YUKON_USER_DB
                && ! DBChangeMsg.CAT_YUKON_USER_GROUP.equalsIgnoreCase(category)) {
            // yukon user change msg
            return this.processUserChange(id);
        }

        // Return null if no update is to be done
        return null;
    }

    /**
     * Helper method to process a user change
     * @param userId - Id of changed user
     * @return Index update info for the user change
     */
    private IndexUpdateInfo processUserChange(int userId) {

        Term term = new Term("userid", Integer.toString(userId));
        List<Document> docList = new ArrayList<Document>();

        StringBuffer sql = new StringBuffer(this.getDocumentQuery());
        sql.append(" AND userid = ?");

        docList = this.jdbcTemplate.query(sql.toString(),
                                          new Object[] { userId },
                                          new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }

}
