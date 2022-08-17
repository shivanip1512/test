package com.cannontech.web.search.lucene.index.site;

import static com.cannontech.common.constants.YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_NON_YUKON_METER;
import static com.cannontech.message.dispatch.message.DBChangeMsg.CHANGE_INVENTORY_DB;

import java.sql.SQLException;

import org.apache.logging.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.springframework.beans.factory.annotation.Autowired;

import com.cannontech.clientutils.YukonLogManager;
import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.inventory.InventoryCategory;
import com.cannontech.common.pao.PaoType;
import com.cannontech.common.util.SqlFragmentSource;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.roleproperties.YukonRole;
import com.cannontech.core.roleproperties.YukonRoleProperty;
import com.cannontech.core.roleproperties.dao.RolePropertyDao;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.data.lite.LiteYukonUser;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.database.cache.StarsDatabaseCache;
import com.cannontech.stars.database.data.lite.LiteStarsEnergyCompany;
import com.cannontech.stars.dr.selectionList.service.SelectionListService;

public class InventoryPageIndexBuilder extends DbPageIndexBuilder {
    
    private final Logger log = YukonLogManager.getLogger(InventoryPageIndexBuilder.class);

    @Autowired private RolePropertyDao rolePropertyDao;
    @Autowired private StarsDatabaseCache starsDatabaseCache;
    @Autowired private SelectionListService selectionListService;

    private final static SqlFragmentSource baseQuery;
    private final static SqlFragmentSource queryTables;
    private final static SqlFragmentSource allWhereClause;

    static {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("select ib.inventoryId, ib.accountId, ib.deviceId, ib.deviceLabel, ib.alternateTrackingNumber,");
        sql.append(    "lmhw.manufacturerSerialNumber, lmhw.lmHardwareTypeId, meterTypeId,");
        sql.append(    "ecm.energyCompanyId, c_yle.yukonDefinitionId, ypo.type as devicePaoType,");
        sql.append(    "ypo.paoName as devicePaoName, lmht_yle.entryText as hwDisplayType,");
        sql.append(    "dmg.meterNumber as deviceMeterNumber, mhw.meterNumber as meterMeterNumber");
        baseQuery = sql;

        sql = new SqlStatementBuilder();
        sql.append("from inventoryBase ib");
        sql.append(    "left join lmHardwareBase lmhw on lmhw.inventoryId = ib.inventoryId");
        sql.append(    "left join meterHardwareBase mhw on mhw.inventoryId = ib.inventoryId");
        sql.append(    "left join ecToInventoryMapping ecm on ecm.inventoryId = ib.inventoryId");
        sql.append(    "left join yukonPaobject ypo on ib.deviceId = ypo.paobjectId");
        sql.append(    "left join yukonListEntry c_yle on c_yle.entryId = ib.categoryId");
        sql.append(    "left join yukonListEntry lmht_yle on lmht_yle.entryId = lmhw.lmHardwareTypeId");
        sql.append(    "left join deviceMeterGroup dmg on dmg.deviceId = ib.deviceId");
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
        int ecId = rs.getInt("energyCompanyId");
        if (log.isTraceEnabled()) {
            log.trace("processing inventory id " + inventoryId + " with ec " + ecId);
        }

        builder.pageKey(createPageKey(inventoryId)).ecId(ecId);

        // results fields
        int accountId = rs.getInt("accountId");
        String pagePart = accountId == 0 ? "inventory" : "hardware";
        builder.module("operator");
        builder.pageName(pagePart + ".VIEW");
        builder.path("/stars/operator/" + pagePart + "/view?" + (accountId == 0 ? "" : "accountId=" + accountId + "&")
            + "inventoryId=" + inventoryId);

        String manufacturerSerialNumber = rs.getString("manufacturerSerialNumber");
        String meterNumber = null;
        String deviceLabel = rs.getString("deviceLabel");
        String altTrackingNumber = rs.getString("alternateTrackingNumber");
        String devicePaoName = rs.getString("devicePaoName");
        LiteStarsEnergyCompany energyCompany = starsDatabaseCache.getEnergyCompany(ecId);

        String displayName;
        String displayType = null;
        InventoryCategory hardwareCategory = InventoryCategory.valueOf(rs.getInt("yukonDefinitionId"));
        if (hardwareCategory == InventoryCategory.YUKON_METER) {
            int deviceId = rs.getInt("deviceId");
            if (deviceId > 0) {
                PaoType devicePaoType = rs.getEnum("devicePaoType", PaoType.class);
                displayType = devicePaoType.getPaoTypeName();
                // devicePaoName should really do what paoLoadingService.getDisplayablePao does somehow.
                displayName = displayType + " " + devicePaoName;
                meterNumber = rs.getString("deviceMeterNumber");
            } else {
                YukonListEntry deviceType = selectionListService.getListEntry(energyCompany, YUK_DEF_ID_DEV_TYPE_NON_YUKON_METER);
                displayType = deviceType.getEntryText();
                displayName = rs.getString("deviceLabel");
            }
        } else if (hardwareCategory == InventoryCategory.NON_YUKON_METER) {
            YukonListEntry mctDeviceType = selectionListService.getListEntry(energyCompany, YUK_DEF_ID_DEV_TYPE_NON_YUKON_METER);
            displayType = mctDeviceType.getEntryText();
            meterNumber = rs.getString("meterMeterNumber");
            displayName = displayType + " " + meterNumber;
        } else {
            // must be a switch or thermostat
            displayType = rs.getString("hwDisplayType");
            displayName = displayType + " " + manufacturerSerialNumber;
        }

        builder.pageArgs(displayName);

        builder.summaryArgs(manufacturerSerialNumber, meterNumber, deviceLabel, altTrackingNumber, displayType);

        return builder.build();
    }

    @Override
    public SqlFragmentSource getWhereClauseForDbChange(int database, String category, int id) {
        return new SqlStatementBuilder("ib.inventoryId").eq(id);
    }

    @Override
    public Query userLimitingQuery(LiteYukonUser user) {
        BooleanQuery.Builder hardwareQuery = null;
        if (!rolePropertyDao.checkProperty(YukonRoleProperty.OPERATOR_CONSUMER_INFO_HARDWARES, user)) {
            hardwareQuery = new BooleanQuery.Builder();
            hardwareQuery.add(new TermQuery(new Term("module", "operator")), Occur.MUST);
            hardwareQuery.add(new TermQuery(new Term("pageName", "hardware.VIEW")), Occur.MUST);
        }

        BooleanQuery.Builder inventoryQuery = null;
        if (!rolePropertyDao.checkRole(YukonRole.INVENTORY, user)) {
            inventoryQuery = new BooleanQuery.Builder();
            inventoryQuery.add(new TermQuery(new Term("module", "operator")), Occur.MUST);
            inventoryQuery.add(new TermQuery(new Term("pageName", "inventory.VIEW")), Occur.MUST);
        }

        if (hardwareQuery != null && inventoryQuery != null) {
            BooleanQuery.Builder limitingQuery = new BooleanQuery.Builder();
            limitingQuery.add(hardwareQuery.build(), Occur.SHOULD);
            limitingQuery.add(inventoryQuery.build(), Occur.SHOULD);
            return limitingQuery.build();
        } else if (hardwareQuery != null) {
            return hardwareQuery.build();
        } else if (inventoryQuery != null) {
            return inventoryQuery.build();
        }

        return null;
    }

    @Override
    public boolean isAllowedToView(Document document, LiteYukonUser user) {
        return true;
    }
    
    @Override
    protected boolean isValidDbChange(DbChangeType dbChangeType, int id, int database, String category) {
        if (database == CHANGE_INVENTORY_DB) {
            return true;
        }
        return false;
    }
}