package com.cannontech.web.search.lucene.index.site;

import static com.cannontech.message.dispatch.message.DBChangeMsg.*;

import java.sql.SQLException;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cannontech.common.inventory.Hardware;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.stars.dr.hardware.service.HardwareUiService;

@Service
public class InventoryPageIndexBuilder extends DbPageIndexBuilder {
    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private HardwareUiService hardwareUiService;

    private final static SqlFragmentSource baseQuery;
    private final static SqlFragmentSource queryTables;
    private final static SqlFragmentSource allWhereClause;

    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ib.inventoryId, ib.accountId, ib.deviceId, ib.deviceLabel,");
        sql.append(    "lmhw.manufacturerSerialNumber, lmhw.lmHardwareTypeId, meterTypeId");
        baseQuery = sql;

        sql = new SqlStatementBuilder();
        sql.append("from inventoryBase ib");
        sql.append(    "left join lmHardwareBase lmhw on lmhw.inventoryId = ib.inventoryId");
        sql.append(    "left join meterHardwareBase mhw on mhw.inventoryId = ib.inventoryId");
        queryTables = sql;

        allWhereClause = new SqlStatementBuilder("ib.inventoryId <> 0");
    }

    protected InventoryPageIndexBuilder() {
        super("inventory");
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

        int inventoryId = rs.getInt("inventoryId");
        // TODO:  can we roll this into the query?  It's kinda complicated but would probably be worth it.
        Hardware hardware = hardwareUiService.getHardware(inventoryId);
        int ecId = hardware.getEnergyCompanyId();

        builder.pageKey(createPageKey(inventoryId)).ecId(ecId);

        // results fields
        int accountId = rs.getInt("accountId");
        String pagePart = accountId == 0 ? "inventory" : "hardware";
        builder.module("operator");
        builder.pageName(pagePart + ".VIEW");
        builder.path("/stars/operator/" + pagePart + "/view?" + (accountId == 0 ? "" : "accountId=" + accountId + "&")
            + "inventoryId=" + inventoryId);

        String displayName = hardware.getDisplayName();
        builder.pageArgs(displayName);

        String manufacturerSerialNumber = rs.getString("manufacturerSerialNumber");
        String meterNumber = hardware.getMeterNumber();
        String deviceLabel = rs.getString("deviceLabel");
        String altTrackingNumber = hardware.getAltTrackingNumber();

        builder.summaryArgs(manufacturerSerialNumber, meterNumber, deviceLabel, altTrackingNumber);

        return builder.build();
    }

    @Override
    public SqlFragmentSource getWhereClauseForDbChange(int database, String category, int id) {
        if (database == CHANGE_INVENTORY_DB) {
            SqlStatementBuilder whereClause = new SqlStatementBuilder();
            whereClause.append("ib.inventoryId").eq(id);

            return whereClause;
        }

        return null;
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        BooleanQuery hardwareQuery = null;
        if (!rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES, user)) {
            hardwareQuery = new BooleanQuery();
            hardwareQuery.add(new TermQuery(new Term("module", "operator")), Occur.MUST);
            hardwareQuery.add(new TermQuery(new Term("pageName", "hardware.VIEW")), Occur.MUST);
        }

        BooleanQuery inventoryQuery = null;
        if (!rolePropertyDao.checkRole(YukonRole.INVENTORY, user)) {
            inventoryQuery = new BooleanQuery();
            inventoryQuery.add(new TermQuery(new Term("module", "operator")), Occur.MUST);
            inventoryQuery.add(new TermQuery(new Term("pageName", "inventory.VIEW")), Occur.MUST);
        }

        if (hardwareQuery != null && inventoryQuery != null) {
            BooleanQuery limitingQuery = new BooleanQuery();
            limitingQuery.add(hardwareQuery, Occur.SHOULD);
            limitingQuery.add(inventoryQuery, Occur.SHOULD);
            return limitingQuery;
        } else if (hardwareQuery != null) {
            return hardwareQuery;
        } else if (inventoryQuery != null) {
            return inventoryQuery;
        }

        return null;
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user) {
        return true;
    }
}
