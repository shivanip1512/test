package com.cannontech.core.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;

import com.cannontech.common.constants.YukonListEntry;
import com.cannontech.common.constants.YukonListEntryTypes;
import com.cannontech.common.constants.YukonSelectionList;
import com.cannontech.common.constants.YukonSelectionListDefs;
import com.cannontech.common.util.CtiUtilities;
import com.cannontech.core.dao.YukonListDao;
import com.cannontech.util.Validator;

/**
 * @author rneuharth
 *
 */
public final class YukonListDaoImpl implements YukonListEntryTypes, YukonListDao {
    private JdbcTemplate jdbcTemplate;
    private static final String selectYukonListEntriesSql;
    private static final String selectYukonSelectionListSql;
    private static final String selectAllListEntriesSql;
    private static final ParameterizedRowMapper<YukonListEntry> listEntryRowMapper;
    private Map<Integer,YukonListEntry> yukonListEntries;
    private Map<Integer,YukonSelectionList> yukonSelectionLists;
	
    static {

        selectYukonListEntriesSql = "SELECT EntryID,ListID,EntryOrder,EntryText,YukonDefinitionID " + 
                                    "FROM YukonListEntry ORDER BY EntryID,ListID";
        
        selectYukonSelectionListSql = "SELECT ListID,Ordering,SelectionLabel,WhereIsList,ListName,UserUpdateAvailable " + 
                                      "FROM YukonSelectionList " +
                                      "WHERE ListID > " + CtiUtilities.NONE_ZERO_ID;
        
        selectAllListEntriesSql = "SELECT EntryID, ListID, EntryOrder, EntryText, YukonDefinitionID " +
                                  "FROM YukonListEntry " +
                                  "WHERE ListID = ?";
        
        listEntryRowMapper = YukonListDaoImpl.createListEntryRowMapper();
        
    }
    
	/**
	 * Constructor for DaoFactory.getYukonListDao().
	 */
	private YukonListDaoImpl() {
		super();
	}

	public YukonListEntry getYukonListEntry(final int entryId) {
		final YukonListEntry entry = getYukonListEntries().get(Integer.valueOf(entryId));
		if (entry == null ) throw new IllegalStateException("Unable to find YukonListEntry with an ID of " + entryId);
		return entry;
	}
    
    public YukonSelectionList getYukonSelectionList(final int listID) {
        final YukonSelectionList list = getYukonSelectionLists().get(Integer.valueOf(listID));
        if( list == null ) throw new IllegalStateException("Unable to find YukonSelectionList with an ID of " + listID );
        return list;
    }
	
	public Map<Integer,YukonListEntry> getYukonListEntries() {
        synchronized (this) {
            if (yukonListEntries == null) {
                yukonListEntries = initYukonListEntries();
            }
        }
        return yukonListEntries;
	}
    
    public Map<Integer,YukonSelectionList> getYukonSelectionLists() {
        synchronized (this) {
            if (yukonSelectionLists == null) {
                yukonSelectionLists = initYukonSelectionList();
            }
        }
        return yukonSelectionLists;
    }

    private Map<Integer,YukonListEntry> initYukonListEntries() {
        final Map<Integer,YukonListEntry> map = Collections.synchronizedMap(new HashMap<Integer,YukonListEntry>());
        jdbcTemplate.query(selectYukonListEntriesSql, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                final int entryId = rs.getInt("EntryID"); 
                final YukonListEntry entry = new YukonListEntry();
                entry.setEntryID(entryId);
                entry.setListID(rs.getInt("ListID"));
                entry.setEntryOrder(rs.getInt("EntryOrder"));              
                entry.setEntryText(rs.getString("EntryText"));
                entry.setYukonDefID(rs.getInt("YukonDefinitionID"));
                
                map.put(entryId, entry);
            }
        });
        return map;
    }
	
    private Map<Integer,YukonSelectionList> initYukonSelectionList() {
        final Map<Integer,YukonSelectionList> map = Collections.synchronizedMap(new HashMap<Integer,YukonSelectionList>());
        jdbcTemplate.query(selectYukonSelectionListSql, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                final int listId = rs.getInt("ListID");
                final YukonSelectionList selectionList = new YukonSelectionList();
                selectionList.setListID(listId);
                selectionList.setOrdering(rs.getString("Ordering"));
                selectionList.setSelectionLabel(rs.getString("SelectionLabel"));
                selectionList.setWhereIsList(rs.getString("WhereIsList"));
                selectionList.setListName(rs.getString("ListName"));
                selectionList.setUserUpdateAvailable(rs.getString("UserUpdateAvailable"));
                
                List<YukonListEntry> allListEntries = getAllListEntries(selectionList);
                selectionList.setYukonListEntries(allListEntries);
                
                map.put(listId, selectionList);
            }
        });
        return map;
    }
    
	public synchronized void releaseAllConstants() {
		yukonListEntries = null;
		yukonSelectionLists = null;
	}

	private List<YukonListEntry> getAllListEntries(final YukonSelectionList selectionList) {
        final SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(jdbcTemplate.getDataSource());
        final Integer listId = Integer.valueOf(selectionList.getListID());
        String query = selectAllListEntriesSql;
        
        if (selectionList.getOrdering().equalsIgnoreCase("A"))  // Alphabetical order
            query += " ORDER BY EntryText";
        else if (selectionList.getOrdering().equalsIgnoreCase("O")) // Order by "EntryOrder"
            query += " ORDER BY EntryOrder";
        else
            query += " ORDER BY EntryID";
        
        List<YukonListEntry> entryList = simpleJdbcTemplate.query(query, listEntryRowMapper, listId);
        return entryList;
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonListDao#isListEntryValid(int, java.lang.String)
     */
	public boolean isListEntryValid( int entryID_, String entry_ )
	{
		switch( entryID_ )
		{
			case YukonListEntryTypes.YUK_DEF_ID_EMAIL:
				return Validator.isEmailAddress( entry_ );
				 
			case YukonListEntryTypes.YUK_DEF_ID_PHONE:
				return Validator.isPhoneNumber( entry_ );
			
			case YukonListEntryTypes.YUK_DEF_ID_PIN:
				return Validator.isNumber( entry_ );
			
			
			default: //what is this?? Must be good!!
				return true;
		}

	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonListDao#areSameInYukon(int, int)
     */
	public boolean areSameInYukon(int entryID1, int entryID2) {
		YukonListEntry entry1 = getYukonListEntry( entryID1 );
		YukonListEntry entry2 = getYukonListEntry( entryID2 );
		return (entry1 != null && entry2 != null && entry1.getYukonDefID() == entry2.getYukonDefID());
	}

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonListDao#isPhoneNumber(int)
     */
	public boolean isPhoneNumber( int listEntryID )
	{
	    return(
	         listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_PHONE
	         || listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_HOME_PHONE
	         || listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_WORK_PHONE
	         || listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_CELL_PHONE );
	}

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonListDao#isEmail(int)
     */
    public boolean isEmail( int listEntryID )
    {
        return
             listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL;
    }

    /* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonListDao#isShortEmail(int)
     */
    public boolean isShortEmail( int listEntryID )
    {
        return
             listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL_CELL
             || listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_EMAIL_PAGER;
    }

	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonListDao#isPIN(int)
     */
	public boolean isPIN( int listEntryID )
	{
		return
			 listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_PIN;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonListDao#isFax(int)
     */
	public boolean isFax( int listEntryID )
	{
		return
			 listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_FAX;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonListDao#isPager(int)
     */
	public boolean isPager( int listEntryID )
	{
		return false;
			 //listEntryID == YukonListEntryTypes.YUK_ENTRY_ID_PAGER;
	}	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonListDao#getYukonListName(int)
     */
	public String getYukonListName(int yukonDefID) {
		if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_SETTLEMENT_HECO)
			return YukonSelectionListDefs.YUK_LIST_NAME_SETTLEMENT_TYPE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_RATE_SCHED_J)
			return YukonSelectionListDefs.YUK_LIST_NAME_RATE_SCHEDULE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_SO_FILTER_BY_STATUS)
			return YukonSelectionListDefs.YUK_LIST_NAME_SO_FILTER_BY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_SO_SORT_BY_ORDER_NO)
			return YukonSelectionListDefs.YUK_LIST_NAME_SO_SORT_BY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_SO_SEARCH_BY_ORDER_NO)
			return YukonSelectionListDefs.YUK_LIST_NAME_SO_SEARCH_BY;
		else if (yukonDefID == YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_ENERGYPRO)
			return YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_INV_FILTER_BY_DEV_TYPE)
			return YukonSelectionListDefs.YUK_LIST_NAME_INV_FILTER_BY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_INV_SORT_BY_SERIAL_NO)
			return YukonSelectionListDefs.YUK_LIST_NAME_INV_SORT_BY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_INV_SEARCH_BY_SERIAL_NO)
			return YukonSelectionListDefs.YUK_LIST_NAME_INV_SEARCH_BY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_FAN_STAT_DEFAULT)
			return YukonSelectionListDefs.YUK_LIST_NAME_THERMOSTAT_FAN_STATE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_THERM_MODE_DEFAULT)
			return YukonSelectionListDefs.YUK_LIST_NAME_THERMOSTAT_MODE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_ANS_TYPE_SELECTION)
			return YukonSelectionListDefs.YUK_LIST_NAME_ANSWER_TYPE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_QUE_TYPE_SIGNUP)
			return YukonSelectionListDefs.YUK_LIST_NAME_QUESTION_TYPE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_TOW_WEEKDAY)
			return YukonSelectionListDefs.YUK_LIST_NAME_TIME_OF_WEEK;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_LOC_UNKNOWN)
			return YukonSelectionListDefs.YUK_LIST_NAME_APP_LOCATION;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_MANU_UNKNOWN)
			return YukonSelectionListDefs.YUK_LIST_NAME_MANUFACTURER;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_DEV_STAT_AVAIL)
			return YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_STATUS;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_SEARCH_TYPE_ACCT_NO)
			return YukonSelectionListDefs.YUK_LIST_NAME_SEARCH_TYPE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_SERV_STAT_PENDING)
			return YukonSelectionListDefs.YUK_LIST_NAME_SERVICE_STATUS;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_APP_CAT_DEFAULT)
			return YukonSelectionListDefs.YUK_LIST_NAME_APPLIANCE_CATEGORY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_DEV_TYPE_EXPRESSSTAT)
			return YukonSelectionListDefs.YUK_LIST_NAME_DEVICE_TYPE;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_INV_CAT_ONEWAYREC)
			return YukonSelectionListDefs.YUK_LIST_NAME_INVENTORY_CATEGORY;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_CUST_ACT_SIGNUP)
			return YukonSelectionListDefs.YUK_LIST_NAME_LM_CUSTOMER_ACTION;
		else if (yukonDefID >= YukonListEntryTypes.YUK_DEF_ID_CUST_EVENT_LMPROGRAM)
			return YukonSelectionListDefs.YUK_LIST_NAME_LM_CUSTOMER_EVENT;
		
		return null;
	}
	
	/* (non-Javadoc)
     * @see com.cannontech.core.dao.YukonListDao#getYukonListEntry(com.cannontech.common.constants.YukonSelectionList, java.lang.String)
     */
	public YukonListEntry getYukonListEntry(final YukonSelectionList list, final String entryText) {
        final List<YukonListEntry> entryList = list.getYukonListEntries();
        for (final YukonListEntry entry : entryList) {
            if (entry.getEntryText().equalsIgnoreCase(entryText)) return entry;
        }
        return null;
	}
    
    private static final ParameterizedRowMapper<YukonListEntry> createListEntryRowMapper() {
        final ParameterizedRowMapper<YukonListEntry> rowMapper = new ParameterizedRowMapper<YukonListEntry>() {
            public YukonListEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
                final YukonListEntry entry = new YukonListEntry();
                entry.setEntryID(rs.getInt("EntryID"));
                entry.setListID(rs.getInt("ListID"));
                entry.setEntryOrder(rs.getInt("EntryOrder"));
                entry.setEntryText(rs.getString("EntryText"));
                entry.setYukonDefID(rs.getInt("YukonDefinitionID"));
                return entry;
            }
        };
        return rowMapper;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
	
}
