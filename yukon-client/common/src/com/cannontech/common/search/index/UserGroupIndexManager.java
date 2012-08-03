package com.cannontech.common.search.index;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.Term;

import com.cannontech.common.search.YukonObjectAnalyzer;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.google.common.collect.Lists;

/**
 * Class which manages point device lucene index creation and update.
 */
public class UserGroupIndexManager extends AbstractIndexManager {

    @Override
    public String getIndexName() {
        return "userGroupPicker";
    }

    @Override
    protected int getIndexVersion() {
        return 2;
    }

    @Override
    protected Analyzer getAnalyzer() {
        return new YukonObjectAnalyzer();
    }

    @Override
    protected String getDocumentQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM UserGroup");
        sql.append("ORDER BY UserGroupName");
        return sql.getSql();
    }

    @Override
    protected String getDocumentCountQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM UserGroup");
        return sql.getSql();
    }

    @Override
    protected Document createDocument(ResultSet rs) throws SQLException {

        Document doc = new Document();

        String userGroupName = rs.getString("userGroupName");
        int userGroupId = rs.getInt("userGroupId");
        String userGroupIdStr = Integer.toString(userGroupId);
        //will not be setting password here; no need for it
        
        String all = userGroupName + " " + userGroupIdStr;
        doc.add(new Field("userGroup", userGroupName, Field.Store.YES, Field.Index.ANALYZED));
        /*Don't store this; we don't want to display based off of this one*/
        doc.add(new Field("all", all, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("userGroupId", userGroupIdStr, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("userGroupName", userGroupName, Field.Store.NO, Field.Index.NOT_ANALYZED));

        return doc;
    }

    @Override
    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category, String type) {
        if (database == DBChangeMsg.CHANGE_USER_GROUP_DB && ! DBChangeMsg.CAT_USER_GROUP.equalsIgnoreCase(category)) {
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
        sql.append("WHERE UserGroupId = ?");
        sql.append("ORDER BY UserGroupName");

        docList = jdbcTemplate.query(sql.getSql(), new Object[] { userGroupId }, new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }
}
