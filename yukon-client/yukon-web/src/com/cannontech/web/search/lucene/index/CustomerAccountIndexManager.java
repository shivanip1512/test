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
 * Class which manages customer account lucene index creation and update.
 */
public class CustomerAccountIndexManager extends SimpleIndexManager {

    private static SqlStatementBuilder documentQuery = new SqlStatementBuilder();
    private static SqlStatementBuilder documentCountQuery  = new SqlStatementBuilder();
   
    static{
        documentQuery.append(getQuerySelect());
        documentQuery.append(getQueryGuts());
        documentQuery.append(getQueryOrderBy());
        
        documentCountQuery.append("SELECT COUNT(*)");
        documentCountQuery.append(getQueryGuts());
    }
    
    private static SqlStatementBuilder getQuerySelect() {
        // TODO:  need to index more fields for search (and include them in search)
        return new SqlStatementBuilder("SELECT CA.AccountId, CA.AccountNumber, ECTAM.EnergyCompanyId");
    }
    
    private static SqlStatementBuilder getQueryGuts() {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("FROM CustomerAccount CA");
        sql.append("  JOIN ECToAccountMapping ECTAM ON ECTAM.AccountId = CA.AccountId");
        return sql;
    }
    
    private static SqlStatementBuilder getQueryOrderBy() {
        return new SqlStatementBuilder("ORDER BY CA.AccountNumber, ECTAM.EnergyCompanyID");
    }

    @Override
    public String getIndexName() {
        return "customerAccount";
    }

    @Override
    protected SqlStatementBuilder getDocumentQuery() {
        return documentQuery;
    }

    @Override
    protected SqlStatementBuilder getDocumentCountQuery() {
        return documentCountQuery;
    }

    @Override
    protected Document createDocument(YukonResultSet rs) throws SQLException {

        Document doc = new Document();

        String accountNumber = rs.getString("AccountNumber");
        int accountIdInt = rs.getInt("AccountId");
        String accountId = Integer.toString(accountIdInt);
        int energyCompanyIdInt = rs.getInt("EnergyCompanyId");
        String energyCompanyId = Integer.toString(energyCompanyIdInt);
        
        doc.add(new Field("all", accountNumber, Field.Store.NO, Field.Index.ANALYZED));
        doc.add(new Field("accountNumber", accountNumber, Field.Store.YES, Field.Index.ANALYZED));
        doc.add(new Field("accountId", accountId, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("energyCompanyId", energyCompanyId, Field.Store.NO, Field.Index.NOT_ANALYZED));

        return doc;
    }

    @Override
    protected IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category) {
        if (database == DBChangeMsg.CHANGE_CUSTOMER_ACCOUNT_DB
               && DBChangeMsg.CAT_CUSTOMER_ACCOUNT.equalsIgnoreCase(category)) {
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
    private IndexUpdateInfo processCustomerAccountChange(int accountId) {

        Term term = new Term("accountId", Integer.toString(accountId));
        List<Document> docList = new ArrayList<Document>();

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append(getQuerySelect());
        sql.append(getQueryGuts());
        sql.append("WHERE CA.AccountId").eq(accountId);
        sql.append(getQueryOrderBy());
        
        docList = jdbcTemplate.query(sql, new DocumentMapper());
        return new IndexUpdateInfo(docList, term);
    }

}
