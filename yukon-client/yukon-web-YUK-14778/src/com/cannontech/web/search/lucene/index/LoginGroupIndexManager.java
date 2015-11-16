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

/**
 * Class which manages point device lucene index creation and update.
 */
public class LoginGroupIndexManager extends SimpleIndexManager {
    @Override
    public String getIndexName() {
        return "loginGroup";
    }

    @Override
    protected SqlStatementBuilder getDocumentQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append(getBaseQuery());
        sql.append(getOrderBy());
        return sql;
    }

    @Override
    protected SqlStatementBuilder getDocumentCountQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select count(*)");
        sql.append(getBaseQuery());
        return sql;
    }
    
    private SqlStatementBuilder getBaseQuery() {
        return new SqlStatementBuilder("from yukongroup");
    }
    
    private SqlStatementBuilder getOrderBy() {
        return new SqlStatementBuilder("order by groupname, groupid");
    }

    @Override
    protected Document createDocument(YukonResultSet rs) throws SQLException {

        Document doc = new Document();

        String groupName = rs.getString("groupname");
        int groupIDInt = rs.getInt("groupid");
        String groupID = Integer.toString(groupIDInt);
        
        String all = groupName + " " + groupID;
        doc.add(new Field("group", groupName, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("all", all, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("groupid", groupID, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("groupName", groupName, Field.Store.NO, Field.Index.NOT_ANALYZED));

        return doc;
    }

    @Override
    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category) {
        if (database == DBChangeMsg.CHANGE_YUKON_USER_DB
                && DBChangeMsg.CAT_YUKON_USER_GROUP.equalsIgnoreCase(category)) {
            // login group change msg
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
    private IndexUpdateInfo processGroupChange(int groupId) {

        Term term = new Term("groupid", Integer.toString(groupId));
        List<Document> docList = new ArrayList<Document>();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select *");
        sql.append(getBaseQuery());
        sql.append("WHERE groupid").eq(groupId);
        sql.append(getOrderBy());

        docList = this.jdbcTemplate.query(sql, new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }

}
