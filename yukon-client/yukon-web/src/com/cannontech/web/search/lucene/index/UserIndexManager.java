package com.cannontech.web.search.lucene.index;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.user.UserUtils;

/**
 * Class which manages point device lucene index creation and update.
 */
public class UserIndexManager extends SimpleIndexManager {
    @Override
    public String getIndexName() {
        return "user";
    }

    @Override
    protected SqlStatementBuilder getDocumentQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append(getQueryGuts());
        sql.append(getOrderBy());
        return sql;
    }

    @Override
    protected SqlStatementBuilder getDocumentCountQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*)");
        sql.append(getQueryGuts());
        return sql;
    }
    
    private SqlStatementBuilder getQueryGuts() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("from yukonuser");
        sql.append("where userid > " + UserUtils.USER_NONE_ID);
        return sql;
    }
    
    private SqlStatementBuilder getOrderBy() {
        return new SqlStatementBuilder("order by status, username, userid");
    }

    @Override
    protected Document createDocument(YukonResultSet rs) throws SQLException {

        Document doc = new Document();

        String userName = rs.getString("username");
        String status = rs.getString("status");
        int userIDInt = rs.getInt("userid");
        String userID = Integer.toString(userIDInt);
        //will not be setting password here; no need for it
        
        String all = userName + " " + userID;
        doc.add(new Field("user", userName, Field.Store.YES, Field.Index.ANALYZED));
        /*Don't store this; we don't want to display based off of this one*/
        doc.add(new Field("all", all, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("userid", userID, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("status", status, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("userName", userName, Field.Store.NO, Field.Index.NOT_ANALYZED));

        return doc;
    }

    @Override
    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category) {
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

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append(getQueryGuts());
        sql.append(" AND userid").eq(userId);
        sql.append(getOrderBy());

        docList = this.jdbcTemplate.query(sql, new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }

}
