package com.cannontech.web.search.lucene.index.site;

import static com.cannontech.message.dispatch.message.DBChangeMsg.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.common.pao.PaoIdentifier;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.TypeRowMapper;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DBChangeMsg;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.web.search.lucene.index.AbstractIndexManager.IndexUpdateInfo;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

public class PaoPageIndexBuilder extends DbPageIndexBuilder {
    private final static SqlFragmentSource baseQuery;
    private final static SqlFragmentSource queryTables;
    private final SqlFragmentSource allWhereClause;

    protected interface PaoTypeHandler {
        Set<PaoType> getTypesHandled();
        void buildDocument(DocumentBuilder builder, YukonResultSet rs, PaoIdentifier paoIdentifier) throws SQLException;
        Query userLimitingQuery(LiteYukonUser user);
        boolean isAllowedToView(Document document, LiteYukonUser user, PaoIdentifier paoIdentifier);
    }
    private final List<PaoTypeHandler> handlers;
    private final Map<PaoType, PaoTypeHandler> handlersByType;

    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ypo.paobjectId, ypo.paoName, ypo.type, ypo.category, ypo.paoClass, ypo.description,");
        sql.append(    "d.deviceId, dmg.meterNumber,");
        sql.append(    "dcs.address, rfna.serialNumber");
        baseQuery = sql;

        sql = new SqlStatementBuilder();
        sql.append("from yukonPaobject ypo");
        sql.append(    "left join device d on d.deviceId = ypo.paobjectId");
        sql.append(    "left join deviceMeterGroup dmg on dmg.deviceId = ypo.paobjectId");
        sql.append(    "LEFT JOIN DeviceCarrierSettings dcs ON dcs.deviceid = ypo.paobjectid");
        sql.append(    "LEFT JOIN RFNAddress rfna ON rfna.deviceId = d.deviceId");
        queryTables = sql;
    }

    @Autowired
    public PaoPageIndexBuilder(List<PaoTypeHandler> handlers) {
        super("pao", CHANGE_PAO_DB, null);

        this.handlers = handlers;
        Builder<PaoType, PaoTypeHandler> builder = ImmutableMap.builder();

        Set<PaoType> allHandledTypes = new HashSet<>();
        for (PaoTypeHandler handler : handlers) {
            for (PaoType paoType : handler.getTypesHandled()) {
                builder.put(paoType, handler);
                allHandledTypes.add(paoType);
            }
        }
        handlersByType = builder.build();

        allWhereClause = new SqlStatementBuilder("ypo.type").in(allHandledTypes);
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

        PaoIdentifier paoIdentifier = rs.getPaoIdentifier("paobjectId", "type");
        int paoId = paoIdentifier.getPaoId();
        PaoType paoType = paoIdentifier.getPaoType();

        builder.pageKey(createPageKey(paoId));

        PaoTypeHandler paoTypeHandler = handlersByType.get(paoType);
        paoTypeHandler.buildDocument(builder, rs, paoIdentifier);

        // Store the paoType for PAOs for easier after the fact decision making.
        builder.dataField("paoType", paoType.name());

        return builder.build();
    }

    @Override
    protected SqlFragmentSource getWhereClauseForDbChange(int database, String category, int id) {
        return new SqlStatementBuilder("ypo.paobjectId").eq(id);
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        List<Query> limitingQueries = new ArrayList<>();
        for (PaoTypeHandler handler : handlers) {
            Query limitingQuery = handler.userLimitingQuery(user);
            if (limitingQuery != null) {
                limitingQueries.add(limitingQuery);
            }
        }

        if (limitingQueries.size() == 0) {
            return null;
        }

        if (limitingQueries.size() == 1) {
            return limitingQueries.get(0);
        }

        // Since this is used with "MUST_NOT", we OR them all together.
        BooleanQuery.Builder retVal = new BooleanQuery.Builder();
        for (Query limitingQuery : limitingQueries) {
            retVal.add(limitingQuery, Occur.SHOULD);
        }

        return retVal.build();
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user) {
        String pageKey = document.get("pageKey");
        int paoId = Integer.parseInt(pageKey.substring(pageKeyBase.length() + 1));
        PaoType paoType = PaoType.valueOf(document.get("paoType"));
        PaoTypeHandler handler = handlersByType.get(paoType);
        return handler.isAllowedToView(document, user, new PaoIdentifier(paoId, paoType));
    }

    @Override
    public IndexUpdateInfo processDBChange(DbChangeType dbChangeType, int id, int database, String category) {
        // If it's a delete, we won't be able to get the type to know if we handle it or not.  That's okay though;
        // we'll just be asking Lucene to delete a non-existent row.
        if (database == DBChangeMsg.CHANGE_PAO_DB && dbChangeType != DbChangeType.DELETE) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("select type from yukonPaobject where paobjectId").eq(id);
            PaoType paoType = jdbcTemplate.queryForObject(sql, TypeRowMapper.PAO_TYPE);
            PaoTypeHandler handler = handlersByType.get(paoType);
            if (handler == null) {
                // We don't handle this particular PAO type.
                return null;
            }
        }
        return super.processDBChange(dbChangeType, id, database, category);
    }
}
