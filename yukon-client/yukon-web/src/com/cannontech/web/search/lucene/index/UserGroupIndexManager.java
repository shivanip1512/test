package com.cannontech.web.search.lucene.index;

import java.sql.SQLException;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.Term;

import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.YukonResultSet;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.Lists;

/**
 * Class which manages point device lucene index creation and update.
 */
public class UserGroupIndexManager extends SimpleIndexManager {
    @Override
    public String getIndexName() {
        return "userGroup";
    }

    @Override
    protected SqlStatementBuilder getDocumentQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM UserGroup");
        sql.append("ORDER BY Name");
        return sql;
    }

    @Override
    protected SqlStatementBuilder getDocumentCountQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM UserGroup");
        return sql;
    }

    @Override
    protected Document createDocument(YukonResultSet rs) throws SQLException {

        Document doc = new Document();

        String userGroupName = rs.getString("name");
        int userGroupId = rs.getInt("userGroupId");
        String userGroupIdStr = Integer.toString(userGroupId);
        //will not be setting password here; no need for it
        
        String all = userGroupName + " " + userGroupIdStr;
        doc.add(new TextField("userGroup", userGroupName, Field.Store.YES));
        /*Don't store this; we don't want to display based off of this one*/
        doc.add(new TextField("all", all, Field.Store.YES));
        doc.add(new Field("userGroupId", userGroupIdStr, TYPE_STORED));
        doc.add(new Field("name", userGroupName, TYPE_NOT_STORED));

        return doc;
    }

    @Override
    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category) {
        if (database == DBChangeMsg.CHANGE_USER_GROUP_DB && DBChangeMsg.CAT_USER_GROUP.equalsIgnoreCase(category)) {
            return this.processUserGroupChange(id);
        }

        // Return null if no update is to be done
        return null;
    }

    /**
     * Helper method to process a user group change
     * @param userGroupId - Id of changed user group
     * @return Index update info for the user group change
     */
    private IndexUpdateInfo processUserGroupChange(int userGroupId) {

        Term term = new Term("userGroupId", Integer.toString(userGroupId));
        List<Document> docList = Lists.newArrayList();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM UserGroup");
        sql.append("WHERE UserGroupId").eq(userGroupId);
        sql.append("ORDER BY Name");

        docList = jdbcTemplate.query(sql, new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }
}
