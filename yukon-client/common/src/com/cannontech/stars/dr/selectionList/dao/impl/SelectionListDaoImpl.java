package com.cannontech.stars.dr.selectionList.dao.impl;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.util.IterableUtils;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.database.AdvancedFieldMapper;
import com.cannontech.database.SimpleTableAccessTemplate;
import com.cannontech.database.SqlParameterChildSink;
import com.cannontech.database.YNBoolean;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.incrementer.NextValueHelper;
import com.cannontech.message.DbChangeManager;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.message.dispatch.message.DbChangeType;
import com.cannontech.stars.dr.selectionList.dao.SelectionListDao;

public class SelectionListDaoImpl implements SelectionListDao {
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;
    @Autowired private NextValueHelper nextValueHelper;
    @Autowired private DbChangeManager dbChangeManager;

    private SimpleTableAccessTemplate<YukonSelectionList> listTemplate;
    private final static AdvancedFieldMapper<YukonSelectionList> listFieldMapper =
        new AdvancedFieldMapper<YukonSelectionList>() {
            @Override
            public Number getPrimaryKey(YukonSelectionList list) {
                return list.getListId();
            }

            @Override
            public void setPrimaryKey(YukonSelectionList list, int listId) {
                list.setListId(listId);
            }

            @Override
            public void extractValues(SqlParameterChildSink sink, YukonSelectionList list) {
                sink.addValue("Ordering", list.getOrdering());
                sink.addValueSafe("SelectionLabel", list.getSelectionLabel());
                sink.addValueSafe("WhereIsList", list.getWhereIsList());
                sink.addValue("ListName", list.getType());
                sink.addValue("UserUpdateAvailable",
                              YNBoolean.valueOf(list.isUserUpdateAvailable()));
                sink.addValue("EnergyCompanyId", list.getEnergyCompanyId());
            }
    };

    private SimpleTableAccessTemplate<YukonListEntry> entryTemplate;
    private final static AdvancedFieldMapper<YukonListEntry> entryFieldMapper =
        new AdvancedFieldMapper<YukonListEntry>() {
            @Override
            public Number getPrimaryKey(YukonListEntry entry) {
                return entry.getEntryID();
            }

            @Override
            public void setPrimaryKey(YukonListEntry entry, int entryId) {
                entry.setEntryID(entryId);
            }

            @Override
            public void extractValues(SqlParameterChildSink sink, YukonListEntry entry) {
                sink.addValue("ListId", entry.getListID());
                sink.addValue("EntryOrder", entry.getEntryOrder());
                sink.addValueSafe("EntryText", entry.getEntryText());
                sink.addValue("YukonDefinitionId", entry.getYukonDefID());
            }
        };

    @Override
    @Transactional
    public void saveList(YukonSelectionList list, List<Integer> entryIdsToDelete) {
        listTemplate.save(list);

        for (YukonListEntry entry : list.getYukonListEntries()) {
            entryTemplate.save(entry);
        }

        if (!IterableUtils.isEmpty(entryIdsToDelete)) {
            SqlStatementBuilder sql = new SqlStatementBuilder();
            sql.append("DELETE FROM YukonListEntry WHERE entryId").in(entryIdsToDelete);
            yukonJdbcTemplate.update(sql);
        }

        dbChangeManager.processDbChange(DbChangeType.UPDATE,
                                        DbChangeCategory.YUKON_SELECTION_LIST,
                                        list.getListId());
    }

    @PostConstruct
    public void init() {
        listTemplate =
            new SimpleTableAccessTemplate<YukonSelectionList>(yukonJdbcTemplate, nextValueHelper);
        listTemplate.setTableName("YukonSelectionList");
        listTemplate.setAdvancedFieldMapper(listFieldMapper);
        listTemplate.setPrimaryKeyField("ListId");
        listTemplate.setPrimaryKeyValidOver(0);

        entryTemplate =
            new SimpleTableAccessTemplate<YukonListEntry>(yukonJdbcTemplate, nextValueHelper);
        entryTemplate.setTableName("YukonListEntry");
        entryTemplate.setAdvancedFieldMapper(entryFieldMapper);
        entryTemplate.setPrimaryKeyField("EntryId");
        entryTemplate.setPrimaryKeyValidOver(0);
    }
}
