package com.cannontech.web.search.lucene.index.site;

import static com.cannontech.message.dispatch.message.DBChangeMsg.*;

import java.sql.SQLException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;

public class CustomerAccountPageIndexBuilder extends DbPageIndexBuilder {
    
    @Autowired private RolePropertyDao rolePropertyDao;

    private final static SqlFragmentSource baseQuery;
    private final static SqlFragmentSource queryTables;

    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ca.accountId, ca.accountNumber, ectam.energyCompanyId");
        baseQuery = sql;

        sql = new SqlStatementBuilder();
        sql.append("from customerAccount ca");
        sql.append(    "join ecToAccountMapping ectam on ectam.accountId = ca.accountId");
        queryTables = sql;
    }

    protected CustomerAccountPageIndexBuilder() {
        super("account", CHANGE_CUSTOMER_ACCOUNT_DB, CAT_CUSTOMER_ACCOUNT);
    }

    @Override
    protected SqlFragmentSource getBaseQuery() {
        return baseQuery;
    }

    @Override
    protected SqlFragmentSource getQueryTables() {
        return queryTables;
    }

    @Override
    protected SqlFragmentSource getAllWhereClause() {
        return null;
    }

    @Override
    protected Document createDocument(YukonResultSet rs) throws SQLException {
        DocumentBuilder builder = new DocumentBuilder();

        int accountId = rs.getInt("accountId");
        int ecId = rs.getInt("energyCompanyId");
        builder.pageKey(createPageKey(accountId)).ecId(ecId);

        builder.module("operator");
        builder.pageName("account.VIEW");
        builder.path("/stars/operator/account/view?accountId=" + accountId);

        builder.pageArgs(rs.getString("accountNumber"));
        // TODO:  name, phone number & maybe address of primary contact at least
        builder.summaryArgs();

        return builder.build();
    }

    @Override
    public SqlFragmentSource getWhereClauseForDbChange(int database, String category, int id) {
        return new SqlStatementBuilder("ca.accountId").eq(id);
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        if (!rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_ACCOUNT_SEARCH, user)) {
            return new PrefixQuery(new Term("pageKey", getPageKeyBase()));
        }
        return null;
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user) {
        return true;
    }
}
