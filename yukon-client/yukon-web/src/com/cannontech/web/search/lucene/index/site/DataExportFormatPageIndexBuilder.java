package com.cannontech.web.search.lucene.index.site;

import static com.cannontech.message.dispatch.message.DbChangeCategory.DATA_EXPORT_FORMAT;

import java.sql.SQLException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.PrefixQuery;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.search.lucene.index.AbstractIndexManager.IndexUpdateInfo;

public class DataExportFormatPageIndexBuilder extends DbPageIndexBuilder {

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private DbChangeManager dbChangeManager;

    private final static SqlFragmentSource baseQuery;
    private final static SqlFragmentSource queryTables;
    private final static SqlFragmentSource allWhereClause;

    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT FormatId, FormatName");
        baseQuery = sql;
        sql = new SqlStatementBuilder();
        sql.append("FROM ArchiveValuesExportFormat ");
        queryTables = sql;
        allWhereClause = null;
    }

    protected DataExportFormatPageIndexBuilder() {
        super("data-exporter", DBChangeMsg.USES_NEW_CATEGORY_ENUM - DATA_EXPORT_FORMAT.ordinal(), "DataExportFormat");
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
        return allWhereClause;
    }

    @Override
    protected Document createDocument(YukonResultSet rs) throws SQLException {
        DocumentBuilder builder = new DocumentBuilder();

        int formatId = rs.getInt("FormatId");

        builder.pageKey(createPageKey(formatId));
        builder.module("tools");
        builder.pageName("bulk.archivedValueExporter.VIEW");
        builder.path("/tools/data-exporter/format/" + formatId);

        builder.pageArgs(rs.getString("FormatId"));
        builder.summaryArgs(rs.getString("FormatName"));

        return builder.build();
    }

    @Override
    public SqlFragmentSource getWhereClauseForDbChange(int database, String category, int id) {
        return new SqlStatementBuilder("FormatId").eq(id);
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        if (!rolePropertyDao.checkProperty(YukonRoleProperty.ARCHIVED_DATA_EXPORT, user)) {
            return new PrefixQuery(new Term("pageKey", getPageKeyBase()));
        }
        return null;
    }

    @Override
    public IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database,
            String category) {
        return super.processDBChange(dbChangeType, id, database, category);
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user) {
        return true;
    }
}
