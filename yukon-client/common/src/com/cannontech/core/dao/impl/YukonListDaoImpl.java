package com.cannontech.core.dao.impl;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;

import org.jfree.util.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListEnum;
import com.cannontech.common.constants.YukonSelectionListOrder;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.common.util.SqlStatementBuilder;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.core.dao.YukonListEntryRowMapper;
import com.cannontech.core.dynamic.AsyncDynamicDataSource;
import com.cannontech.core.dynamic.DatabaseChangeEventListener;
import com.cannontech.database.YukonJdbcTemplate;
import com.cannontech.database.YukonResultSet;
import com.cannontech.database.YukonRowMapper;
import com.cannontech.message.dispatch.message.DatabaseChangeEvent;
import com.cannontech.message.dispatch.message.DbChangeCategory;
import com.cannontech.stars.energyCompany.model.YukonEnergyCompany;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public final class YukonListDaoImpl implements YukonListEntryTypes, YukonListDao {

    public static class EnergyCompanyAndListName {
        private String listName;
        private int energyCompanyId;

        public EnergyCompanyAndListName(int energyCompanyId, String listName) {
            this.energyCompanyId = energyCompanyId;
            this.listName = listName;
        }

        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + energyCompanyId;
            result = prime * result
                     + ((listName == null) ? 0 : listName.hashCode());
            return result;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            EnergyCompanyAndListName other = (EnergyCompanyAndListName) obj;
            if (energyCompanyId != other.energyCompanyId) {
                return false;
            }
            if (listName == null) {
                if (other.listName != null) {
                    return false;
                }
            } else if (!listName.equals(other.listName)) {
                return false;
            }
            return true;
        }
    }

    @Autowired private AsyncDynamicDataSource asyncDynamicDataSource;
    @Autowired private YukonJdbcTemplate yukonJdbcTemplate;

    // Base Queries
    private SqlStatementBuilder selectYukonListEntriesSql = new SqlStatementBuilder();
    {
        selectYukonListEntriesSql.append("SELECT EntryId, ListId, EntryOrder, EntryText, YukonDefinitionId");
        selectYukonListEntriesSql.append("FROM YukonListEntry");
    }

    private SqlStatementBuilder selectYukonSelectionListSql = new SqlStatementBuilder();
    {
        selectYukonSelectionListSql.append("SELECT ListId, Ordering,SelectionLabel, WhereIsList,");
        selectYukonSelectionListSql.append("       ListName, UserUpdateAvailable, EnergyCompanyId");
        selectYukonSelectionListSql.append("FROM YukonSelectionList");
    }

    // Cache
    private LoadingCache<Integer,YukonListEntry> yukonListEntries;
    private LoadingCache<Integer,YukonSelectionList> yukonSelectionLists;
    private LoadingCache<EnergyCompanyAndListName, YukonSelectionList> yukonSelectionListByName;

    private YukonSelectionList NULL_LIST = new YukonSelectionList();
    private YukonListEntry NULL_ENTRY = new YukonListEntry();

    @PostConstruct
    public void initialize (){
        createCacheObjects();
        createDatabaseChangeListeners();
    }
    
    /**
     * Creates the cached maps that helps add performance for retrieving YukonSelectionLists
     * and YukonListEntries.
     */
    private void createCacheObjects() {
        yukonListEntries = CacheBuilder.newBuilder().concurrencyLevel(1).build(new CacheLoader<Integer, YukonListEntry>() {
            @Override
            public YukonListEntry load(Integer entryId) throws Exception {
                YukonListEntry yukonListEntry = findYukonListEntryFromDb(entryId);

                if (yukonListEntry == null) {
                    return NULL_ENTRY;
                }
                return yukonListEntry;
            }
        });

        yukonSelectionLists = CacheBuilder.newBuilder().concurrencyLevel(1).build(new CacheLoader<Integer, YukonSelectionList>() {
            @Override
            public YukonSelectionList load(Integer listId) {
                YukonSelectionList yukonSelectionList = findYukonSelectionListFromDb(listId);

                if (yukonSelectionList == null) {
                    return NULL_LIST;
                }
                return yukonSelectionList;
            }
        });

        yukonSelectionListByName = CacheBuilder.newBuilder().concurrencyLevel(1).build(new CacheLoader<EnergyCompanyAndListName, YukonSelectionList>() {
            @Override
            public YukonSelectionList load(EnergyCompanyAndListName energyCompanyAndListName) {

                try {
                    return getSelectionListByEnergyCompanyIdAndListName(energyCompanyAndListName.energyCompanyId,
                                                                        energyCompanyAndListName.listName);
                } catch (EmptyResultDataAccessException e) {
                    return NULL_LIST;
                }
            }
        });
    }

    /**
     * This method creates all the needed db change listeners for the cache objects in this class.
     */
    private void createDatabaseChangeListeners(){
        // Adding clearCachesEventListener db listener
        DatabaseChangeEventListener clearCachesEventListener =
            new DatabaseChangeEventListener() {
                @Override
                public void eventReceived(DatabaseChangeEvent event) {
                    // Clearing cached items
                    yukonSelectionListByName.invalidateAll();
                    yukonSelectionLists.invalidateAll();
                    yukonListEntries.invalidateAll();
                }
            };

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.YUKON_SELECTION_LIST,
                                                              clearCachesEventListener);

        asyncDynamicDataSource.addDatabaseChangeEventListener(DbChangeCategory.YUKON_LIST_ENTRY,
                                                              clearCachesEventListener);

    }

	@Override
    public YukonListEntry getYukonListEntry(final int entryId) {
		final YukonListEntry listEntry;
		
		try {
		    listEntry = yukonListEntries.get(entryId);
		} catch (ExecutionException e) {
		    throw new IllegalStateException("Error finding YukonListEntry with an ID of " + entryId, e);
		}
		
		if (listEntry == NULL_ENTRY) {
		    throw new IllegalStateException("Unable to find YukonListEntry with an ID of " + entryId);
		}
		return listEntry;
	}

    @Override
    public YukonSelectionList getYukonSelectionList(final int listId) {
        final YukonSelectionList selectionList;
        try {
            selectionList = yukonSelectionLists.get(listId);
        }catch (ExecutionException e) {
            throw new IllegalStateException("Error finding YukonSelectionList with an ID of " + listId, e);
        }
        
        if (selectionList == NULL_LIST) {
            throw new IllegalStateException("Unable to find YukonSelectionList with an ID of " + listId );
        }
        return selectionList;
    }

    /**
     * This method retrieves the YukonSelectionList associated with the listId.  If a
     * YukonSelectionList entry is not found, this method will return NULL_LIST.
     */
    private YukonSelectionList findYukonSelectionListFromDb(int listId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectYukonSelectionListSql);
        sql.append("WHERE ListId").eq(listId);

        try {
            return yukonJdbcTemplate.queryForObject(sql, new YukonSelectionListRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    /**
     * This method retrieves the YukonListEntry associated with the entryId.  If a
     * YukonListEntry entry is not found, this method will return NULL_ENTRY.
     */
    private YukonListEntry findYukonListEntryFromDb(int entryId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectYukonListEntriesSql);
        sql.append("WHERE EntryId").eq(entryId);

        try {
            return yukonJdbcTemplate.queryForObject(sql, new YukonListEntryRowMapper());
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    @Override
    public List<YukonSelectionList> getSelectionListsByEnergyCompanyId(int energyCompanyId) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectYukonSelectionListSql);
        sql.append("WHERE ListID").gt(CtiUtilities.NONE_ZERO_ID);
        sql.append("AND EnergyCompanyId").eq(energyCompanyId);

        return yukonJdbcTemplate.query(sql, new YukonSelectionListRowMapper());
    }

    @Override
    public YukonSelectionList findSelectionListByEnergyCompanyIdAndListName(int energyCompanyId,
                                                                             String listName) {

        EnergyCompanyAndListName cacheKey = new EnergyCompanyAndListName(energyCompanyId, listName);
        YukonSelectionList selectionList;
        try {
            selectionList = yukonSelectionListByName.get(cacheKey);
        } catch (ExecutionException e) {
            Log.debug("Error retrieving selection list by name.", e);
            return null;
        }
        

        if (selectionList == NULL_LIST) {
            return null;
        }

        return selectionList;
    }

    /**
     * This method returns the YukonSelectionList object associated with the energyCompanyId and
     * listName supplied.  If it does not exist it will throw an EmptyResultDataAccessException.
     */
    private YukonSelectionList getSelectionListByEnergyCompanyIdAndListName(int energyCompanyId,
                                                                            String listName) {

        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectYukonSelectionListSql);
        sql.append("WHERE ListID").gt(CtiUtilities.NONE_ZERO_ID);
        sql.append("AND EnergyCompanyId").eq(energyCompanyId);
        sql.append("AND ListName").eq(listName);

        return yukonJdbcTemplate.queryForObject(sql, new YukonSelectionListRowMapper());
    }

    @Override
    public YukonListEntry getYukonListEntry(final YukonSelectionList list, final String entryText) {
        final List<YukonListEntry> entryList = list.getYukonListEntries();
        for (final YukonListEntry entry : entryList) {
            if (entry.getEntryText().equalsIgnoreCase(entryText)) {
                return entry;
            }
        }
        return null;
    }

    @Override
    public List<YukonListEntry> getYukonListEntry(int yukonDefinitionId, YukonEnergyCompany ec) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.append("SELECT YLE.*");
        sql.append("FROM YukonListEntry YLE");
        sql.append(  "JOIN YukonSelectionList YSL ON YSL.ListID = YLE.ListID");
        sql.append("WHERE YLE.YukonDefinitionID").eq(yukonDefinitionId);
        sql.append(  "AND YSL.EnergyCompanyId").eq(ec.getEnergyCompanyId());

        return yukonJdbcTemplate.query(sql, new YukonListEntryRowMapper());
    }

    // Row Mappers
    private class YukonSelectionListRowMapper implements YukonRowMapper<YukonSelectionList> {

        @Override
        public YukonSelectionList mapRow(YukonResultSet rs) throws SQLException {
            final YukonSelectionList selectionList = new YukonSelectionList();

            selectionList.setListId(rs.getInt("ListId"));
            selectionList.setOrdering(rs.getEnum("Ordering", YukonSelectionListOrder.class));
            selectionList.setSelectionLabel(rs.getString("SelectionLabel"));
            selectionList.setWhereIsList(rs.getString("WhereIsList"));
            selectionList.setType(rs.getEnum("ListName", YukonSelectionListEnum.class));
            selectionList.setUserUpdateAvailable(rs.getBooleanYN("UserUpdateAvailable"));
            selectionList.setEnergyCompanyId(rs.getInt("EnergyCompanyId"));

            List<YukonListEntry> allListEntries = getAllListEntries(selectionList);
            selectionList.setYukonListEntries(allListEntries);

            return selectionList;
        }
    }

    /**
     * This method retrieves all of the yukonListEntries for a selection list, and also handles
     * sorting them.
     */
    private List<YukonListEntry> getAllListEntries(final YukonSelectionList selectionList) {
        SqlStatementBuilder sql = new SqlStatementBuilder();
        sql.appendFragment(selectYukonListEntriesSql);
        sql.append("WHERE ListId").eq(selectionList.getListId());

        if (selectionList.getOrdering() == YukonSelectionListOrder.ALPHABETICAL) {
            sql.append("ORDER BY EntryText");
        } else if (selectionList.getOrdering() == YukonSelectionListOrder.ENTRY_ORDER) {
            sql.append("ORDER BY EntryOrder");
        } else {
            sql.append("ORDER BY EntryId");
        }

        // We're not supporting different orderings anymore so convert them all to entry order.
        List<YukonListEntry> entries = yukonJdbcTemplate.query(sql, new YukonListEntryRowMapper());
        selectionList.setOrdering(YukonSelectionListOrder.ENTRY_ORDER);
        int index = 1;
        for (YukonListEntry entry : entries) {
            entry.setEntryOrder(index++);
        }

        return entries;
    }

}
