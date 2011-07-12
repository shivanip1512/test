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
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;

/**
 * Class which manages customer account lucene index creation and update.
 */
public class CustomerAccountIndexManager extends AbstractIndexManager {

    public String getIndexName() {
        return "customerAccountPicker";
    }

    protected int getIndexVersion() {
        return 1;
    }

    protected Analyzer getAnalyzer() {
        return new YukonObjectAnalyzer();
    }

    protected String getDocumentQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT *");
        sql.append("FROM CustomerAccount");
        
        return sql.getSql();
    }

    protected String getDocumentCountQuery() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT COUNT(*)");
        sql.append("FROM CustomerAccount");

        return sql.getSql();
    }

    protected Document createDocument(ResultSet rs) throws SQLException {

        Document doc = new Document();

        String accountNumber = rs.getString("AccountNumber");
        int accountIdInt = rs.getInt("AccountId");
        String accountId = Integer.toString(accountIdInt);
        
        String all = accountNumber + " " + accountId;
        doc.add(new Field("customerAccount", accountNumber, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("all", all, Field.Store.YES, Field.Index.TOKENIZED));
        doc.add(new Field("accountNumber", accountNumber, Field.Store.YES, Field.Index.UN_TOKENIZED));
        doc.add(new Field("accountId", accountId, Field.Store.YES, Field.Index.UN_TOKENIZED));

        return doc;
    }

    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category, String type) {
        if (database == DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB 
               && DBChangeMsg.CAT_CUSTOMER_ACCOUNT.equalsIgnoreCase(category) ) {
            // Customer account change msg
            return this.processCustomerAccountChange(id);
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
    private IndexUpdateInfo processCustomerAccountChange(int accountId) {

        Term term = new Term("accountId", Integer.toString(accountId));
        List<Document> docList = new ArrayList<Document>();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(this.getDocumentQuery());
        sql.append("WHERE AccountId").eq(accountId);
        
        docList = this.jdbcTemplate.query(sql.getSql(), sql.getArguments(), new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }

}
